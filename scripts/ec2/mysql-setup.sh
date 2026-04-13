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
