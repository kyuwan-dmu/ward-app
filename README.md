# ward-app

`ward-app`은 프로젝트분석설계 수업에서 예제로 사용할 수 있도록 만든 간단한 Spring Boot 목업 서비스입니다.
현재 버전은 `WARD`라는 이름의 동네 음식 추천 화면을 중심으로 구성되어 있고, 실행하면 메인 화면에서 고척동/구일역 주변 지도 형태의 화면과 음식점 목록이 바로 보입니다.

이 프로젝트는 완성형 상용 서비스가 아니라, 한 학기 프로젝트를 설명하거나 시연할 때 사용할 수 있는 학습용 예제에 초점을 둡니다.

## 1. 개발 환경

- Java 21
- Gradle Wrapper 9.4.1
- Spring Boot 4.0.5
- Spring Web MVC
- 로컬 실행: Spring Boot 내장 Tomcat
- 외부 배포: Apache Tomcat WAR 배포 가능

## 2. 프로젝트 목적

이 프로젝트의 목적은 다음과 같습니다.

- Spring Boot 기본 프로젝트 구조 익히기
- `Controller -> Service -> VO` 흐름 익히기
- 정적 화면과 JSON API를 함께 구성하는 방법 익히기
- 이후 DB, 회원 기능, 리뷰 기능 등으로 확장할 수 있는 시작점 만들기

즉, 현재 프로젝트는 단순 REST API 실습만을 위한 구조가 아니라, 수업에서 설명하기 쉬운 `화면 + 샘플 API` 예제입니다.

## 3. 현재 구현 내용

기동 후 `http://localhost:8080` 으로 접속하면 다음과 같은 메인 화면이 표시됩니다.

- 상단 브랜드 헤더
- 검색창
- 날씨 정보 영역
- 고척동/구일역 주변을 표현한 지도 목업
- 음식점 목록 카드

실제 지도 API 연동보다는, 시안과 흐름을 보여주기 위한 목업 화면에 가깝습니다.
수업용 예제이므로 구조는 단순하게 유지하고 데이터는 하드코딩된 샘플 데이터를 사용합니다.

## 4. 프로젝트 구조

```text
ward-app
├─ build.gradle
├─ src
│  ├─ main
│  │  ├─ java/com/ward/ward_app
│  │  │  ├─ controller
│  │  │  │  └─ HomeController.java
│  │  │  ├─ service
│  │  │  │  └─ HomeService.java
│  │  │  ├─ dto
│  │  │  │  └─ MessageRequestDTO.java
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

## 5. 구성 설명

### `WardAppApplication`

- Spring Boot 애플리케이션 시작 클래스입니다.

### `ServletInitializer`

- WAR 파일을 외부 Tomcat에 배포할 때 사용하는 초기화 클래스입니다.

### `controller`

- 요청을 받는 계층입니다.
- 현재는 메인 화면에서 사용하는 샘플 API를 제공합니다.

### `service`

- 화면과 API에서 사용할 데이터를 조립하는 계층입니다.
- 현재는 날씨 정보와 음식점 목록을 하드코딩 데이터로 반환합니다.

### `dto`

- 요청 데이터를 담는 객체입니다.
- 현재는 POST 예제용 `MessageRequestDTO`가 있습니다.

### `vo`

- 응답 데이터를 담는 객체입니다.
- `HomeVO`, `MessageVO`, `RestaurantVO`를 사용합니다.

### `static`

- 메인 화면을 구성하는 정적 리소스 경로입니다.
- 현재 프로젝트의 첫 화면은 `static/index.html`을 통해 표시됩니다.

## 6. 현재 API

### `GET /api/info`

메인 화면 상단에 필요한 기본 정보와 날씨 표시용 데이터를 반환합니다.

### `GET /api/restaurants`

음식점 목록 샘플 데이터를 반환합니다.

### `GET /api/hello`

기본 동작 확인용 예제 API입니다.

### `POST /api/messages`

간단한 요청/응답 구조를 보여주기 위한 POST 예제 API입니다.

## 7. 실행 방법

### 로컬 실행

```powershell
.\gradlew.bat bootRun
```

또는 IDE에서 `WardAppApplication`을 실행합니다.

기본 주소:

```text
http://localhost:8080
```

메인 화면:

```text
GET http://localhost:8080/
```

음식점 목록 API:

```text
GET http://localhost:8080/api/restaurants
```

### 테스트 실행

```powershell
.\gradlew.bat test
```

### WAR 파일 생성

```powershell
.\gradlew.bat bootWar
```

생성된 WAR 파일은 외부 Apache Tomcat에 배포할 수 있습니다.

## 8. 학습용 프로젝트로서의 특징

이 프로젝트는 학습용 예제이므로 다음 원칙을 따릅니다.

- 화면은 단순하고 직관적으로 구성
- 계층 구조는 최소한으로 유지
- 데이터는 우선 하드코딩으로 처리
- 나중에 DB 연동이나 로그인 기능으로 확장 가능

처음부터 너무 많은 기능을 넣기보다, 기본 구조를 이해하고 점진적으로 확장하는 데 적합한 예제입니다.

## 9. 앞으로 확장할 수 있는 방향

수업 진행에 따라 아래와 같은 확장이 가능합니다.

- 회원가입 / 로그인
- 리뷰 작성 / 리뷰 조회
- 지역 검색 기능
- 실제 지도 API 연동
- DB 연동을 위한 `repository` 또는 `dao` 계층 추가
- 관리자 화면 또는 상세 음식점 페이지 추가

현재 버전은 그 출발점이 되는 목업 프로젝트입니다.
