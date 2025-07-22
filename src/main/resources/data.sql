-- 관리자 계정
-- 이메일: admin@example.com
-- 비밀번호: 1234
INSERT INTO member (email, password, role, created_at) VALUES 
('admin@admin.com', 'A6xnQhbz4Vx2HuGl4lXwZ5U2I8iziLRFnhP5eNfIRvQ=', 'ADMIN', CURRENT_TIMESTAMP);

-- 테스트용 상품 데이터
INSERT INTO product (name, price, image_url) VALUES
('카카오 라이언 인형', 15000, 'https://example.com/ryan.jpg'),
('카카오 어피치 인형', 12000, 'https://example.com/apeach.jpg'),
('무지 머그컵', 8000, 'https://example.com/muzi-cup.jpg'),
('프로도 키링', 5000, 'https://example.com/frodo-keyring.jpg'),
('네오 쿠션', 20000, 'https://example.com/neo-cushion.jpg'); 