# 2. Use integration tests as behavior preservation evidence

Date: 2026-05-19

## Status

Accepted

## Context

본 PR 은 평면 패키지를 6계층 (ADR-001) 으로 옮기는 큰 변경이다. 의존 방향, 트랜잭션 경계, 응답 포맷이 모두 바뀐다. 코드 컴파일과 정적 분석만으로는 변경 후의 작동이 변경 전과 같다는 것을 증명할 수 없다. 단위 테스트만으로 채우면 외부 호출 / DB 영속화 / 트랜잭션 경계 같은 경계 동작이 빠진다.

비교 선택지.

- 단위 테스트 중심 (Mockito 로 각 계층 격리)
- 통합 테스트 중심 (`@SpringBootTest` + 실제 DB + 외부 API 만 mock)
- E2E 테스트 (실제 외부 호출 포함)

## Decision

통합 테스트 중심 전략을 채택한다. 도메인마다 두 종류의 통합 테스트를 짝지어 둔다.

- `{Domain}ControllerTest` 외부 계약 (URL, 인증, status code, 핵심 본문) 검증
- `{Domain}ServiceTest` ControllerTest 가 닿지 않는 시나리오 (도메인 정책 분기, idempotent 보장, 운영자 흐름) 검증

베이스는 모든 통합 테스트가 공유한다. `@SpringBootTest` + `@ActiveProfiles("test")` 로 전체 컨텍스트 부팅하고, `DatabaseCleaner` 가 매 테스트 전에 모든 테이블 truncate. 외부 API 호출 (카카오 OAuth, 카카오 메시지) 만 `@MockitoBean` 으로 격리한다.

리팩터링 작업은 도메인 단위로 3개 커밋으로 분리한다.

1. `test:` 작동 보존 테스트 (변경 전 외부 계약과 DB 상태 변화 캡처)
2. `refactor:` 6계층 분리와 응답 포맷 통일
3. `test:` 정책 분기 / idempotent 시나리오 ServiceTest 보강

## Consequences

- 변경 전후 작동 보존을 외부 계약 (status + DB 상태) 으로 증명할 수 있다. 응답 본문 세부 구조 변경에 안전망이 깨지지 않는다.
- 외부 API 만 mock 하고 내부 흐름 (회원 저장, JWT 발급, 트랜잭션) 은 실제 통합이라 안전망이 단위 테스트로 격하되지 않는다.
- 작동 보존 테스트가 변경 직전 별도 `test:` 커밋으로 분리돼 캡처 시점이 git 히스토리에서도 드러난다.
- 단순 CRUD 도메인 (예: 카테고리) 처럼 ServiceTest 가 ControllerTest 와 시나리오가 겹치면 ServiceTest 를 통째 들어내는 판단도 가능해진다.
- 컨텍스트 부팅 비용이 매 테스트에서 발생해 단위 테스트보다 실행 시간이 길다. 도메인이 늘어 누적 시간이 부담스러워지면 Testcontainers 또는 test slice 도입을 후속 결정으로 본다.
