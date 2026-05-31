# 3. Separate Domain Object from JPA Entity

Date: 2026-05-29

## Status

Superseded (2026-05-31 재검토)

## Context

리팩터링 이전 코드는 `storage/` 패키지의 JPA `@Entity` 클래스가 도메인 객체 역할을 겸했다. 
이 구조에서는 `api/validator/` 의 형식 검증 로직이 `application/` 서비스에서 직접 호출되어 `application -> api` 역방향 의존이 생겼다. VO 생성자로 검증을 흡수하는 방향으로 구조를 개선하려면 두 가지 접근 중 하나를 선택해야 했다.

비교한 선택지는 세 가지였다.

- JPA `@Entity`가 도메인 역할 겸임 유지 (현상 유지)
- `@Embeddable` VO를 `domain/` 패키지에 허용 (JPA Entity와 함께 사용)
- 순수 도메인 객체와 JPA Entity를 완전히 분리

## Decision

도메인 객체와 JPA Entity를 완전히 분리하는 세 번째 방식을 택했다. 이 결정의 실제 목적은 "분리했을 때와 함께 두었을 때 중 유지보수 용이성 기준으로 어느 쪽이 더 나은가"를 직접 경험해서 판단하는 것이었다.

- `domain/{domain}/` - 순수 Java 클래스. JPA/Spring import 없음.
- `domain/{domain}/vo/` - 값 객체. 생성자에서 도메인 규칙 검증.
- `storage/{domain}/{Domain}Entity.java` - JPA `@Entity`. `toDomain()` 과 `from()` 변환 메서드 보유.
- `storage/{domain}/{Domain}JpaRepository.java` / `{Domain}RepositoryImpl.java`

## Consequences

긍정적인 결과.

- 도메인 계층에 JPA 어노테이션이 전혀 없다. `application -> api` 역방향 의존이 사라진다.
- 계층 경계가 명확해져 각 계층을 독립적으로 읽을 수 있다.

부정적인 결과.

- 도메인마다 Entity, JpaRepository, RepositoryImpl 세 파일이 추가된다.
- `toDomain()` / `from()` 변환 코드가 각 Entity에 생긴다. 연관 관계가 있는 엔티티는 변환 파라미터가 늘어난다.
- 연관 관계가 깊으면 `toDomain()` 호출 체인이 생기고, `@Transactional` 경계 밖에서 호출 시 Lazy 로딩 문제가 발생한다.

## 사후 검토 (2026-05-31)

분리를 직접 경험한 결과, JPA 기반 프로젝트에서는 도메인 객체와 JPA Entity를 함께 두는 것이 유지보수 면에서 더 적합하다는 판단에 이르렀다.

분리로 얻으려 한 핵심 이점(도메인 단위 테스트)은 이후에 실제로 작성해서 확인했다. VO 생성자 검증, 도메인 메서드 정책(포인트 충전/차감, 재고 차감)을 Spring Context 없이 순수 JUnit 으로 실행할 수 있었다. 반면 변환 코드 비용(`toDomain()` / `from()` 파라미터 증가, Lazy 로딩 주의)도 실제로 발생했다. 이점을 실현했지만, 동일한 도메인 단위 테스트는 `@Embeddable` VO 를 사용하는 구조에서도 작성할 수 있다. 분리가 이 이점의 유일한 수단은 아니었다.

"JPA 어노테이션이 섞이면 도메인 계층이 영속성 기술에 종속된다"는 최초 근거도 과장이었다. 애너테이션은 런타임 메타데이터이며, 이것이 비즈니스 로직에 직접적인 제약을 만들지는 않는다.
