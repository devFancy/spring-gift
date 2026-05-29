# spring-gift - Project Guide for Claude Code

본 문서는 spring-gift 리팩터링 작업의 Project 수준 컨텍스트다. Claude Code 가 모든 코드 작업 전에 자동 로드한다. 구조와 컨벤션은 Spring Boot 환경의 도메인 주도 설계와 클린 아키텍처 원칙을 spring-gift(Java) 맥락에 재서술한 결과다. 모든 항목을 그대로 따르지 않고 spring-gift 에 의미 있는 항목만 선택한다.

본 과제는 리팩터링 전용이다. 새 작동을 도입하는 기능 구현은 이번 라운드 범위가 아니다.

커밋 관련 절대 준수 항목은 `.claude/rules/commit-discipline.md` 가 단일 출처이며 다른 모든 룰보다 우선한다.

---

## 1. Core Constraints (모든 코드에 적용)

- 들여쓰기 깊이 최대 2단계. 3단계 발생 시 메서드를 분리한다.
- 메서드 길이 최대 15줄. 하나의 일만 하도록 작게 분리한다. 단, 원자적으로 묶인 비즈니스 행위는 예외로 둔다.
- `else` 키워드 사용 금지. Early Return 으로 분기한다.
- 들여쓰기 4 spaces. switch 문, 3항 연산자 사용 금지 (if 문으로 대체).
- 변수명은 구현 기술(`dto`, `entity`, `result`)이 아닌 도메인 의미로 짓는다.

세부 사항과 Java 관용구 적용 기준은 `.claude/rules/coding-convention.md` 참고.

---

## 2. 패키지 구조

Java 전용 프로젝트다. `src/main/kotlin` 디렉토리는 사용하지 않는다 (현재 빈 디렉토리만 존재).

### 현재 구조 (리팩터링 시작점)

```text
src/main/java/gift/
├── Application.java
├── auth/        # *Controller, *Client, *Properties, *Provider, *Resolver 혼재
├── category/    # *Controller, *Repository, *Request, *Response, Entity 평면 배치
├── member/      # 동일 패턴 + Admin* 별도 Controller
├── option/      # 동일 패턴 + *NameValidator
├── order/       # 동일 패턴 + *MessageClient (외부 호출)
├── product/     # 동일 패턴 + Admin* 별도 Controller
└── wish/        # 동일 패턴
```

특징: Service 계층 0개. Controller 가 Repository 를 직접 호출하고, 외부 클라이언트와 도메인이 한 패키지에 섞여 있다.

### 현재 구조 (리팩터링 완료 기준)

```text
src/main/java/gift/
├── Application.java
├── api/                  # 웹 계층 (*Controller, Request/Response DTO)
│   └── resolver/         # ArgumentResolver
├── application/          # 응용 계층 (*Service - 트랜잭션 경계, 유스케이스 진입점)
│   └── {domain}/         # 도메인별 하위 패키지 (예: application/product/ProductService)
├── domain/               # 순수 도메인 (JPA/Spring 어노테이션 금지)
│   └── {domain}/
│       ├── vo/           # 값 객체 (생성자에서 형식 검증. 예: ProductName, Email)
│       └── policy/       # 명시적 비즈니스 정책 (복잡하거나 재사용되는 규칙만)
├── infrastructure/       # 외부 시스템 통신
│   ├── *Provider         # 토큰 발급/검증 (JwtProvider)
│   ├── *PasswordEncoder  # 보안 구현체
│   ├── *ClockHolder      # 시간 추상화 구현체
│   └── oauth/            # 카카오 OAuth/메시지 영역
│       ├── client/
│       ├── dto/
│       └── uri/
├── storage/              # 영속성 (JPA *Entity, *JpaRepository, *RepositoryImpl)
│   └── {domain}/         # 도메인별 하위 패키지
├── support/              # 전역 예외, 공통 응답, ClockHolder 인터페이스
└── config/               # 설정 (*Properties 직접 배치)
```

- 도메인 객체(`domain/`)와 JPA 엔티티(`storage/*Entity`)를 완전히 분리한다. 도메인 Repository 인터페이스는 `domain/`에, 구현체(`*RepositoryImpl`)는 `storage/`에 둔다.
- 형식 검증은 VO 생성자 안에서 수행한다. `api/validator/` 를 별도로 두지 않는다.
- 비즈니스 정책은 복잡하거나 여러 Service 에서 재사용될 때만 `domain/{domain}/policy/*Policy` 로 분리한다. 단순 boolean 체크는 Service 에 직접 작성한다.
- 트랜잭션 기본값: Service 클래스 상단 `@Transactional(readOnly = true)`, CUD 메서드에만 `@Transactional` 명시.
- DTO 는 도메인별 단일 `*Dto.java` 파일에 Request/Response 정적 중첩 클래스로 모은다.

---

## 3. 커밋 컨벤션

형식: `<type>(<scope>): <한국어 제목>`

type 은 다음 5개만 사용한다. 본 과제는 리팩터링 전용이라 새 작동을 도입하는 `feat` 는 사용하지 않는다. 다른 type(style, perf, build, ci 등)은 chore 또는 refactor 로 흡수한다.

| type | 용도 |
| --- | --- |
| refactor | 리팩터링 (구조 변경, 작동 보존). 포맷팅/스타일도 흡수 |
| fix | 버그 수정 |
| test | 테스트만 추가/수정 |
| docs | README, 주석, 문서 변경 |
| chore | 빌드, 의존성, 도구 설정 |

scope: 도메인 단위 (auth / member / product / option / category / order / wish / build / config 등). 여러 도메인에 걸치면 가장 핵심 도메인으로 지정한다.

제목: 한국어, 50자 이내. 명령형/평서형 일관성 유지. 유스케이스 기반의 도메인 유비쿼터스 언어(상품, 주문, 카카오 로그인, 위시리스트, 옵션 등)로 작성해서 UX 관점에서 누구나 이해할 수 있어야 한다. 기술 어휘(Controller, Repository, Service, JPA, ArgumentResolver 등)는 노출하지 않는다. PM 이나 신입 동료가 봐도 변경의 의미를 이해할 수 있어야 한다. 본문은 정말 중요한 결정/이유만 1, 2줄. 없으면 생략한다.

예시:
- `refactor(product): 상품 조회 책임 정리`
- `refactor(auth): 카카오 로그인 흐름 정리`
- `test(order): 주문 저장 시나리오 통합 테스트 추가`
- `docs: README 에 카카오 로그인 흐름과 AI 활용 기록 추가`

절대 준수 4개 항목(자율 실행 금지, AI 출처 표기 금지, 단일 목적 커밋, 위험 명령 차단)과 직전 체크리스트는 `.claude/rules/commit-discipline.md` 가 단일 출처다.

---

## 4. Rule vs Skill 구분 원칙

판별 한 줄: "매번 자동으로 켜져 있어야 하는가, 필요할 때만 펴 보면 되는가."

Rule(`.claude/rules/`) 은 매번 자동 적용되는 가드레일/제약이다. 짧고 단일 책임이다. Skill(`.claude/skills/`) 은 호출되어 실행되는 워크플로우/레시피다. 단계가 있다. Rule 에 절차가 들어가 있거나 Skill 에 매번 적용되어야 할 제약이 박혀 있으면 잘못된 배치다. 발견하면 옮긴다.

---

## 5. 카카오 소셜 로그인 - 사전 가정

과제5 의 "과제 진행 요구 사항 (선택)" 인 카카오 API 애플리케이션 등록은 사용자가 직접 처리한다. Claude 는 등록 완료를 전제로 진행하며 `application.properties` 의 `kakao.login.client-id`, `client-secret`, redirect URI 가 설정되어 있다고 가정한다.

카카오 소셜 로그인은 OAuth 클라이언트(`infrastructure/oauth/`), 설정 프로퍼티(`config/kakao/`), 토큰 발급/검증(`infrastructure/auth/`), ArgumentResolver(`api/resolver/`) 4개 영역으로 분리해서 패키지 배치한다. 응답 처리, 토큰 발급 흐름은 Java 관용구(Optional, record 또는 일반 클래스)로 작성한다. 실제 호출 테스트가 필요한 작업이면 사용자에게 값 설정 여부를 먼저 묻는다.

어드민 키, 액세스 토큰, 클라이언트 시크릿은 저장소 또는 클라이언트 코드에 절대 포함하지 않는다.

---

## 6. 룰 라우팅

- 사용자 명령 수신 시 작업 흐름(모호함 확인 -> Plan -> 승인 -> Implement): `.claude/rules/plan-execute-discipline.md`.
- 커밋 관련 절대 준수와 메시지 형식: `.claude/rules/commit-discipline.md` (다른 모든 룰보다 우선).
- 코드 작성/리팩터링 시 일반 컨벤션: `.claude/rules/coding-convention.md`.
- 리팩터링 작업 절차와 11원칙: `.claude/rules/refactoring-discipline.md`.
- 테스트 작성/실행: `.claude/rules/testing-discipline.md`.
- 작업 종료 직전 자체 점검: `.claude/rules/self-verification.md`.
- 컨텍스트/세션 길어졌을 때: `.claude/rules/session-discipline.md`.
- 문서/응답 표기: `.claude/rules/formatting.md`.

리팩터링 한 조각을 단계별로 진행하려면 `/refactor-step` skill 을 호출한다. SKILL.md 가 references/ 의 세부 파일로 라우팅한다.

---

## 7. 룰/스킬 정리 정책

룰과 스킬이 늘어나면 관리 비용이 모델 개선보다 빨리 증가한다. 단순화 우선을 유지하기 위한 정책이다.

- 새 룰/스킬은 "매번 자동인가, 호출 시점만인가" 의 한 줄 판별을 거친다. 불명확하면 추가하지 않는다.
- 같은 보강을 두 번 이상 떠올렸을 때만 룰/스킬 후보로 기록한다. 한 번만으로는 만들지 않는다.
- 안 쓰는 룰/스킬, 사용처가 사라진 룰/스킬, 같은 책임이 중복된 룰/스킬은 "정리 후보" 로 기록한다. 사용자 명시 합의 없이 자발적으로 삭제하지 않는다.
- 보완할 때는 "내용 추가" 보다 "단일 책임 유지" 가 우선이다. 한 파일에 두 가지 책임이 섞이면 분리한다.
- 정기 점검 시점은 도메인 리팩터링 단위가 끝날 때다. 7개 도메인 중 한 도메인이 끝나면 룰/스킬을 한 번 점검한다.

세부 점검 항목은 `.claude/rules/self-verification.md` 가 정의한다.
