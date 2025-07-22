
# 개발 일기

어느날에 무엇을 어떤 근거로 개발했는지를 적음.
까먹지 않기 위해 알은 것들, 알아야하는 것들을 적음.

### 2025.07.04 ~ 2025.07.07
##### 적용 : 사용자 어노테이션
- `@Target(ElementType.TYPE)`  
    클래스·인터페이스·열거형 등에만 붙일 수 있도록 범위를 제한
- `@Retention(RetentionPolicy.RUNTIME)`
    런타임 시에도 이 애노테이션 정보를 유지해, 리플렉션 가능
- `@Documented`  
    Javadoc 생성 시 이 애노테이션을 문서화하여 표시하도록 합니다.
##### 적용 : 포트와 어댑터 아키텍처인 헥사고날아키텍처

비스니스 로직(Domain)을 외부 기술(Web,DB)와 완전히 분리한다.
Domain은 데이터모델과 모델의 비지니스 규칙을 넣음
Application은 중간계층으로 실제 기능을 담당하고,
port(인터페이스)와 useCase(구현체)로 나뉨.
port는 어플리케이션이 무엇을 할 수 있는가를 적고, useCase는 그에 대한 구현체임.
port는 in과 out으로 나뉨.
들어오는 포트인 in은 비지니스 로직 입장에서 client인 controller가 사용하는 포트.
나가는 포트인 out은 비지니스 로직이 client로서 외부기능(DB)를 사용하기 위한 포트.

##### 적용 : 컨트롤러에 수많은 useCase 주입?
Controller에 ProductUseCase를 하나 주입받도록 만드는게 나은지 아니면 AddProductUseCase, .... 각각 이렇게 주입하는게 맞을지 고민했다.
결과는 SOLID 원칙의 ISP원칙(클라이언트는 자신이 사용하지 않는 메서드에 의존하도록 강요받으면 안됨)을 근거로 2안을 택함.
단점은 보일러플레이트 코드의 증가지만 추후 lombok을 사용하면 완화 가능하다고 생각했음.

##### 적용 : ProductMapper
DDD에서 개념적으로 비지니스로직은 도메인계층에 두어야 한다(실무적인지 아닌지는 PASS)
비지니스 로직은 이런 것이라고 생각한다. 
Dto Entity 매핑은 비지니스로직이 아니라 단순한 기술적 관심사로 보고 도메인과 분리했다.

##### 개념 : 헥사고날 아키텍처
input port = primary port = Driving port = Inbound port
어플리케이션을 호출 즉, 작동시키는 쪽, 또는 불러들이는 쪽이라 이렇게 부름

output port = secondary port = Driven port = outbound Port
DB와 같은 외부 자원을 사용하므로 이렇게 부름

infrastructure layer
구현체들이 있는 곳을 말한다.
인터페이스가 아닌 기술적인 내용이 있는 곳이다. 
http, jdbc와 분리되어있다.

인프라스트럭처 레이어는 어플리케이션과 도메인을 의존함. . 어댑터가 위치하고, Spring Controller와 jdbcClient 같은 구체적인 기술을 사용.

어플리케이션 레이어는 도메인을 의존함. 인프라에는 의존하지 않으며 포트를 사용함.

도메인은 아무것도 의존하지 않음. 비지니스 로직이 위치해야 함.

##### 적용 : 헥사고날 아키텍처 구조 적용

Client ──HTTP──▶
🔵  Primary Adapter              (Infrastructure Layer)
    ProductController            @RestController  
    └─ calls **Input Port / Use-case**

   ▼
🟢  Application Layer
    • AddProductUseCase (interface)  
    • ProductInteractor (impl) ─ orchestrates Domain & Ports

   ▼
🟡  Domain Layer
    • Product (Entity)  
    • Value Objects  (아직 필요x)
    • ProductDomainService (아직 필요X)

   ▼
🔴  Output Port                  (Application Layer)
    ProductPersistencePort (interface)

   ▼
🔵  Secondary Adapter            (Infrastructure Layer)
    ProductPersistenceAdapter    @Repository  
    └─ JDBC

현재 비지니스 로직이라고 판단될만 한 것이 없다고 생각하여 도메인에 비지니스 로직이 위치하지 않음.  기본 CRUD와 Dto<->Entity 변환은 비지니스로직이 아니라고 생각했음
##### 개념 : 전통적인 계층 vs DDD/CA
전통적인 계층은 서비스 계층에 비지니스 로직을 두지만,
DDD/CA(도메인 드라이븐 디자인, 클린아키텍처)에서는 도메인 계층에 비지니스 로직을 둔다.
##### 개념 : SOLID 원칙이란

- S - 단일 책임 원칙 (Single Responsibility Principle)
> 클래스는 오로지 하나의 변경 이유만을 가져야 한다.

- O - 개방-폐쇄 원칙 (Open/Closed Principle)
> 소프트웨어 개체(클래스, 모듈 등)는 확장에는 열려 있어야 하고, 수정에는 닫혀 있어야 한다.

- L - 리스코프 치환 원칙 (Liskov Substitution Principle)
> 하위 타입은 언제나 자신의 기반(부모) 타입으로 교체될 수 있어야 한다.

- I - 인터페이스 분리 원칙 (Interface Segregation Principle)
> 클라이언트는 자신이 사용하지 않는 메서드에 의존하도록 강요받아서는 안 된다.

- D - 의존성 역전 원칙 (Dependency Inversion Principle)
> 상위 수준 모듈은 하위 수준 모듈에 의존해서는 안 되며, 둘 모두 추상화(인터페이스)에 의존해야 한다.

### 2025.07.07 ~ 2025.07.09

시간이 없어서 풀리퀘스트 본문 작성한 것을 Ai에게 전달하여 Docs 작성

##### 적용 : 멤버 도메인 추가 및 JWT 인증/인가 시스템 구현

1. 멤버 도메인 헥사고날 아키텍처 적용
- `MemberPersistenceAdapter`: 멤버의 세컨더리 어댑터 계층인 퍼시스턴스 계층 구현
- `RegisterMemberUseCase`, `LoginMemberUseCase`: 멤버의 어플리케이션 계층의 비지니스 흐름을 담당하는 UseCase 인터페이스 구현
- `MemberInteractor`: UseCase 인터페이스의 구현체로 실제 비지니스 로직 오케스트레이션 담당
- `PasswordEncoder` 인터페이스와 `SimplePasswordEncoder` 구현체: 회원가입 시 비밀번호 해시화를 위한 의존성 역전 적용

2. JWT 기반 인증/인가 시스템 구현
- `JwtTokenProvider`: JWT 토큰 생성, 검증, 정보추출을 처리하는 유틸리티 클래스
- `JwtTokenPort`: 토큰 유틸의 인터페이스로 의존성 역전 적용
- `JwtAuthenticationFilter`: 서블릿 필터에서 JWT 토큰을 통한 1차 권한 검사 수행
- `AuthorizationAspect`: AOP를 활용한 관리자용 API 접근 권한 검사
- `RequireAdmin`: 관리자용 API 지정을 위한 커스텀 어노테이션
- `WebConfig`: AuthorizationAspect 등록 및 설정

3. 컨트롤러 계층 확장
- `MemberController`: 회원가입, 로그인 기능 제공
- `AdminController`: 관리자 전용 CRUD 기능 제공

4. 예외 처리 및 응답 표준화
- `ErrorCode`에 401(UNAUTHORIZED), 403(FORBIDDEN) 추가
- `ForbiddenException`, `UnauthorizedException` 커스텀 예외 추가
- `GlobalExceptionHandler`에서 새로운 예외들 처리
- ErrorResponse → ProblemDetail 리팩토링: RFC 7807 웹 API 에러 응답 국제 표준 준수

```
ErrorResponse 방식 (Spring 전용):
{
  "timestamp": "2025-01-09T14:15:16.123Z",
  "status": 400,
  "error": "Bad Request", 
  "message": "입력하신 정보를 다시 확인해주세요",
  "code": "VALIDATION_FAILED",
  "path": "/api/products"
}

ProblemDetail 방식 (RFC 7807 표준):
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "입력하신 정보를 다시 확인해주세요",
  "instance": "/api/products", 
  "code": "VALIDATION_FAILED",
  "path": "/api/products"
}
```

##### 맞닥뜨린 문제 및 해결 과정

1. PasswordEncoder 설계 문제
- 문제: Spring Security를 사용하지 않기로 결정하여 직접 패스워드 인코더 구현 필요
- 해결: `PasswordEncoder` 인터페이스와 `SimplePasswordEncoder` 구현체로 의존성 역전 적용
- 근거: `MemberInteractor`가 구체적인 패스워드 인코딩 구현체를 알지 못하게 하여 변경에 유연하게 대응

2. 테스트 환경에서 JWT 빈 생성 문제
- 문제: `@WebMvcTest`는 웹 계층만 로드하므로 `JwtAuthenticationFilter`가 의존하는 `JwtTokenPort` 빈이 Context에 없어서 에러 발생
- 시도한 해결 방법들:
  1. `@TestConfiguration` 사용 → 매번 import 필요로 불편
  2. `@MockitoBean` 사용 → 매 테스트마다 추가 필요로 불편
  3. 최종 채택: 스프링 프로필 기반 조건부 빈 등록
- 해결책: 
  - 프로덕션: `@Profile("!test")` + `@ConditionalOnProperty(jwt.enabled=true)`로 실제 JWT 컴포넌트 로드
  - 테스트: `@Profile("test")` + `NoOpJwtTokenProvider`로 테스트용 구현체 제공

3. MemberInteractor의 JWT 의존성 문제
- 문제: `MemberInteractor`가 JWT 토큰 생성을 위해 `JwtTokenPort` 의존성 필요
- 해결: 인터페이스 의존으로 변경하고 테스트 환경에서는 `NoOpJwtTokenProvider` 제공

##### 중점 리뷰 포인트

1. JwtAuthenticationFilter를 통한 관리자용 API 보호 메커니즘
   - 서블릿 필터 레벨에서의 1차 인증 검증
   - 토큰 유효성 검사 및 사용자 정보 추출

2. JwtTokenProvider의 토큰 생성/검증/추출 로직
   - JWT 토큰 생성 시 필요한 Claims 설정
   - 토큰 검증 시 예외 처리 및 결과 객체 설계
   - 토큰에서 사용자 정보 추출 로직의 안전성

3. 헥사고날 아키텍처 적용의 일관성
   - 멤버 도메인이 기존 상품 도메인과 동일한 구조로 구현되었는지
   - 포트와 어댑터 패턴의 올바른 적용

##### 추후 개선 계획

1. Auth 도메인 분리: 인증/인가 관련 로직을 회원 도메인에서 분리하여 독립적인 Auth 도메인 생성
2. 관리자 화면 구현: 회원을 조회, 추가, 수정, 삭제할 수 있는 관리자 전용 웹 인터페이스 구현 (선택사항)
3. JWT 토큰 만료 정책: Refresh Token 도입 및 토큰 갱신 메커니즘 구현