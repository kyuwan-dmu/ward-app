# ward-app

`ward-app`은 프로젝트 분석 및 설계 수업을 위해 만든 소규모 Spring Boot 샘플입니다.
`WARD` 랜딩 페이지 목업을 보여주면서 MySQL에서 식당 데이터를 읽어옵니다.

현재 버전은 다음과 같은 단순 학습 흐름을 목표로 합니다.

- `Controller -> Service -> DAO -> MySQL`
- 정적 메인 페이지 + JSON API
- 데모용 식당 샘플 데이터

## Stack

- Java 21
- Gradle Wrapper 9.4.1
- Spring Boot 4.0.5
- Spring Web MVC
- Spring JDBC
- MySQL 8.x

## 주요 기능

앱을 실행하면 `http://localhost:8080`에서 아래 요소를 확인할 수 있습니다.

- `WARD` 헤더
- 검색창
- 날씨 카드
- 구일역·고척동 일대 지도 형태의 목업
- MySQL에서 불러온 식당 리스트

날씨 카드는 고정 샘플 값을 사용합니다.
식당 리스트는 MySQL `restaurant` 테이블에서 조회합니다.

## 구조

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
  페이지 헤더와 날씨 카드에 쓰이는 고정 데이터를 반환합니다.
- `GET /api/restaurants`  
  MySQL의 식당 데이터를 반환합니다.
- `GET /api/hello`  
  간단한 샘플 GET 엔드포인트입니다.
- `POST /api/messages`  
  POST 예제를 위한 엔드포인트입니다.

## Database Settings

`src/main/resources/application.properties`의 기본 DB 설정:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ward_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=test
spring.datasource.password=test
```

아래 환경 변수로 덮어쓸 수도 있습니다.

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

## EC2 MySQL Setup

목적은 EC2 터미널에서 그대로 복사·붙여넣어 실행하는 것입니다.

참고 파일:

- `scripts/ec2/mysql-setup.sh`
- `docs/ec2-mysql-setup.md`

### 추천: EC2 셸에 직접 붙여넣기

아래 스크립트는 `bash <<'EOF'`부터 마지막 `EOF`까지 한 번에 복사해 붙여넣으면 됩니다.
실제 사용 시 `root` 계정은 로컬 전용으로 두고, DBeaver 같은 원격 툴은 `test` 계정을 사용하도록 열어 둡니다.

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

### 선택: 저장소 스크립트를 직접 실행

```bash
cd ~/ward-app
chmod +x scripts/ec2/mysql-setup.sh
./scripts/ec2/mysql-setup.sh
```

### DBeaver 접속 정보

- Host: EC2 Public IP 또는 Elastic IP
- Port: `3306`
- Database: `ward_app`
- Username: `test`
- Password: `test`

EC2 보안 그룹에서 개인 공인 IP에 대해 `3306` 인바운드를 허용해야 합니다.
스크립트는 `test` 계정 인증 방식을 `mysql_native_password`로 맞춰 DBeaver의 `Public Key Retrieval is not allowed` 오류를 피합니다.

## 실행

```powershell
.\gradlew.bat bootRun
```

메인 페이지:

```text
http://localhost:8080/
```

식당 API:

```text
http://localhost:8080/api/restaurants
```

테스트:

```powershell
.\gradlew.bat test
```

WAR 빌드:

```powershell
.\gradlew.bat bootWar
```
