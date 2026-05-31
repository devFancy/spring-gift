# spring-gift

선물하기 서비스 백엔드의 구조를 개선하는 미션입니다. 
Controller 안에 데이터 접근부터 비즈니스 처리까지 한데 뒤섞여 있던 코드를 역할별 패키지로 나눠 변경 난도를 낮추고, 기존 작동을 통합 테스트로 보존했습니다.

---

## 기능 목록 단위

기능 단위로 커밋을 분리합니다. `<type>(<scope>): <한국어 제목>` 형식을 사용합니다.

<details>
<summary>전체 목록 보기</summary>

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
- [x] 포인트, 주문 수량, 주문 메시지를 값 객체로 전환 - 도메인 모델과 코드 일치
- [x] 상품명 카카오 검증 로직 단순화 - 단순 검증은 정책 클래스 없이 서비스에 직접 작성
- [x] 상품 삭제 시 옵션 연관관계를 명시적 삭제로 전환 - @OneToMany 제거, 옵션 삭제 흐름 명시화
- [x] 도메인 모델링 문서화 - 용어 사전과 도메인별 속성/행위/정책을 `docs/gift-domain-modeling.md` 에 정리
- [x] ADR 작성 - 선택지가 둘 이상이었던 설계 결정을 4개 ADR 로 기록
- [x] 도메인 규칙 순수 단위 테스트 추가 - Spring 없이 VO 검증과 도메인 메서드 정책 검증
- [x] ControllerTest를 API 문서화 중심 DocsTest로 전환 - 서비스 Mock 기반 독립 테스트, DB 없이 API 계약 문서화

</details>

---

## AI 도구 활용 기록

Claude Code 와 함께 진행했습니다. "코드를 대신 짜 주는" 방식이 아니라, AI 가 일할 환경(`CLAUDE.md`, `.claude/rules/`, `.claude/skills/`)을 먼저 설계하고 계획 제안 -> 승인 -> 실행 순서로 진행했습니다.

### 반복한 프롬프트 패턴

- 범위 좁히기: `/refactor-step 주문 흐름을 패키지별로 정리하고 카카오 메시지 클라이언트를 infrastructure/oauth 로 이동`
- 단일 목적 분리: "주문 영역에 테스트 먼저 추가하고, 구조 정리는 별도 커밋으로 분리해"

### 코드 리뷰에서 생긴 결정

- `ErrorType` 에 `HttpStatus` 포함 유지. "서비스 계층 순수성이 깨진다"는 리뷰에, 서비스가 `HttpStatus` 를 직접 꺼내 쓰지 않는다는 점과 `support/` 가 전 계층 공유 위치라는 점을 근거로 답변했습니다. ADR-004 로 기록.
- `vo/` 서브패키지 분리 유지. package-private 제안을 검토했을 때 `storage/ProductEntity` 가 VO 에 직접 접근해 `public` 이 강제됨을 확인했습니다. Eric Evans 전술적 패턴 구분을 근거로 유지.
- 카테고리 `ServiceTest` 제거. ControllerTest 가 이미 외부 계약과 상태 변화를 모두 검증하고 있어 중복이라 판단했습니다.

AI 가 제시한 사실은 별도 출처 확인 후 채택하고, 설계와 검증의 책임은 본인에게 있습니다.

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

의존 방향: `api -> application -> domain`. `storage` 와 `infrastructure` 는 `domain` 을 참조하며, 역방향은 없습니다. 멀티 모듈 전환 시 각 패키지가 독립 모듈이 될 수 있도록 순환 의존을 허용하지 않았습니다.

### 도메인 모델링

리팩터링의 출발점은 도메인 언어를 먼저 정의하는 것이었습니다. [용어 사전과 도메인 모델](docs/gift-domain-modeling.md)을 먼저 작성하고, 코드 네이밍과 커밋 메시지까지 같은 언어를 썼습니다. NextStep DDD Serenade 강의의 전략적 설계(용어 사전, 도메인 모델링)와 전술적 설계(값 객체, 엔티티, 도메인 Repository)를 실제 코드에 적용한 결과입니다.

### 설계 의도

- DTO 응집: Kotlin의 data class 처럼 도메인별로 관련 DTO를 한 파일에 모으는 패턴을 Java `record` 정적 중첩 클래스로 구현했습니다. `ProductDto.Request`, `ProductDto.Response` 형태로 파일 수를 줄이고 응집도를 높였습니다.
- 예외 처리 트레이드오프: `ErrorType` 하나에 에러 코드, HTTP 상태, 메시지, 로그 레벨을 모아 핸들러를 단순하게 유지했습니다. 계층 순수성보다 응집성을 우선한 결정이며 [ADR-004](docs/adr/0004-include-http-status-in-error-type.md) 에 근거를 기록했습니다.

### 아키텍처 결정 기록

설계 과정에서 선택지가 둘 이상이었던 결정은 아래와 같이 ADR에 기록했습니다.

| ADR | 결정 요약 |
| --- | --- |
| [ADR-001](docs/adr/0001-adopt-responsibility-based-package-boundaries.md) | 책임 기반 패키지 경계 채택 |
| [ADR-002](docs/adr/0002-use-integration-tests-as-behavior-preservation-evidence.md) | 작동 보존 증거로 통합 테스트 채택 |
| [ADR-003](docs/adr/0003-separate-domain-object-from-jpa-entity.md) | 도메인 객체와 JPA 엔티티 분리 |
| [ADR-004](docs/adr/0004-include-http-status-in-error-type.md) | ErrorType 에 HttpStatus 포함 |

---

## API 명세서

모든 REST API 응답은 아래 공통 형식으로 반환합니다.

``` json
// 성공
{ "result": "SUCCESS", "data": { }, "error": null }

// 실패
{ "result": "ERROR", "data": null, "error": { "code": "E400", "message": "..." } }
```

인증이 필요한 API 는 요청 헤더에 `Authorization: Bearer {token}` 을 포함합니다.

### 회원

| 메서드 | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| POST | /api/v1/members/register | 회원 가입 | 불필요 |
| POST | /api/v1/members/login | 로그인, JWT 토큰 발급 | 불필요 |

### 카카오 로그인

| 메서드 | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| GET | /api/v1/auth/kakao/login | 카카오 로그인 페이지로 이동 | 불필요 |
| GET | /api/v1/auth/kakao/callback | 인증 코드 수신, JWT 발급 | 불필요 |

### 상품

| 메서드 | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| GET | /api/v1/products | 상품 목록 조회 (페이징) | 불필요 |
| GET | /api/v1/products/{id} | 상품 단건 조회 | 불필요 |
| POST | /api/v1/products | 상품 등록 | JWT |
| PUT | /api/v1/products/{id} | 상품 수정 | JWT |
| DELETE | /api/v1/products/{id} | 상품 삭제 | JWT |

### 상품 옵션

| 메서드 | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| GET | /api/v1/products/{productId}/options | 옵션 목록 조회 | 불필요 |
| POST | /api/v1/products/{productId}/options | 옵션 등록 | JWT |
| DELETE | /api/v1/products/{productId}/options/{optionId} | 옵션 삭제 | JWT |

### 카테고리

| 메서드 | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| GET | /api/v1/categories | 카테고리 목록 조회 | 불필요 |
| POST | /api/v1/categories | 카테고리 등록 | JWT |
| PUT | /api/v1/categories/{id} | 카테고리 수정 | JWT |
| DELETE | /api/v1/categories/{id} | 카테고리 삭제 | JWT |

### 위시리스트

| 메서드 | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| GET | /api/v1/wishes | 내 위시리스트 조회 (페이징) | JWT |
| POST | /api/v1/wishes | 위시 추가 | JWT |
| DELETE | /api/v1/wishes/{id} | 위시 삭제 | JWT |

### 주문

| 메서드 | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| GET | /api/v1/orders | 내 주문 목록 조회 (페이징) | JWT |
| POST | /api/v1/orders | 주문 생성 | JWT |

운영자 화면(`/admin/products`, `/admin/members`)은 Thymeleaf 기반 서버 렌더링 페이지로, 위 REST API 와 별개로 동작합니다.

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
    "message": "상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있습니다."
  }
}
```

---

## 카카오 로그인 사용 시 설정 (선택)

카카오 API 애플리케이션 등록은 본인이 직접 처리합니다. 등록 후 `application.properties` 의 다음 값을 채웁니다.

- `kakao.login.client-id`
- `kakao.login.client-secret`
- `kakao.login.redirect-uri` (예: `http://localhost:8080/api/auth/kakao/callback`)

운영 환경에서는 환경 변수로 주입합니다. 어드민 키, 액세스 토큰, 클라이언트 시크릿은 저장소나 클라이언트 코드에 절대 포함하지 않습니다.

---

## 빌드와 실행

```bash
./gradlew clean build       # 전체 빌드 (테스트 포함)
./gradlew test              # 테스트만 실행
./gradlew bootRun           # 서버 실행
```

---

## 소감 (Lessons Learned)

### 잘한 점

요구 사항을 모두 만족하는 걸 목표로 진행했습니다. 거기에 더해 저만의 구현 전략과 AI 활용 능력을 과제의 범위에 맞게 적절하게 트레이드오프를 고려해서 개발을 진행했습니다.

패키지 구성 및 의존 방향을 단방향으로 유지한 점과 용어 사전 및 모델링에 맞게 구현해서 제3자가 봐도 이해하기 쉽도록 신경썼습니다. 리뷰어(진조링)로부터 "도메인 의존성 방향이 단방향으로 잘 유지되고 있다", "개발자가 읽기 좋도록 문서를 제공해주신 점이 인상 깊었다"는 피드백을 받았습니다.

VO 패턴으로 원시값을 포장해 불변성을 확보하고 검증 로직을 응집한 부분에 대해 리뷰어(콩떡아)로부터 "원시값을 포장하여 불변성을 확보하고 검증 로직이 응집되는 좋은 리팩터링이라 생각한다"는 평가를 받았습니다.

코드 리뷰를 통해 스스로는 충분하다고 생각한 설계 결정들이 다른 시각에서 검토됐습니다. `ErrorType` 에 `HttpStatus` 를 넣는 결정, `vo/` 서브패키지 분리 유지 결정 모두 리뷰어의 질문을 계기로 근거가 더 단단해졌습니다.

### 배운 점

ADR 작성 기준에 대해 다시 한번 많이 고려하게 됐습니다. 트레이드오프가 존재할 경우, 규칙이나 정책이 필요할 경우, 테스트 전략이나 검증 방식이 중요한 경우에 ADR 을 작성해서 의사결정이 논리적으로 맞는지 기록하고 추후 개선 시 참고 용도로 활용할 수 있었습니다.

Kotlin 의 DTO 패턴을 Java 의 `record` 정적 중첩 클래스로 적용했습니다. DTO 클래스를 여러 개 만드는 것보다 하나로 관리하는 편이 현재 규모에서는 더 적절하다는 것을 알게됐습니다.

카테고리 `ServiceTest` 를 삭제하는 판단도 리뷰어(진조링)로부터 "겹친다는 생각이 든 것만으로도 삭제해도 좋다고 생각한다"는 동의를 받았습니다. 테스트의 가치를 냉정하게 판단하는 것 자체가 중요한 역량임을 확인했습니다.

주문 완료 후 알림 전송을 하나의 흐름 안에서 처리하되, 알림 실패가 주문 실패로 이어지지 않도록 처리한 부분도 의사결정 과정을 정리하는 계기가 됐습니다.
- 관련 PR [댓글](https://github.com/woowahan-pjs/spring-gift/pull/2#discussion_r3327872673)

에러 처리에 대한 부분도 현재 규모에 맞게 트레이드오프를 고려해 처리했습니다.
- 관련 PR [댓글](https://github.com/woowahan-pjs/spring-gift/pull/2#discussion_r3327879260)

`@Nested` 어노테이션은 중첩 클래스마다 `@BeforeEach` 가 달라야 하거나 테스트 수가 많아 계층 구조가 필요할 때 사용하는 게 적합합니다. 
- 단순 그루핑 목적으로만 쓰면 오히려 들여쓰기 depth 만 늘어납니다. 이번에 통합 테스트와 도메인 테스트 전반에서 `@Nested` 를 제거하며 기준을 직접 확인했습니다.

### 아쉬운 점

도메인 객체와 영속성 객체를 분리했지만, JPA 가 고정된 환경에서 이 구조가 가져오는 복잡도(변환 코드 증가, 파일 수 증가)가 현재 규모에서는 실질적 이점보다 컸습니다. 
분리의 주된 이점인 도메인 단위 테스트는 이후에 직접 작성해서 기술적으로 가능함을 확인했지만, 동일한 테스트는 `@Embeddable` 을 사용하는 구조에서도 작성할 수 있어요. 
분리가 이 이점의 유일한 수단은 아니었다는 점이 아쉽습니다.