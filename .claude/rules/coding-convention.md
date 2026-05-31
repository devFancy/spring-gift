# coding-convention

spring-gift(Java + Spring Boot) 코드 작성 규칙. 클린 코드 원칙과 도메인 주도 설계 Backend 규칙을 Java 관용구로 재서술했다. 모든 신규 코드와 리팩터링 결과물에 적용한다.

## Global Constraints (depth, length, control flow)

- 들여쓰기 깊이 최대 2단계. 3단계 발생 시 메서드를 분리한다.
- 메서드 길이 최대 15줄. 하나의 일만 한다.
  - 단, 원자적으로 묶인 비즈니스 행위가 분리 시 가독성을 해친다고 판단되면 예외로 둔다.
- `else` 키워드 금지. Early Return 으로 분기한다.
- switch 문 금지. if 문 또는 enum 메서드로 분기한다.
- 3항 연산자(`? :`) 금지. if 문으로 작성한다.

## 포맷팅

- 들여쓰기 4 spaces. 2 spaces 금지.
- 한 줄 최대 120자 권장.
- import 순서: java -> javax/jakarta -> org -> com (Spring 표준).

## 패키지 배치

- 새 클래스/리팩터링 산출물은 CLAUDE.md 의 목표 구조(api/application/domain/infrastructure/storage/support/config) 6계층 안에 배치한다.
- 계층 의존 방향: api -> application -> domain. domain 은 application/api/infrastructure/storage 에 의존하지 않는다.
- domain 패키지에 JPA, Spring 어노테이션 import 금지.

## 트랜잭션 경계

- Service 클래스 상단에 `@Transactional(readOnly = true)` 를 기본 선언한다.
- CUD(Create, Update, Delete) 메서드에만 클래스 기본을 덮어쓰는 `@Transactional` 을 명시한다.
- Controller, Repository, Validator 에 `@Transactional` 부착 금지.

## 검증 분리

- 형식 검증(길이, null, 패턴): `api/validator/` 의 Validator 클래스에서 처리.
- 비즈니스 정책 검증(범위, 단위, 도메인 규칙): `domain/policy/` 의 `*Policy` 에서 전담.
- Service 에서 검증 로직을 직접 작성하지 않는다.

## 값 객체 (VO)

- VO 는 Java `record` 로 작성한다. `equals()`, `hashCode()`, `toString()` 이 자동 제공되고 불변성이 보장된다.
- 검증 로직은 compact constructor 안에 작성한다. 생성 시점에 유효하지 않은 값이 도메인으로 들어오지 못하게 막는다.
- VO 의 접근자는 `value()` 로 통일한다 (record 컴포넌트 이름을 `value` 로 선언).

## DTO 와 도메인 분리

- 레이어 간 결합을 낮추기 위해 용도별로 DTO 를 분리한다. Create, Update, 조회 목적이 다르면 같은 필드여도 별도 클래스로 정의한다.
- DTO 는 도메인별로 하나의 `*Dto.java` 파일 안에 Request/Response 정적 중첩 클래스로 모은다.
- Service 이하 계층은 도메인 객체를 다룬다. JPA Entity 를 Controller 까지 노출하지 않는다.

## 네이밍

- 변수명은 구현 기술(`dto`, `entity`, `result`, `data`) 이 아닌 도메인 의미를 드러낸다.
- 컬렉션은 복수형 명사를 쓴다. `itemList`, `itemArray` 금지. `items` 사용.
- Map 변수는 `{값}By{키}` 패턴으로 인덱스 기준을 명시한다 (예: `productByOptionId`).
- 루프/람다 변수에 `it`, `item` 같은 범용명 대신 도메인 개념을 사용한다.

## API 응답

- 모든 API 응답은 `ApiResponse<T>` 객체로 감싼다. 구조는 `.claude/skills/api-convention.md` 참고.
- 에러는 `support/error/` 의 `ErrorType` 을 참고하여 일관된 코드와 메시지로 반환한다.

## 숫자, 날짜

- 정밀도가 중요한 숫자(금액, 비율, 점수)는 `BigDecimal` 을 사용한다. `double` 금지.
- 클라이언트에 날짜를 표시할 때는 백엔드에서 `String` 으로 포맷팅하여 응답한다. 시간대는 KST 기준.

## 설계 원칙

- 현재 요구사항에 맞게 단순하게 구현한다. 가상의 미래 요구사항을 위해 미리 복잡하게 설계하지 않는다.
- 구조를 개선할 때는 현재 서비스 규모와 팀 맥락에서의 트레이드오프를 먼저 따진다. 교과서적으로 "올바른" 구조보다 지금 상황에서 "적절한" 구조를 우선한다.
- 동일한 개선을 두 번 이상 고민했을 때 적용한다. 한 번의 관찰만으로 추상화하거나 구조를 변경하지 않는다.

## 주석

- 기본은 주석 없음. 식별자 이름이 의미를 설명하면 주석은 빼는 게 맞다.
- 주석은 WHY 가 비자명할 때만 단다. 숨겨진 제약, 미묘한 invariant, 특정 버그 우회 등.
- WHAT 을 설명하지 않는다. "현재 작업/티켓/호출자" 같이 PR 설명문에 들어가야 할 정보는 코드에 박지 않는다.

## 문서 표기

- `**` 굵기는 정말 중요한 키워드 1, 2개에만 사용한다. 한 문서당 최대 1, 2개.
- 표 첫 컬럼을 굵게 처리하지 않는다.
- 금지 특수문자와 대체: `—`, `–` -> `-` / `→` -> `->` / `·` -> `,` 또는 `/` / `…` -> `...`
- 사용자에게 보이는 문구는 "요" 체로 작성한다. AI/개발자 대상 문서는 "다" 체 허용.
- 프로젝트 구조 트리 주석은 패키지/디렉토리 단위까지만 작성한다. 개별 클래스 단위 주석 금지.
