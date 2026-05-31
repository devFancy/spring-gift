# 1. Adopt responsibility-based package boundaries

Date: 2026-05-19

## Status

Accepted

## Context

기존 코드는 도메인 패키지(`gift/product/`, `gift/member/` 등) 안에 Controller, Repository, Entity, Validator 가 평면적으로 섞여 있었다. Controller 가 Repository 를 직접 호출하면서 비즈니스 흐름이 흩어졌고, 도메인 간 의존 관계가 어디서 어떻게 만들어지는지 한눈에 보이지 않았다. 새 도메인을 추가하거나 기존 도메인을 손볼 때 "이 코드가 어디 들어가야 하는가" 가 매번 새로 결정되는 상태였다.

비교한 선택지는 다음과 같다.

- 평면 패키지 유지
- 6계층 (api / application / domain / infrastructure / storage / support) 으로 분리
- 헥사고날 / 클린 아키텍처 (port / adapter 명시 분리)

## Decision

도메인별 6계층 패키지 구조를 채택한다. 각 도메인은 다음 위치에 자기 부분을 가진다.

- `api/{domain}/` 사용자 요청 진입점 (Controller, DTO)
- `application/{domain}/` 응용 서비스 (Service, 트랜잭션 경계)
- `domain/{domain}/` 도메인 모델과 정책 (Policy, VO. 프레임워크 의존 금지)
- `infrastructure/` 외부 시스템 통신 및 기술 구현체. 카카오 OAuth 코드는 `oauth/{client,dto,uri}` 하위에, JwtProvider / Sha256PasswordEncoder / SystemClockHolder 는 루트에 직접 배치
- `storage/{domain}/` 영속성 (JPA Entity, Repository)
- `support/` 전 영역이 공유하는 응답 형식과 에러 타입

의존 방향은 단방향이다. `api -> application -> domain` 이 정방향이며, `domain` 은 다른 어떤 계층에도 의존하지 않는다.

## Consequences

긍정적인 결과.

- 새 도메인을 추가할 때 어디에 무엇을 두어야 하는지 자명해진다.
- 도메인 책임 누수가 가시화된다. Controller 가 Service 를 거치지 않고 Repository 를 직접 호출하면 import 만 봐도 비정상으로 보인다.
- 검토 시 도메인별 흐름을 6개 폴더 안에서만 추적하면 된다.

부정적인 결과.

- 7개 도메인 모두에 6계층을 일괄 적용해야 해서 한 번에 변경할 양이 많다. 도메인 단위로 잘게 나눈 3커밋 패턴 (`test:` 작동 보존 -> `refactor:` 6계층 분리 -> `test:` ServiceTest) 으로 분할해 완화했다.
- 한 도메인을 손볼 때 여러 폴더를 동시에 열어야 하는 경우가 생긴다. IDE 의 패키지 탐색에 의존도가 늘어난다.
- 헥사고날 구조와 비교하면 port / adapter 경계가 명시되지 않아 외부 의존성 교체가 완전히 자유롭지는 않다. 본 서비스 규모에서는 6계층이 적정선이라 판단했다.

후속 결정.

- 통합 테스트로 6계층 분리의 작동 보존을 증명하는 전략은 ADR-002 에서 다룬다.
