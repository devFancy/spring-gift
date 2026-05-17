# api-convention (refactor-step references)

`/refactor-step` 의 4단계 중 "API 추가/변경 시" 분기에서 참조한다. 일반 코딩 규칙은 `.claude/rules/coding-convention.md` 가 자동 로드되므로 여기서는 API 레이어에 한정된 의사결정만 다룬다.

## 응답 포맷: ApiResponse<T>

모든 API 응답은 `support/` 의 `ApiResponse<T>` 객체로 감싼다.

```json
{
  "result": "SUCCESS or ERROR",
  "data": "T 또는 null",
  "error": { "code": "...", "message": "..." }
}
```

- `result`: SUCCESS 또는 ERROR 문자열.
- `data`: 성공 시에만 실제 데이터. 없으면 null 또는 빈 객체.
- `error`: ERROR 시에만 코드와 메시지를 포함. SUCCESS 시 null.

`ApiResponse` 가 spring-gift 에 아직 없으면 본 작업의 일환으로 `support/response/` 에 먼저 신설한다. 한 번만 만들면 됨. 단일 커밋으로 분리한다.

## 에러 처리

- 에러 코드는 `support/error/ErrorType` enum 에 모은다. 신규 에러가 필요하면 enum 에 추가한다.
- `CoreException` 또는 동등한 도메인 예외를 throw 하고, 전역 ExceptionHandler 가 `ApiResponse.error(...)` 로 변환한다.
- Controller 안에서 `ResponseEntity<ErrorBody>` 를 직접 빌드하지 않는다. 일관된 ExceptionHandler 만 사용한다.

## 검증 분리 (api Validator vs domain Policy)

- 형식 검증 (입력값 길이, null, 정규식 패턴, 형식 일치 여부): `api/validator/` 의 Validator 클래스에서 처리. 결과로 형식 에러를 던진다.
- 비즈니스 정책 검증 (값의 범위, 단위 계산, 도메인 상태 전이 가능 여부): `domain/policy/*Policy` 에서 전담.
- Service 에서 직접 if 문으로 검증하지 않는다. Validator 또는 Policy 호출만 한다.
- 프론트엔드 검증은 UX 보조이지 신뢰 경계가 아니다. 모든 결정값은 백엔드 응답으로 제공한다.

## URL/메서드 규칙

- REST 자원 기반. 동사 URL 금지 (`POST /products/create` 금지, `POST /products` 사용).
- 컬렉션은 복수형. 단일 자원은 식별자 path variable.
- 인증이 필요한 API 는 `AuthenticationResolver` 가 주입하는 Member 객체로 식별한다. Controller 에서 직접 세션/토큰 파싱 금지.

## 신규 엔드포인트 추가 절차 (4단계 안에서)

1. URL, 메서드, 요청/응답 DTO 스케치를 사용자에게 먼저 제시한다.
2. 형식 검증 항목은 Validator 로 분리한다.
3. 정책 검증 항목은 Policy 로 분리한다.
4. Controller 본문은 Validator 호출 -> Service 호출 -> 응답 매핑 3단계로 단순화한다.
5. 통합 테스트 1개를 추가하여 성공 경로와 대표 실패 경로를 검증한다.
6. `http/` 폴더가 있다면 `.http` 파일에 호출 예시를 추가한다 (없으면 이번 작업에서 신설 여부를 사용자와 합의).

## 점검 체크리스트 (적용 직후)

- 모든 응답이 `ApiResponse<T>` 로 감싸졌는가.
- 에러가 ExceptionHandler 경로로만 발생하는가 (Controller 안 try/catch 노출 없음).
- 형식 검증과 정책 검증이 다른 위치에 있는가.
- Controller 본문이 4단계 절차의 3단계 구조를 유지하는가.
- JPA Entity 가 응답 DTO 로 그대로 노출되지 않는가.
