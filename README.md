# ward-app

`ward-app` is a small Spring Boot sample project for a project analysis and design class.
It shows a mock `WARD` landing page and loads restaurant data from MySQL.

The current version focuses on a simple learning flow:

- `Controller -> Service -> DAO -> MySQL`
- static main page + JSON API
- sample restaurant rows for demos

## Stack

- Java 21
- Gradle Wrapper 9.4.1
- Spring Boot 4.0.5
- Spring Web MVC
- Spring JDBC
- MySQL 8.x

## Features

When the app starts, `http://localhost:8080` shows:

- a `WARD` header
- a search box
- a weather card
- a map-like mock section for the Guil Station / Gocheok-dong area
- a restaurant list loaded from MySQL

The weather section uses fixed sample values.
The restaurant list is read from the `restaurant` table in MySQL.

## Structure

```text
ward-app
├─ build.gradle
├─ docs
│  └─ ec2-mysql-setup.md
├─ scripts
│  └─ ec2
│     └─ mysql-setup.sh
├─ src
│  ├─ main
│  │  ├─ java/com/ward/ward_app
│  │  │  ├─ controller
│  │  │  │  └─ HomeController.java
│  │  │  ├─ dao
│  │  │  │  └─ RestaurantDAO.java
│  │  │  ├─ dto
│  │  │  │  └─ MessageRequestDTO.java
│  │  │  ├─ service
│  │  │  │  └─ HomeService.java
│  │  │  ├─ vo
│  │  │  │  ├─ HomeVO.java
│  │  │  │  ├─ MessageVO.java
│  │  │  │  └─ RestaurantVO.java
│  │  │  ├─ ServletInitializer.java
│  │  │  └─ WardAppApplication.java
│  │  └─ resources
│  │     ├─ static
│  │     │  ├─ index.html
│  │     │  ├─ styles.css
│  │     │  └─ app.js
│  │     └─ application.properties
│  └─ test
│     └─ java/com/ward/ward_app
│        └─ WardAppApplicationTests.java
└─ README.md
```

## API

- `GET /api/info`
  Returns fixed data for the page header and weather card.
- `GET /api/restaurants`
  Returns restaurant rows from MySQL.
- `GET /api/hello`
  Simple sample endpoint.
- `POST /api/messages`
  Simple POST example endpoint.

## Database Settings

Default DB settings in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ward_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=test
spring.datasource.password=test
```

You can also override them with environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

## EC2 MySQL Setup

Primary usage is copy-paste execution directly in the EC2 terminal.

Reference files:

- `scripts/ec2/mysql-setup.sh`
- `docs/ec2-mysql-setup.md`

### Recommended: copy-paste directly into the EC2 shell

Copy everything below from `bash <<'EOF'` to the final `EOF` and paste it into the EC2 terminal at once.
This version keeps `root` local-only in practice and opens the `test` account for remote tools such as DBeaver.

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

### Optional: run the repository file directly

```bash
cd ~/ward-app
chmod +x scripts/ec2/mysql-setup.sh
./scripts/ec2/mysql-setup.sh
```

### DBeaver connection values

- Host: EC2 public IP or Elastic IP
- Port: `3306`
- Database: `ward_app`
- Username: `test`
- Password: `test`

You still need the EC2 security group to allow inbound `3306` from your own public IP.
The script sets the `test` account to `mysql_native_password` to avoid the common DBeaver `Public Key Retrieval is not allowed` error.

## Run

```powershell
.\gradlew.bat bootRun
```

Main page:

```text
http://localhost:8080/
```

Restaurant API:

```text
http://localhost:8080/api/restaurants
```

Tests:

```powershell
.\gradlew.bat test
```

WAR build:

```powershell
.\gradlew.bat bootWar
```
