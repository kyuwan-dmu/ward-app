# EC2 MySQL Setup For `ward-app`

이 문서는 EC2 서버에 MySQL을 설치하고, `ward-app`이 바로 연결할 수 있도록
데이터베이스, 계정, 테이블, 더미데이터를 한 번에 구성하는 방법을 정리한 문서입니다.

대상 기준:

- MySQL을 EC2 내부에 직접 설치
- DB 이름: `ward_app`
- DB 계정: `test`
- DB 비밀번호: `test`
- `ward-app` 애플리케이션이 같은 EC2 안에서 MySQL에 접속

## 1. 저장소에 포함된 스크립트

프로젝트 안에는 아래 스크립트가 이미 들어 있습니다.

- `scripts/ec2/mysql-setup.sh`

이 스크립트는 다음 작업을 수행합니다.

1. MySQL 서버 설치
2. MySQL 서비스 활성화 및 재시작
3. 로컬 테스트용 root 비밀번호 설정
4. `ward_app` 데이터베이스 생성
5. `test` / `test` 계정 생성 및 권한 부여
6. `restaurant` 테이블 생성
7. 샘플 음식점 데이터 삽입
8. 데이터 삽입 결과 확인

## 2. EC2에서 바로 실행하는 방법

### 방법 A. 저장소 안의 스크립트 실행

EC2에 `ward-app` 저장소가 이미 있다면 아래처럼 실행하면 됩니다.

```bash
cd ~/ward-app
chmod +x scripts/ec2/mysql-setup.sh
./scripts/ec2/mysql-setup.sh
```

### 방법 B. 붙여넣기 한 번으로 실행

아직 저장소를 받기 전이거나, 터미널에서 바로 실행하고 싶다면 아래 내용을 통째로 붙여넣으면 됩니다.

```bash
cat <<'EOF' > mysql-setup.sh
#!/bin/bash
set -euo pipefail

echo "[1/6] Installing MySQL server"
sudo apt update
sudo DEBIAN_FRONTEND=noninteractive apt install -y mysql-server

echo "[2/6] Starting and enabling MySQL"
sudo systemctl enable mysql
sudo systemctl restart mysql

echo "[3/6] Configuring root authentication for local setup"
sudo mysql <<'SQL'
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
SQL

echo "[4/6] Creating database and application user"
mysql -uroot -proot <<'SQL'
CREATE DATABASE IF NOT EXISTS ward_app
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'test'@'localhost' IDENTIFIED BY 'test';
ALTER USER 'test'@'localhost' IDENTIFIED BY 'test';
GRANT ALL PRIVILEGES ON ward_app.* TO 'test'@'localhost';
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

echo
echo "MySQL setup complete."
echo "DB      : ward_app"
echo "USER    : test"
echo "PASSWORD: test"
EOF

chmod +x mysql-setup.sh
./mysql-setup.sh
```

## 3. 생성되는 DB 구조

생성되는 테이블은 아래 하나입니다.

### `restaurant`

- `id`: PK
- `name`: 음식점 이름
- `category`: 음식 카테고리
- `summary`: 간단 설명
- `address`: 주소
- `latitude`: 위도
- `longitude`: 경도
- `rating`: 평점
- `created_at`: 생성 시각

현재는 수업용 목업이므로 테이블을 최소한으로 유지했습니다.

## 4. `ward-app` 연결 정보

`ward-app`은 기본적으로 아래 설정으로 MySQL에 접속합니다.

파일:

- `src/main/resources/application.properties`

기본값:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ward_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=test
spring.datasource.password=test
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

즉, 애플리케이션과 MySQL이 같은 EC2 안에 있다면 별도 수정 없이 바로 연결됩니다.

## 5. 애플리케이션에서 DB를 사용하는 위치

현재 음식점 목록은 아래 흐름으로 DB를 읽습니다.

1. `GET /api/restaurants`
2. `HomeController`
3. `HomeService`
4. `RestaurantDAO`
5. MySQL `restaurant` 테이블 조회

관련 파일:

- `src/main/java/com/ward/ward_app/controller/HomeController.java`
- `src/main/java/com/ward/ward_app/service/HomeService.java`
- `src/main/java/com/ward/ward_app/dao/RestaurantDAO.java`

## 6. 실행 후 확인 방법

MySQL 준비가 끝난 뒤 `ward-app`을 실행하면 아래 주소로 확인할 수 있습니다.

메인 화면:

```text
http://localhost:8080/
```

음식점 API:

```text
http://localhost:8080/api/restaurants
```

MySQL 데이터가 정상이라면 `/api/restaurants`에서 JSON 배열이 내려오고,
메인 화면 목록에도 음식점 데이터가 표시됩니다.

## 7. 주의사항

- 이 문서의 `root/root`, `test/test` 계정은 수업용 예제 기준입니다.
- 실서비스라면 약한 비밀번호를 그대로 쓰면 안 됩니다.
- 현재 스크립트는 로컬 EC2 단독 사용을 전제로 합니다.
- 외부에서 MySQL에 직접 접속할 계획이 없다면 보안그룹에서 `3306` 포트를 열 필요는 없습니다.

## 8. 추천 다음 단계

다음 단계로는 아래 순서를 권장합니다.

1. EC2에서 MySQL 스크립트 실행
2. `ward-app`에서 `./gradlew bootWar`
3. WAR를 Tomcat에 배포
4. `/api/restaurants` 응답 확인
5. 메인 화면에서 목록 표시 확인

필요하면 이후에 이 문서 옆에 `ec2-deploy.md`를 추가해서
Tomcat 배포 절차까지 같이 정리하면 관리가 더 쉬워집니다.
