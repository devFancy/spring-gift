# 5. Use integration tests as behavior preservation evidence

Date: 2026-05-19

## Status

Accepted

## Context

본 PR 은 평면 패키지를 6계층 구조로 옮기는 큰 변경 (ADR-001) 이다. 의존 방향, 트랜잭션 경계, 응답 포맷이 모두 바뀐다. 코드 컴파일과 정적 분석만으로는 변경 후의 작동이 변경 전과 같다는 것을 증명할 수 없다. 한편 단위 테스트만으로 채우면 외부 호출, DB 영속화, 트랜잭션 경계 같은 경계 동작이 빠진다.

비교한 선택지는 다음과 같다.

- 단위 테스트 중심 (Mockito 만으로 Controller, Service, Repository 각각 격리)
- 통합 테스트 중심 (`@SpringBootTest` + 실제 DB + 외부 API 만 mock)
- E2E 테스트 (실제 외부 호출 포함)

## Decision

통합 테스트 중심 전략을 채택한다. 도메인마다 두 종류의 통합 테스트를 짝지어 둔다.

- `{Domain}ControllerTest` HTTP 진입점에서 외부 계약 (URL, 인증, status code, 본문 핵심 필드) 검증
- `{Domain}ServiceTest` ControllerTest 가 닿지 않는 시나리오 (도메인 정책 분기, idempotent 보장, 운영자 흐름) 검증

모든 통합 테스트는 다음 베이스를 공유한다.

- `@SpringBootTest` + `@ActiveProfiles("test")` 로 전체 컨텍스트 부팅
- `DatabaseCleaner` 가 매 테스트 전에 모든 테이블 truncate
- 외부 API 호출 (카카오 OAuth, 카카오 메시지) 만 `@MockitoBean` 으로 격리

리팩터링 작업은 도메인 단위로 다음 3개 커밋으로 분리한다.

1. `test:` 작동 보존 테스트 (변경 전 외부 계약과 DB 상태 변화 캡처)
2. `refactor:` 6계층 분리와 응답 포맷 통일
3. `test:` 정책 분기 / idempotent 시나리오를 ServiceTest 로 보강

작동 보존 테스트는 응답 본문의 세부 모양보다 status code 와 DB 상태 변화 중심으로 검증한다. 응답 포맷 통일 (`ApiResponse<T>`, URL `v1` prefix) 같은 외부 계약 변경에도 테스트가 깨지지 않도록 안전망을 잡는다.

## Consequences

긍정적인 결과.

- 변경 전후 작동 보존을 외부 계약 (status + DB 상태) 으로 증명할 수 있다. 응답 본문 세부 구조가 바뀌어도 안전망이 깨지지 않는다.
- 외부 API 호출만 mock 으로 격리하고 내부 흐름 (회원 저장, JWT 발급, 트랜잭션) 은 실제 통합이라 작동 보존 안전망이 약해지지 않는다.
- 작동 보존 테스트가 변경 직전 별도 `test:` 커밋으로 분리되어 있어 변경 전 작동의 캡처 시점이 git 히스토리에서도 명확하다.
- ControllerTest 와 ServiceTest 의 시나리오 분배 기준이 생긴다. 단순 CRUD 도메인 (예: 카테고리) 처럼 ServiceTest 가 ControllerTest 와 겹치면 ServiceTest 를 통째로 들어내는 판단도 가능해진다.

부정적인 결과.

- 컨텍스트 부팅 비용이 매 테스트에서 발생해 단위 테스트보다 실행 시간이 길다. 본 프로젝트 규모에서는 수십 초 단위라 감내 가능.
- mock 으로 외부 의존을 모두 치환하면 통합 테스트가 단위 테스트로 격하되어 안전망이 약해진다. 카카오 외부 API 만 mock 하고 그 외 통합을 그대로 유지한다는 경계가 매 테스트 작성 시 의식돼야 한다.
- 통합 테스트 베이스 (`IntegrationTestSupport`, `DatabaseCleaner`) 자체의 변경은 전체 테스트에 영향을 주므로 신중해야 한다.

후속 결정.

- 외부 API 호출 테스트 자체를 격리할 필요가 생기면 `@Tag("external-api")` 로 분리하고 CI 포함 여부를 별도 합의한다.
- E2E 테스트 도입은 본 PR 범위 밖. 운영 환경 검증이 필요해질 때 별도 ADR 로 다룬다.
