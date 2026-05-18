# testing-guide (refactor-step references)

`/refactor-step` 의 4단계(검증)에서 참조한다. 일반 테스트 룰은 `.claude/rules/testing-discipline.md` 가 항상 자동 로드되므로 여기서는 리팩터링 작동 보존을 어떻게 증거로 만들지에 한정한다.

## 작동 보존 증거 만들기

- 작동 보존이 목표. 새 테스트는 변경된 작동을 검증하기 위해 추가하지 않는다 (본 과제는 리팩터링 전용).
- 기존 전체 테스트가 변경 전과 동일하게 통과해야 한다.
- 기존 테스트가 없는 영역을 리팩터링한다면, 변경 전에 작동 보존 테스트를 먼저 추가한다. 이 테스트는 별도 `test:` 커밋으로 분리해서 리팩터링 커밋보다 앞에 둔다.
- 통합 테스트에서는 API 호출 결과(status, body, 헤더)가 변경 전후 동일한지 비교한다.
- 도메인 정책 테스트에서는 입력 -> 출력 매핑이 변경 전후 동일한지 비교한다.
- 테스트를 약화 또는 비활성화하는 변경은 금지. 회피 패턴이 발견되면 즉시 되돌린다.

## 두 가지 테스트 유형 매핑

### 도메인 정책 단위 테스트

- 대상: `domain/policy/*Policy`, 도메인 모델 메서드.
- 외부 의존 없이 POJO 단위로 검증. Spring Context 부팅 없이 실행되어야 한다.
- 테스트 패키지: 대상과 동일하게 미러링 (예: `domain/policy/ProductPolicyTest`).
- 빠른 피드백을 위해 단위 테스트가 통합 테스트보다 우선이다.

### 사용자 시나리오 통합 테스트

도메인 한 조각을 작업할 때 통합 테스트를 두 종류로 분리해서 함께 작성한다.

- `{Domain}ControllerTest` (`api/{domain}/`): HTTP 진입점에서 URL/인증/status code/권한 같은 외부 계약을 검증한다. `@AutoConfigureMockMvc` + MockMvc.
- `{Domain}ServiceTest` (`application/{domain}/`): 인증을 우회하고 비즈니스 흐름을 직접 검증한다. 도메인 정책 분기, 운영자 흐름, idempotent 보장처럼 ControllerTest 가 닿지 않는 시나리오 중심.

공통 베이스: `IntegrationTestSupport` (`@SpringBootTest` + `@ActiveProfiles("test")` + `DatabaseCleaner` 의 테스트 전 truncate). 클래스 `@DisplayName` 은 도메인 비즈니스 어휘, `@Nested` 로 시나리오 그룹.

파레토 원칙: 모든 메서드를 다 검증하지 않는다. ControllerTest 와 ServiceTest 가 중복되지 않도록 시나리오를 분배하고, 비즈니스적으로 가치가 큰 시나리오만 둔다.

외부 API 호출 테스트는 `@Tag("external-api")` 로 격리한다. CI 포함 여부는 사용자와 합의한다.

## BDD 와 네이밍

- `// given`, `// when`, `// then` 주석으로 구간 분리. 모든 테스트에 적용.
- 클래스 `@DisplayName` 은 비즈니스 도메인 용어. `*Test`, `*규칙 테스트`, `*서비스` 같은 개발자 용어 접미사 금지.
- 메서드는 "조건일 때 결과" 패턴. 한국어 사용 시 `@DisplayName` 활용.
- 예외 케이스는 "예외가 발생한다" 로 통일.

## 실행 절차 (4단계 안에서)

1. 변경 적용 직후 `./gradlew test` 실행.
2. 실패가 있으면 코드를 먼저 수정한다. 테스트를 약화시키지 않는다.
3. 통과 후 `./gradlew check` 또는 `./gradlew build` 로 빌드 + 테스트 일괄 검증.
4. 결과를 사용자에게 한 줄로 보고한다 (통과 수, 실패 수, 빌드 성공/실패).
5. 통과한 테스트가 실제 작동 보존을 충분히 캡처하는지 한 번 더 확인하고 한 줄로 보고한다.

## 회피 패턴 차단

다음을 발견 즉시 되돌린다.
- `@Disabled`, `@Ignore` 가 새로 부착됨.
- assertion 이 약화됨 (`assertEquals` -> `assertNotNull`).
- try/catch 로 실패가 삼켜짐.
- 통합 테스트가 단위 테스트로 격하되며 외부 의존이 모두 mock 으로 치환됨 (작동 보존 안전망 손실).

위 패턴은 commit-discipline 의 위반과 동급으로 다룬다.
