# 3. Separate Domain Object from JPA Entity

Date: 2026-05-29

## Status

Accepted

## Context

리팩터링 이전 코드는 `storage/` 패키지의 JPA `@Entity` 클래스가 도메인 객체 역할을 겸했다. 이 구조에서는 두 가지 문제가 생겼다.

첫째, 값 객체(Value Object)를 도입하려면 JPA의 `@Embeddable`을 VO 클래스에 붙여야 했다. VO는 순수한 도메인 개념인데 JPA 어노테이션이 섞이면 도메인 계층이 영속성 기술에 종속된다.

둘째, `api/validator/` 의 형식 검증 로직이 `application/` 서비스에서 직접 호출되어 `application -> api` 역방향 의존이 생겼다. VO 생성자로 검증을 흡수하려 해도 VO가 `@Embeddable`을 가져야 하면 `domain` 패키지에 JPA import가 들어와 기존 패키지 경계 규칙(ADR-001)과 충돌했다.

비교한 선택지는 세 가지였다.

- JPA `@Entity`가 도메인 역할 겸임 유지 (현상 유지)
- `@Embeddable` VO를 `domain/` 패키지에 허용 (coupon-system 방식)
- 순수 도메인 객체와 JPA Entity를 완전히 분리 (spring-shopping 방식)

## Decision

도메인 객체와 JPA Entity를 계층별로 완전히 분리한다.

- `domain/{domain}/` - 순수 Java 클래스. JPA/Spring import 없음. 비즈니스 행위와 불변식 보유.
- `domain/{domain}/vo/` - 값 객체. 생성자에서 도메인 규칙 검증. JPA/Spring import 없음.
- `domain/{domain}/{Domain}Repository.java` - 도메인 Repository 인터페이스. Spring Data 의존 없음 (Page/Pageable은 허용).
- `storage/{domain}/{Domain}Entity.java` - JPA `@Entity`. `toDomain()` / `from(domain)` 변환 메서드 보유.
- `storage/{domain}/{Domain}JpaRepository.java` - `JpaRepository<*Entity, Long>` 확장 인터페이스.
- `storage/{domain}/{Domain}RepositoryImpl.java` - 도메인 Repository 인터페이스 구현체. `@Repository` 빈.

의존 방향은 `api -> application -> domain` 단방향을 유지한다. `storage` 는 `domain` 을 참조하되 역방향은 허용하지 않는다.

## Consequences

긍정적인 결과.

- 도메인 계층에 JPA/Spring 어노테이션이 전혀 없다. VO 생성자에서 형식 검증을 수행하므로 `application -> api` 역방향 의존이 사라진다.
- 도메인 객체와 VO는 Spring Context 없이 단위 테스트가 가능하다.
- 도메인 Repository 인터페이스가 추상화 경계 역할을 해서, 영속성 기술 교체 시 `storage/` 구현만 바꾸면 된다.

부정적인 결과.

- 도메인마다 Entity, JpaRepository, RepositoryImpl 세 파일이 추가된다. 기존 방식보다 파일 수가 늘어난다.
- `toDomain()` / `from()` 변환 코드가 각 Entity에 생긴다. 도메인 필드가 많아질수록 변환 코드도 늘어난다.
- 연관 관계가 깊으면 (Order -> Option -> Product -> Category) `toDomain()` 호출 체인이 생긴다. `@Transactional` 경계 안에서 호출해야 Lazy 로딩 문제가 생기지 않는다.
