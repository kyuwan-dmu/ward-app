# EC2 MySQL Setup For `ward-app`

This guide explains how to install MySQL directly inside an EC2 instance and connect `ward-app` to it.

Target assumptions:

- MySQL is installed inside the same EC2 instance
- database name: `ward_app`
- database user: `test`
- database password: `test`
- `ward-app` connects to MySQL through `localhost:3306`
- DBeaver can connect remotely with the `test` account

## Recommended approach

Use direct copy-paste execution in the EC2 terminal.
That avoids issues caused by broken heredoc blocks when creating temporary files manually.

Reference files in this repository:

- `scripts/ec2/mysql-setup.sh`
- `README.md`

## Copy-paste command for EC2

Copy everything from `bash <<'EOF'` to the final `EOF` and paste it into the EC2 terminal in one go.
This version opens MySQL for remote access and allows DBeaver login with `test/test`.

```bash
bash <<'EOF'
set -euo pipefail

echo "[1/6] Installing MySQL server"
sudo apt update
sudo DEBIAN_FRONTEND=noninteractive apt install -y mysql-server

echo "[2/6] Starting and enabling MySQL"
sudo systemctl enable mysql
sudo systemctl restart mysql

echo "[3/6] Configuring MySQL for local and remote access"
sudo sed -i "s/^[#[:space:]]*bind-address[[:space:]]*=.*/bind-address = 0.0.0.0/" /etc/mysql/mysql.conf.d/mysqld.cnf
sudo systemctl restart mysql

if sudo mysql -e "SELECT 1;" >/dev/null 2>&1; then
sudo mysql <<'SQL'
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
SQL
else
mysql -uroot -proot <<'SQL'
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
SQL
fi

echo "[4/6] Creating database and application user"
mysql -uroot -proot <<'SQL'
CREATE DATABASE IF NOT EXISTS ward_app
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'test'@'localhost' IDENTIFIED WITH mysql_native_password BY 'test';
ALTER USER 'test'@'localhost' IDENTIFIED WITH mysql_native_password BY 'test';
GRANT ALL PRIVILEGES ON ward_app.* TO 'test'@'localhost';

CREATE USER IF NOT EXISTS 'test'@'%' IDENTIFIED WITH mysql_native_password BY 'test';
ALTER USER 'test'@'%' IDENTIFIED WITH mysql_native_password BY 'test';
GRANT ALL PRIVILEGES ON ward_app.* TO 'test'@'%';

FLUSH PRIVILEGES;
SQL

echo "[5/6] Creating tables and seed data"
mysql -uroot -proot ward_app <<'SQL'
CREATE TABLE IF NOT EXISTS restaurant (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    summary VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    rating DECIMAL(2, 1) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

TRUNCATE TABLE restaurant;

INSERT INTO restaurant (name, category, summary, address, latitude, longitude, rating) VALUES
('Gocheok Kalguksu', 'Noodles, Dumplings', 'Warm soup-based menu suitable for a lightweight sample project.', '76-173 Gocheok-dong, Guro-gu, Seoul', 37.5009000, 126.8648000, 4.0),
('Guil Bunsik', 'Snacks, Gimbap', 'Simple student-friendly menu for a class demo.', '63-3 Gocheok-dong, Guro-gu, Seoul', 37.4969000, 126.8690000, 4.0),
('Gocheok Donkatsu', 'Donkatsu, Udon', 'Lunch-style mock restaurant near Guil Station.', '49 Gyeongin-ro 43-gil, Guro-gu, Seoul', 37.4976000, 126.8674000, 4.0),
('Anyangcheon Pocha', 'Korean, Pub', 'Sample place used to explain list and marker connections.', '66-41 Gocheok-dong, Guro-gu, Seoul', 37.4987000, 126.8711000, 4.0);
SQL

echo "[6/6] Verifying inserted data"
mysql -utest -ptest ward_app -e "SELECT id, name, category, rating FROM restaurant ORDER BY id;"
mysql -uroot -proot -e "SELECT user, host, plugin FROM mysql.user WHERE user IN ('root', 'test') ORDER BY user, host;"

echo
echo "MySQL setup complete."
echo "DB      : ward_app"
echo "USER    : test"
echo "PASSWORD: test"
echo "REMOTE  : test account allowed from remote hosts"
EOF
```

## Optional: run the repository file directly

If you prefer using the repository file:

```bash
cd ~/ward-app
chmod +x scripts/ec2/mysql-setup.sh
./scripts/ec2/mysql-setup.sh
```

The file is intentionally written so its contents can also be copied directly into the EC2 shell.

## What this setup creates

- MySQL server installation
- `ward_app` database
- `test` / `test` local and remote account access
- `restaurant` table
- sample restaurant rows
- MySQL `bind-address = 0.0.0.0`

## How `ward-app` connects

`ward-app` uses these default properties:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ward_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=test
spring.datasource.password=test
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Since both MySQL and the application are inside the same EC2 instance, `localhost` is correct for this stage.

For remote tools such as DBeaver:

- Host: EC2 public IP
- Port: `3306`
- Database: `ward_app`
- Username: `test`
- Password: `test`

The EC2 security group must allow inbound `3306` from your client IP.
The script forces the `test` account to use `mysql_native_password`, which is usually easier for tools like DBeaver.

## Verify after setup

After MySQL setup, start or redeploy `ward-app` and check:

- `http://localhost:8080/api/restaurants`
- the main page restaurant list

If you later migrate to RDS, only the datasource host needs to change from `localhost` to the RDS endpoint.
