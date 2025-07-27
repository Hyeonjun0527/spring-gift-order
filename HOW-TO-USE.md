-- 관리자 계정
-- 이메일: admin@admin.com
-- 비밀번호: 1234
-- 멤버로 먼저 회원가입하고
-- 관리자 계정으로 접속하면 멤버를 관리하는 관리자 기능까지 테스트 해볼 수 있습니다.
-- 테스트 가능한 기능
-- 상품 추가, 
-- 관리자페이지에서 테이블의 id,상품명, 가격으로 상품 정렬 기능 사용가능

-- 카카오 로그인
테스트 방법 아래 링크로 접속. 하지만 API 키가 비공개
https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={REST_API_키}&redirect_uri=http://localhost:8080&scope=profile_nickname,talk_message
