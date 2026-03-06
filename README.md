# Spring Advanced Todo API

JWT 기반 인증과 Todo/Comment 관리 기능을 구현한 **Spring Boot REST API 프로젝트**입니다.

코드 리팩토링(Early Return, Validation 분리),
JPA **N+1 문제 해결**,
**단위 테스트 작성 및 테스트 커버리지 개선**을 중심으로 구현했습니다.

---

# Tech Stack

### Backend

* Java 17
* Spring Boot 3.3
* Spring Data JPA

### Database

* MySQL

### Authentication

* JWT

### Test

* JUnit5
* Mockito

### Build Tool

* Gradle

---

# Architecture

본 프로젝트는 **3 Layer Architecture** 구조로 설계되었습니다.

```
Controller
   ↓
Service
   ↓
Repository
```

각 계층은 다음 역할을 담당합니다.

**Controller**

* HTTP 요청 처리
* Request / Response DTO 관리

**Service**

* 비즈니스 로직 처리

**Repository**

* 데이터베이스 접근

---

# Main Features

## Authentication

* 회원가입
* 로그인
* JWT 토큰 발급
* 인증 기반 API 접근

---

## Todo

* Todo 생성
* Todo 목록 조회
* Todo 단건 조회

---

## Comment

* 댓글 생성
* 댓글 조회
* 관리자 댓글 삭제

---

## Admin API

관리자 권한을 가진 사용자만 접근할 수 있는 API입니다.

```
DELETE /admin/comments/{commentId}
PATCH  /admin/users/{userId}
```

관리자 API 접근 시 다음 정보가 로깅됩니다.

* 사용자 ID
* 요청 시간
* 요청 URL
* 요청 Body
* 응답 Body

---

# JWT Authentication Flow

JWT 인증은 다음 흐름으로 동작합니다.

```
Client
   ↓
JwtFilter
   ↓
JwtAuthHelper
   ↓
Request Attribute 저장
   ↓
ArgumentResolver
   ↓
Controller
```

**JwtFilter**

* 요청을 가로채 JWT 토큰 확인

**JwtAuthHelper**

* 토큰 검증
* Claims 추출
* 권한 검사

**ArgumentResolver**

* 인증 사용자 정보를 `AuthUser` 객체로 변환

---

# N+1 Problem 해결

Todo 목록 조회 시 연관 엔티티 조회로 인해 **N+1 Query 문제**가 발생할 수 있습니다.

이를 해결하기 위해 `@EntityGraph`를 활용하여 연관 엔티티를 한 번에 조회하도록 개선했습니다.

```java
@EntityGraph(attributePaths = {"user"})
Page<Todo> findAll(Pageable pageable);
```

---

# Test

비즈니스 로직과 인증 로직에 대한 **단위 테스트(Unit Test)** 를 작성했습니다.

### Service Test

```
CommentServiceTest
ManagerServiceTest
AuthServiceTest
UserServiceTest
```

### Config Test

```
JwtAuthHelperTest
GlobalExceptionHandlerTest
WebConfigTest
FilterConfigTest
```

---

# Test Coverage

IntelliJ Coverage 기준

```
Class Coverage  : 77%
Method Coverage : 73%
Line Coverage   : 76%
```

### Coverage Result

<img width="717" height="255" src="https://github.com/user-attachments/assets/a6392356-ec7c-4065-831d-ebb7c6788100" />

<img width="1183" height="337" src="https://github.com/user-attachments/assets/27b1a507-cac1-43fa-834c-aaff91ef62b3" />

---

# Troubleshooting

### JWT 의존성 문제

JWT 라이브러리를 `compileOnly`로 설정했을 때 테스트 코드에서
`Claims` 클래스를 인식하지 못하는 문제가 발생했습니다.

기존 설정

```gradle
compileOnly 'io.jsonwebtoken:jjwt-api'
```

해결

```gradle
implementation 'io.jsonwebtoken:jjwt-api'
```

`implementation`으로 변경하여 테스트 클래스패스에서도 JWT API를 사용할 수 있도록 수정했습니다.

---

# How to Run

프로젝트 실행

```bash
./gradlew bootRun
```

---

# API Test

Postman을 사용하여 API 테스트를 진행했습니다.

---

# Project Structure

```
src
 ┣ config
 ┣ domain
 ┃ ┣ auth
 ┃ ┣ todo
 ┃ ┣ comment
 ┃ ┣ user
 ┃ ┗ manager
 ┣ client
 ┗ common
```

---

# Blog (Troubleshooting & Development Log)

프로젝트 개발 과정과 트러블슈팅은 Velog에 정리했습니다.  
[필수기능]https://velog.io/@jiiim_ni/%EB%82%B4%EC%9D%BC%EB%B0%B0%EC%9B%80%EC%BA%A0%ED%94%84-Spring-3%EA%B8%B0-CH-3-%EC%8B%AC%ED%99%94-Spring-%EC%BD%94%EB%93%9C-%EA%B0%9C%EC%84%A0-%EA%B3%BC%EC%A0%9C  
[도전기능]https://velog.io/@jiiim_ni/%EB%82%B4%EC%9D%BC%EB%B0%B0%EC%9B%80%EC%BA%A0%ED%94%84-Spring-3%EA%B8%B0-CH-3-%EC%8B%AC%ED%99%94-Spring-%EC%BD%94%EB%93%9C-%EA%B0%9C%EC%84%A0-%EA%B3%BC%EC%A0%9C-%EB%8F%84%EC%A0%84-%EA%B8%B0%EB%8A%A5  

---

