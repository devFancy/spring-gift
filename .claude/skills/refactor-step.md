---
name: refactor-step
description: spring-gift 리팩터링 한 조각을 단계별로 진행. 단일 목적 확인, 한 번에 한 조각으로 좁히기, 작동 보존 검증, 커밋 직전 사용자 승인 요청까지 포함. /refactor-step, 리팩터링, refactor, 서비스 추출, 트랜잭션 경계, 도메인 책임, 한 조각 키워드에 트리거.
argument-hint: "{변경 한 가지 설명}"
---

# refactor-step

spring-gift 리팩터링 한 조각을 받아 단계별로 진행하는 워크플로우. 단일 목적 커밋과 한 번에 한 조각 원칙을 실행 흐름으로 갖는다.

## 사용법

```
/refactor-step {변경 한 가지 설명}
```

예시:
- `/refactor-step ProductController 의 비즈니스 로직을 ProductService 로 추출`
- `/refactor-step OrderRepository 의 트랜잭션 경계 명시`
- `/refactor-step Member 도메인에 비밀번호 정책 회수`

## 흐름

### 1단계: 단일 목적 확인

입력된 변경이 단일 목적인지(예: 패키지 재배치 한 가지, 트랜잭션 경계 명시 한 가지, 도메인 정책 회수 한 가지) 사용자와 함께 확인한다. 두 가지 이상 섞여 있으면 "한 가지만" 으로 좁힌다.

### 2단계: 범위 좁히기

변경 범위가 여러 도메인, 여러 계층에 걸쳐 있으면 "다음 변경 1개" 로 더 좁힌다. 좁히기 어렵다면 사용자에게 분할안 2, 3개를 제시하고 1개를 고르게 한다.

### 3단계: 적용

코드 수정/추가 시: `code-style.md` 를 먼저 읽는다. 패키지 배치, 트랜잭션 경계, 검증 분리, DTO 분리 원칙을 따라 변경을 적용한다.

### 4단계: 검증 (작동 보존 증거)

테스트 작성/실행 시: `testing-guide.md` 를 먼저 읽는다. 기존 테스트 전체 통과로 작동 보존을 증명한다. 기존 테스트가 없는 영역이면 변경 전에 작동 보존 테스트를 별도 `test:` 커밋으로 먼저 추가한다.

API 추가/변경 시: `api-convention.md` 를 먼저 읽는다. ApiResponse 포맷, 검증 분리, ErrorType 사용을 일관되게 적용한다.

### 5단계: 커밋 직전 사용자 승인 요청

`git status` 와 `git diff` 를 사용자에게 보여주고 멈춘다. `.claude/rules/commit-discipline.md` 형식으로 커밋 메시지 초안을 제시하되, 제목은 도메인 유비쿼터스 언어(상품, 주문, 카카오 로그인 등)로 작성한다. 기술 어휘(Controller, Repository, Service 등)는 제목에 노출하지 않는다. 사용자 명시 승인을 받기 전까지 `git commit` 을 실행하지 않는다. Co-Authored-By 또는 AI 출처 표기를 메시지에 포함하지 않는다.
