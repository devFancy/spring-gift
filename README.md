# spring-gift

카카오 선물하기 백엔드 서버를 리팩터링하는 미션. 
Controller 안에 데이터 접근부터 비즈니스 처리까지 한데 뒤섞여 있던 구조를 역할별 패키지로 나눠 정리한다.

---

## 기능 목록 단위

기능 단위로 커밋을 분리한다. `<type>(<scope>): <한국어 제목>` 형식을 사용한다.

- [x] 작업 환경 가이드 - Claude Code 가드레일과 작업 흐름 룰을 저장소에 함께 보관
- [x] 전역 응답/에러 처리 - 모든 API 응답을 같은 모양으로 감싸고, 예외도 같은 흐름으로 처리
- [x] 통합 테스트 공통 환경 구성 - 테스트 실행 전 DB 자동 초기화, 통합 테스트 기반 클래스 신설
- [x] 상품 카탈로그 - 상품 조회/등록/수정/삭제 흐름 정리
- [x] 회원 가입과 로그인 - 회원 가입/로그인 흐름 정리
- [x] 선물 위시리스트 - 위시리스트 담기/삭제 흐름 정리
- [x] API 경로 v1 버전 명시
- [x] 빌드 설정 정리 - Java 전용 프로젝트와 어긋난 빌드 설정 제거
- [x] 카테고리 - 카테고리 관리 흐름 정리, ControllerTest 로 외부 계약 검증 (단순 CRUD 라 ServiceTest 미작성)
- [x] 상품 옵션 - 상품 옵션 관리 흐름 정리, ControllerTest 와 ServiceTest 함께 작성
- [x] 주문 - 주문 생성 흐름 정리, ControllerTest 와 ServiceTest 함께 작성, 카카오 메시지 클라이언트 분리
- [x] 카카오 로그인과 인증 - 카카오 로그인 흐름 정리, 토큰 발급/검증, 인증 처리 분리
- [x] 영속성 객체와 도메인 객체 분리 - 도메인 계층을 순수 Java 클래스로 구성, 값 객체(VO) 도입
- [x] 비밀번호 암호화 - 로그인 시 평문 비교에서 SHA-256 해시 비교로 전환
- [x] 주문 시각을 테스트에서 제어할 수 있도록 전환 - 하드코딩된 현재 시각을 인터페이스로 교체
- [x] 카카오 메시지 전송 실패 로그 기록 - 예외를 무시하던 코드에 warn 레벨 로그 추가
- [x] 상품명 카카오 정책 맥락 분리 - 일반 API와 운영자 화면의 흐름을 명시적으로 분리
- [x] 값 객체를 record로 전환 - 불변성 보장, 생성자에서 형식 검증
- [x] 에러 유형별 로그 수준 추가 - 클라이언트 오류와 서버 오류를 로그 수준으로 구분
- [x] 도메인 모델링 문서화 - 용어 사전과 도메인별 속성/행위/정책을 `docs/gift-domain-modeling.md` 에 정리
- [x] ADR 작성 - 선택지가 둘 이상이었던 설계 결정을 4개 ADR 로 기록

---

## AI 도구 활용 기록

본 프로젝트는 Claude Code 와 함께 진행했다. "코드를 대신 짜 주는" 자유 방임이 아니라 AI 가 일할 작업 환경 자체를 먼저 설계하고 그 안에서 실행하는 방식을 택했다.

가드레일은 세 가지 역할로 나눴다. `CLAUDE.md` 는 프로젝트 컨텍스트, `.claude/rules/` 는 매번 자동 적용되는 제약, `.claude/skills/refactor-step/` 는 필요할 때
호출하는 워크플로우다. 모든 작업은 계획 제안 -> 사용자 승인 -> 실행 순서로 진행하고, 작업이 끝나면 7개 항목을 자가 점검한다.

### 코드를 어떻게 수정했고 무엇을 학습했는지

실제 사용한 프롬프트 패턴은 다음 두 가지가 반복됐다.

- `/refactor-step 주문 흐름을 패키지별로 정리하고 카카오 메시지 클라이언트를 infrastructure/oauth 로 이동` - 변경 한 조각의 범위를 좁혀 실행. 스킬이 단일 목적 확인 -> 범위
  좁히기 -> 적용 -> 검증 -> 커밋 승인 단계를 자동 진행.
- "주문 영역에 작동 보존 테스트 먼저 추가하고, 구조 정리는 별도 커밋으로 분리해" - 단일 목적 분리. 리팩터링과 테스트 추가가 한 커밋에 섞이지 않도록 작동 보존 테스트(test:) -> 구조 정리(
  refactor:) -> 추가 통합 테스트(test:) 3개 커밋으로 분할 적용.

코드 리뷰 과정에서 설계 결정이 더 선명해졌다.

- `ErrorType` 에 `HttpStatus` 를 포함한 결정에 대해 "서비스 계층 순수성이 깨진다"는 리뷰가 왔다. 서비스가 `HttpStatus` 를 직접 꺼내 쓰지 않는다는 점(
  `GlobalExceptionHandler` 한 곳에서만 사용)과 `support/` 가 모든 패키지가 공유하는 위치라는 점을 근거로 정리했다. 이 판단을 ADR-004 로 기록.
- `vo/` 서브패키지 분리를 같은 패키지에 두는 방향으로 바꾸자는 제안이 왔다. package-private 캡슐화 주장을 검토했을 때 `storage/ProductEntity` 가
  `product.getName().value()` 형태로 VO 에 접근하므로 이 구조에서는 VO 가 public 이어야 함을 확인했다. Eric Evans 의 전술적 패턴 구분을 근거로 분리 유지.
- 카테고리 영역 `ServiceTest` 를 삭제했다. ControllerTest 가 이미 외부 계약과 상태 변화를 모두 검증하고 있어서, 단순 CRUD 인 카테고리는 ServiceTest 가 같은 시나리오를
  반복하는 것이라 판단했다.

AI 가 제시한 사실은 별도 출처 확인 후 채택한다. 검증 없이 받아들이면 코드에 그럴듯한 오류가 남는다. 출처는 `docs/private/research/` 에 별도 문서로 정리한다.

AI 가 작성한 코드는 초안이며, 설계와 검증의 책임은 본인에게 있다. 중간 결과물의 의도하지 않은 변경은 즉시 제거하고, 커밋 직전 git diff 를 사람이 직접 한 번 더 확인한다.

---

## 구현 전략

### 패키지 구성

```
src/main/java/gift/
├── api/           # 진입점 (Controller, DTO)
├── application/   # 서비스 (비즈니스 흐름, 트랜잭션 경계)
├── domain/        # 도메인 규칙, 값 객체, Repository 인터페이스
├── infrastructure/# 외부 시스템 (카카오 OAuth, JWT)
├── storage/       # 영속성 (JPA Entity, Repository 구현체)
├── support/       # 공통 (응답 형식, 에러 타입)
└── config/        # 설정
```

의존 방향: `api -> application -> domain`. `storage` 와 `infrastructure` 는 `domain` 을 참조하며, 역방향은 없다.

### 아키텍처 결정 기록

설계 과정에서 선택지가 둘 이상이었던 결정은 [docs/adr/](docs/adr/) 에 기록했다.

| ADR                                                                                 | 결정 요약                     |
|-------------------------------------------------------------------------------------|---------------------------|
| [ADR-001](docs/adr/0001-adopt-responsibility-based-package-boundaries.md)           | 책임 기반 패키지 경계 채택           |
| [ADR-002](docs/adr/0002-use-integration-tests-as-behavior-preservation-evidence.md) | 작동 보존 증거로 통합 테스트 채택       |
| [ADR-003](docs/adr/0003-separate-domain-object-from-jpa-entity.md)                  | 도메인 객체와 JPA 엔티티 분리        |
| [ADR-004](docs/adr/0004-include-http-status-in-error-type.md)                       | ErrorType 에 HttpStatus 포함 |

---

## API 명세서

모든 REST API 응답은 아래 공통 형식으로 반환한다.

``` json
// 성공
{ "result": "SUCCESS", "data": { }, "error": null }

// 실패
{ "result": "ERROR", "data": null, "error": { "code": "E400", "message": "..." } }
```

인증이 필요한 API 는 요청 헤더에 `Authorization: Bearer {token}` 을 포함한다.

### 회원

| 메서드  | URL                      | 설명             | 인증  |
|------|--------------------------|----------------|-----|
| POST | /api/v1/members/register | 회원 가입          | 불필요 |
| POST | /api/v1/members/login    | 로그인, JWT 토큰 발급 | 불필요 |

### 카카오 로그인

| 메서드 | URL                         | 설명               | 인증  |
|-----|-----------------------------|------------------|-----|
| GET | /api/v1/auth/kakao/login    | 카카오 로그인 페이지로 이동  | 불필요 |
| GET | /api/v1/auth/kakao/callback | 인증 코드 수신, JWT 발급 | 불필요 |

### 상품

| 메서드    | URL                   | 설명             | 인증  |
|--------|-----------------------|----------------|-----|
| GET    | /api/v1/products      | 상품 목록 조회 (페이징) | 불필요 |
| GET    | /api/v1/products/{id} | 상품 단건 조회       | 불필요 |
| POST   | /api/v1/products      | 상품 등록          | JWT |
| PUT    | /api/v1/products/{id} | 상품 수정          | JWT |
| DELETE | /api/v1/products/{id} | 상품 삭제          | JWT |

### 상품 옵션

| 메서드    | URL                                             | 설명       | 인증  |
|--------|-------------------------------------------------|----------|-----|
| GET    | /api/v1/products/{productId}/options            | 옵션 목록 조회 | 불필요 |
| POST   | /api/v1/products/{productId}/options            | 옵션 등록    | JWT |
| DELETE | /api/v1/products/{productId}/options/{optionId} | 옵션 삭제    | JWT |

### 카테고리

| 메서드    | URL                     | 설명         | 인증  |
|--------|-------------------------|------------|-----|
| GET    | /api/v1/categories      | 카테고리 목록 조회 | 불필요 |
| POST   | /api/v1/categories      | 카테고리 등록    | JWT |
| PUT    | /api/v1/categories/{id} | 카테고리 수정    | JWT |
| DELETE | /api/v1/categories/{id} | 카테고리 삭제    | JWT |

### 위시리스트

| 메서드    | URL                 | 설명               | 인증  |
|--------|---------------------|------------------|-----|
| GET    | /api/v1/wishes      | 내 위시리스트 조회 (페이징) | JWT |
| POST   | /api/v1/wishes      | 위시 추가            | JWT |
| DELETE | /api/v1/wishes/{id} | 위시 삭제            | JWT |

### 주문

| 메서드  | URL            | 설명               | 인증  |
|------|----------------|------------------|-----|
| GET  | /api/v1/orders | 내 주문 목록 조회 (페이징) | JWT |
| POST | /api/v1/orders | 주문 생성            | JWT |

운영자 화면(`/admin/products`, `/admin/members`)은 Thymeleaf 기반 서버 렌더링 페이지로, 위 REST API 와 별개로 동작한다.

### API 응답 예시

로그인 성공

``` json
POST /api/v1/members/login
{
  "result": "SUCCESS",
  "data": { "token": "eyJhbGciOiJIUzI1NiJ9..." },
  "error": null
}
```

상품 등록 성공

``` json
POST /api/v1/products
{
  "result": "SUCCESS",
  "data": {
    "id": 1,
    "name": "아이스 아메리카노 T",
    "price": 4500,
    "imageUrl": "https://example.com/image.jpg",
    "categoryId": 1
  },
  "error": null
}
```

에러 응답

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "상품 이름은 15자 이하여야 합니다."
  }
}
```

---

## 카카오 로그인 사용 시 설정 (선택)

카카오 API 애플리케이션 등록은 본인이 직접 처리한다. 등록 후 `application.properties` 의 다음 값을 채운다.

- `kakao.login.client-id`
- `kakao.login.client-secret`
- `kakao.login.redirect-uri` (예: `http://localhost:8080/api/auth/kakao/callback`)

운영 환경에서는 환경 변수로 주입한다. 어드민 키, 액세스 토큰, 클라이언트 시크릿은 저장소나 클라이언트 코드에 절대 포함하지 않는다.

---

## 빌드와 실행

```bash
./gradlew clean build       # 전체 빌드 (테스트 포함)
./gradlew test              # 테스트만 실행
./gradlew bootRun           # 서버 실행
```
