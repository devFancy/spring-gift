# 4. Include HttpStatus in ErrorType

Date: 2026-05-31

## Status

Accepted

## Context

에러 처리 구조를 설계할 때 HTTP 상태 코드를 어느 지점에서 결정할지가 문제였다. `ErrorType`은 `support/` 패키지에 두기로 이미 정했지만 (ADR-001), `ErrorType` 자체가 `HttpStatus` 필드를 들고 있어야 하는지는 별도 결정이 필요했다.

리뷰 과정에서 "서비스 계층이 `ErrorType`을 참조하면 `HttpStatus`라는 웹 계층 개념이 서비스 안으로 들어온다"는 우려가 제기되었고, 이 트레이드오프를 명시적으로 기록한다.

비교한 선택지는 세 가지였다.

- `ErrorType`에 `HttpStatus` 포함 (현재): 에러 코드, 메시지, 로그 레벨, HTTP 상태를 `ErrorType` 하나에 모은다. `GlobalExceptionHandler`는 `ErrorType`만 읽어 응답을 완성한다.
- `ErrorType`은 HTTP 무관, Handler에서 매핑: `ErrorType`을 HTTP 개념 없이 정의하고, `GlobalExceptionHandler`가 `ErrorType -> HttpStatus` 매핑 테이블을 관리한다.
- 도메인별 예외 계층 + `@ResponseStatus`: 도메인마다 예외 클래스를 만들고 어노테이션으로 상태 코드를 지정한다.

## Decision

`ErrorType`에 `HttpStatus`를 포함하는 첫 번째 방식을 채택한다.

결정의 핵심 근거는 두 가지다.

첫째, 서비스 계층이 `ErrorType`을 직접 참조하더라도 `HttpStatus`를 직접 사용하지는 않는다. 서비스는 `throw new CoreException(ErrorType.NOT_FOUND)` 형태로 에러 분류를 선언할 뿐이며, `HttpStatus`를 꺼내 쓰는 코드는 `GlobalExceptionHandler` 한 곳에만 있다. `HttpStatus`가 `ErrorType` 내부에 있다는 사실은 서비스 입장에서 구현 세부 사항이다.

둘째, `support/`는 ADR-001에서 "전 영역이 공유하는 응답 형식과 에러 타입"으로 정의했다. 이 계층은 이미 웹 계층과 도메인 계층 사이의 공유 공간이다. HTTP 상태 코드를 여기서 관리하는 것은 `support/`의 역할과 어긋나지 않는다.

두 번째 선택지(Handler에서 매핑)는 `ErrorType` 정의와 `HttpStatus` 매핑이 두 파일로 분산된다. 새 에러 타입을 추가하거나 수정할 때 두 파일을 함께 수정해야 하는 번거로움이 생기고, 매핑 누락 버그가 발생할 수 있다. 이 규모에서 분산의 이점(순수한 에러 분류)이 단점을 상회하지 않는다.

세 번째 선택지(도메인별 예외 계층)는 도메인마다 예외 클래스를 만들어야 해서 파일 수가 크게 늘어난다. 현재 에러 유형이 6개 수준인 단순한 규모에서는 불필요하다.

## Consequences

긍정적인 결과.

- 에러 코드, HTTP 상태, 메시지, 로그 레벨이 `ErrorType` 한 곳에서 관리된다. 새 에러 유형을 추가할 때 파일 하나만 수정한다.
- `GlobalExceptionHandler`가 단순하다. `ErrorType`에서 속성만 읽어 응답을 완성하므로 분기가 없다.
- 새 에러 유형 추가 시 누락 항목이 생기지 않는다. 컴파일 타임에 enum 생성자가 모든 필드를 강제한다.

부정적인 결과.

- 비즈니스 에러 분류와 HTTP 매핑이 한 enum에 공존한다. 단일 책임 관점에서는 두 관심사가 섞인 구조다. 현재 HTTP 단일 전송 계층에서는 실질적인 문제가 없다.

