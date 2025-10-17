# 🍱 한입만조- 배달 플랫폼 백엔드 서버


> **“배달의 민족” ㅣ  “요기요” ㅣ  “쿠팡이츠”** 와 같은 배달 앱을 목표로 개발한 프로젝트입니다.

> 해당 프로젝트에서는 **배달 라이더가 없다고 가정**하고 진행합니다.  
> 주문 요청은 `CUSTOMER` → 가게(`OWNER`)로 전달되며
> 가게가 주문을 수락하고 조리 및 완료 상태를 관리합니다.

---

<img width="300" height="300" alt="logo" src="https://github.com/user-attachments/assets/6791a94e-b523-475c-a85a-c3e2489e34b3" />
 






##  팀원 소개

| 이름 | 역할 | 주요 담당 |
|------|------|------------|
| **배원진** | 리뷰(Review) | 리뷰 작성/조회/수정/삭제, 평점 통계, 글로벌 예외처리 |
| **민송경** | 주문(Order) | 주문  |
| **서지희** | 결제(Payment) | 결제 및 거래 승인, 취소 |
| **송준일** | 회원(User) | 회원가입, 로그인, JWT 인증 |
| **노희현** | 가게(Shop) | 가게 등록, 수정, 삭제 및 리뷰와 주문내역 조회 | 
| **남예준** | 상품(Item), AI, 인프라 | 상품 CRUD, AI API, AWS EC2 CI/CD 구축 |

---


##  프로젝트 개요

| 항목 | 내용 |
|------|------|
| **프로젝트명** | Just One Bite |
| **목표** | 광화문 지역 기반 배달 서비스를 위한 백엔드 API 구축 |
| **기간** | 2025.09.26 ~ 2025.10.XX |
| **개발 언어** | Java 21 |
| **프레임워크** | Spring Boot 3.5.6 |
| **데이터베이스** | PostgreSQL |
| **빌드 도구** | Gradle |
| **배포 환경** | AWS EC2, Nginx |
| **CI/CD** | GitHub Actions |
| **버전 관리** | Git & GitHub |

---




## ERD
<img width="2151" height="1232" alt="배달" src="https://github.com/user-attachments/assets/672ca809-e9d7-45a0-ae4c-0ec731a5e12c" />




> - User ↔ Order ↔ Payment ↔ Review ↔ Shop ↔ Item 간 다대일 관계로 구성  
> - Soft Delete 및 Auditing 기반으로 데이터 이력 추적 가능  
> - Review 도메인은 Shop과 직접 연결되어 평균 평점 자동 반영




---




##  기술 스택

| 구분 | 기술 |
|------|------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.6 / Spring Data JPA / Spring Security / **Spring Scheduler** |
| **Database** | PostgreSQL |
| **Build Tool** | Gradle |
| **Infra** | AWS EC2 / Nginx / RDS |
| **CI/CD** | GitHub Actions |
| **Test** | JUnit 5 / Mockito |
| **Docs & Tool** | Swagger / Postman / Notion / Slack |

---


 

## 서비스 실행 방법

###   환경 설정

프로젝트는 `application.yml`을 기반으로 설정됩니다.  
아래 yml은 local환경 실행용 기본 설정입니다.  
(※ 보안을 위해 실제 계정 정보 및 키는 예시 값으로 작성하였습니다.)

```yaml
spring:
  application:
    name: justonebite
  profiles:
    active: local

  datasource:
    url: jdbc:postgresql://localhost:5432/hanip
    username: YourUsername
    password: YourPassword
    driver-class-name: org.postgresql.Driver

gemini:
  api:
    url: https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent
    key: YourGeminiApiKey

jpa:
  database-platform: org.hibernate.dialect.PostgreSQLDialect
  database: postgresql
  hibernate:
    ddl-auto: none
  properties:
    hibernate:
      show_sql: true
      format_sql: true
      use_sql_comments: true

dev:
  seed:
    completed-order:
      enabled: true
      user-email: YourEmail@example.com
      shop-id: 00000000-0000-0000-0000-000000000000
      total-price: 15000

jwt:
  secret:
    key: "YourJwtSecretKeyHere"
```

### 데이터베이스 준비 & 스키마 적용
> JPA ddl-auto: none 이므로 DDL을 직접 적용해야 합니다.


1. PostgreSQL 실행 & DB 생성 (macOs 환경)
  ```bash
  # PostgreSQL 실행
  brew services start postgresql
  
  # DB 접속
  psql -U YourUsername
  
  -- DB 생성 (처음 한 번만 실행)
  CREATE DATABASE hanip;
  \q
```


2) 스키마 파일 배치
 - 아래 제공된 DDL 전체를 파일로 저장합니다.
    - 경로   src/main/resources/db/hanip.sql
     > 제공된 스키마를 그대로 사용하면 됩니다.
     


3) 스키마 적용
   psql -U YourUsername -d hanip -f src/main/resources/db/schema.sql


4) 빌드 & 실행
     ``` bash
     ./gradlew clean build
     java -jar build/libs/justonebite-0.0.1-SNAPSHOT.jar
       ```


##  주요 기능

###  회원 (User)
- 회원가입 / 로그인 (이메일 + JWT)
- 마이페이지 (회원 정보 수정)
- 비밀번호 변경 

###  가게 (Shop)
- 가게 등록 / 조회 / 수정 / 삭제 처리 
- 가게별 리뷰 평점 자동 반영
- 가게별 주문 조회 및 리뷰 조회 
 

### 주문 (Order)
- 주문 생성 / 취소 / 상태 변경  
  (`PENDING` → `ACCEPTED` → `PREPARING` → `DELIVERING` → `COMPLETED`)
- 주문 내역 관리 및 재주문 기능
- 주문 후 5분 이내 취소 제한 로직

###  결제 (Payment)
- 결제 요청 / 승인 / 취소
 -

###  리뷰 (Review)
- 주문 완료 고객만 리뷰 작성 가능
- 리뷰 수정 / 삭제 (Soft Delete)
- 가게 평균 평점 자동 갱신 (Spring Scheduler 기반)
 
   
###  상품 (Item)
- 상품 조회를 제외한 기능은 OWNER, MANAGER, MASTER만 가능
- 상품 수정 / 삭제 (Soft Delete), 상품 숨김 기능
- CUSTOMER와 그 외 역할에 대한 조회 대상 row(숨김 및 soft delete) 구분
- Gemini API를 이용한 AI 상품 설명 기능 제공

###  AI 사용 기록 (AI Request History)
- 상품 생성 및 수정 시 aiGenerated value에 따른 사용 기록 저장
- user 별로 해당 기록 조회

     



