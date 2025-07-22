#spring-gift-wishlist
## 0단계 기본 코드 준비
## 1단계 유효성 검사 및 예외 처리
상품을 추가하거나 수정하는 경우
잘못된 값이 전달되면 클라이언트가 어떤 부분이 왜 잘못되었는지 인지할 수 있도록 응답을 제공한다.

1. 상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있다.
2. 특수 문자 가능: ( ), [ ], +, -, &, /, _(그 외에는 불가)
3. "카카오"가 포함된 문구는 담당 MD와 협의한 경우에만 사용할 수 있다.

# spring-gift-product
## 1단계

1. 상품 추가 기능
POST /api/products

2. 특정 상품 조회 기능
GET /api/products/{productId}

3. 모든 상품 조회 기능
상품 목록 조회 (페이지네이션 적용)	모든 상품의 목록을 페이지 단위로 조회한다(추후 예정)
GET	/api/products?page=0&size=10&sort=name,asc&categoryId=1	

4. 상품 수정 기능
PUT	/api/products/{productId}

5. 상품 삭제 기능
DELETE /api/products/{productId}

## 2단계

1. 관리자 상품 추가 기능

2. 관리자 모든 상품 조회 기능
상품 목록 조회 (페이지네이션 적용)	모든 상품의 목록을 페이지 단위로 조회한다(추후 예정)

3. 관리자 상품 수정 기능

4. 관리자 상품 삭제 기능

## 3단계

1. h2 데이터베이스 환경설정

    - build.gradle에 h2 데이터베이스 의존성 추가
    - application.yml에 h2 데이터베이스 설정 추가
      - H2 콘솔 활성화
      - JDBC URL,username,password 설정

2. 스키마 스크립트, 데이터 스크립트 생성

    - src/main/resources/schema.sql

3. CREATE 기능 구현(JdbcClient)

4. READ 기능 구현(JdbcClient)

5. UPDATE 기능 구현(JdbcClient)

6. DELETE 기능 구현(JdbcClient)

# spring-gift-wishlist

## 1단계

1. 유효성 검사 및 에러처리
   - 상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있다.
   - 특수 문자 가능: ( ), [ ], +, -, &, /, _(그 외에는 불가)
   - "카카오"가 포함된 문구는 담당 MD와 협의한 경우에만 사용할 수 있다.


## 2단계 회원 로그인

### 기능 목록
1. 회원 가입 기능
   - POST /api/members/signUp
   - 이메일, 비밀번호로 회원 가입
   - 가입 성공 시 JWT 토큰 반환

2. 로그인 기능
   - POST /api/members/login
   - 이메일, 비밀번호로 로그인
   - 로그인 성공 시 JWT 토큰 반환

3.회원 데이터베이스 관리
   - Member 테이블 생성 (id, email, password, created_at)
   - 이메일 중복 검사
   - 비밀번호 암호화 (BCrypt)

4.JWT 토큰 관리
   - 토큰 생성
   - 토큰 검증
   - 토큰 정보 추출

## 3단계 - 위시 리스트

### 기능 목록
1. 위시리스트에 상품 추가
2. 위시리스트에 등록된 상품 목록 조회
3. 위시 리스트에 담긴 상품을 삭제

### 참고 사항
- 로그인 후 받은 토큰을 사용하여 사용자별 위시 리스트 기능을 구현한다.
- 사용자 정보는 요청 헤더의 Authorization 필드를 사용한다.
   - 예시: `Authorization: Bearer <token>`
- HandlerMethodArgumentResolver를 활용해 컨트롤러 메서드에 로그인 회원 객체를 주입한다.

# spring-gift-enhancement

## 1단계 - 엔티티 매핑(JPA 적용)

현재 도메인이 객체지향적이지 않았음. 그걸 깨닫고 리팩토링하기
JdbcClient를 사용하는 방식에서 JPA를 사용하는 것으로 리팩토링을 해보며, 객체지향적으로 도메인을 매핑한다.
객체의 참조와 테이블의 외래 키를 매핑해서 객체에서는 참조를 사용하고 테이블에서는 외래 키를 사용할 수 있도록 한다.
@DataJpaTest를 사용하여 학습 테스트를 해 본다.
0. 의존성 추가 및 개발 컨벤션 수립(폴더 구조 및 클래스 명 규칙)

1. Member 엔티티 매핑
   - 회원 엔티티를 정의하고, JPA 어노테이션을 사용하여 매핑
2. Product 엔티티 매핑
   - 상품 엔티티를 정의하고, JPA 어노테이션을 사용하여 매핑
3. Wishlist 엔티티 매핑
   - 위시리스트 엔티티를 정의하고, JPA 어노테이션을 사용하여 매핑
4. @DataJpaTest를 사용한 학습 테스트
   - JPA를 사용하여 데이터베이스와의 상호작용을 테스트

## 2단계 - 페이지네이션 구현
