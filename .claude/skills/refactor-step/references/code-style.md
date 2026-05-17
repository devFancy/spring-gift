# code-style (refactor-step references)

`/refactor-step` 의 3단계(적용)에서 참조한다. spring-gift 의 목표 패키지 구조와 계층 책임에 맞춰 코드를 어디에, 어떻게 둘지 결정할 때 쓰는 짧은 매뉴얼이다. 일반 코딩 규칙은 `.claude/rules/coding-convention.md` 가 항상 자동 로드되므로 여기서 중복하지 않는다.

## 변경 시작 전 결정 사항

1. 이 변경은 어느 계층을 건드리는가. api / application / domain / infrastructure / storage / support / config 중 어디인가.
2. 변경 후 새 클래스가 생긴다면 어디에 두는가. CLAUDE.md 3번 섹션의 목표 트리 기준.
3. 기존 패키지(`gift.auth`, `gift.product` 등 평면 배치)에서 옮겨야 한다면 옮길 위치를 먼저 선언한다.

## Controller -> Service 추출 시 (가장 빈번한 패턴)

1. 현재 Controller 가 직접 호출하는 Repository 와 비즈니스 분기를 식별한다.
2. `application/<domain>/` 아래에 `*Service` 를 만든다. 클래스 상단 `@Transactional(readOnly = true)`, CUD 메서드에 `@Transactional` 명시.
3. Controller 는 Service 만 의존하도록 변경한다. Repository 의존 제거.
4. Request/Response DTO 는 `api/` 에 두되, Service 가 받는 입력은 도메인 객체 또는 Command 타입으로 분리한다. Controller 에서 변환한다.
5. JPA Entity 가 Controller 까지 노출되지 않도록 점검한다.

## 트랜잭션 경계 명시 시

1. Service 클래스에 클래스 단위 `@Transactional(readOnly = true)` 가 있는지 확인한다. 없으면 추가한다.
2. CUD 메서드에 메서드 단위 `@Transactional` 이 있는지 확인한다. 없으면 추가한다.
3. Repository, Controller, Validator 에서 `@Transactional` 을 발견하면 Service 로 옮긴다.
4. 한 유스케이스 안에서 여러 도메인을 호출하는 경우, 진입점 한 곳에서만 트랜잭션을 연다. 중첩 시작 금지.

## 도메인 책임 회수 시

1. Service 안에 들어 있는 비즈니스 규칙(범위 검증, 단위 계산, 정책 분기) 을 찾는다.
2. 해당 규칙을 `domain/<domain>/policy/*Policy` 또는 도메인 모델 메서드로 옮긴다.
3. Service 는 정책 호출 + 영속화만 담당하도록 좁힌다.
4. 형식 검증(길이, null, 패턴)은 `api/validator/` 에 별도로 둔다. 정책에 섞지 않는다.

## 카카오 관련 코드 이동 시

1. 카카오 OAuth/메시지 클라이언트는 `infrastructure/oauth/` 로 옮긴다. (`KakaoLoginClient`, `KakaoMessageClient`, `TokenResponse` 등 패턴)
2. JWT 발급/검증은 `infrastructure/auth/` 로 옮긴다. (`*Provider` 패턴)
3. 카카오 설정은 `config/kakao/` 로 옮긴다. (`*Properties` 패턴)
4. ArgumentResolver 는 `api/resolver/` 로 옮긴다.
5. spring-gift 의 기존 `gift/auth/` 안에 흩어진 카카오 관련 클래스를 위 4개 위치로 분리한다. Java 관용구로 작성한다 (record 또는 일반 클래스, Optional 또는 명시 null 처리).

## 점검 체크리스트 (적용 직후)

- 변경한 클래스가 목표 계층 위치에 있는가.
- depth 2 / 메서드 15줄 / else 금지 / switch, 3항 연산자 금지가 지켜졌는가.
- domain 패키지에 JPA, Spring 어노테이션 import 가 새로 들어가지 않았는가.
- Controller 에서 Repository 를 직접 호출하지 않는가 (`/extract-service` 류 작업이라면).
- DTO 가 도메인별 단일 `*Dto.java` 파일에 모이는 규칙을 지키는가.

위 5개 중 하나라도 어긋나면 적용 결과를 되돌리고 사용자에게 보고한다.
