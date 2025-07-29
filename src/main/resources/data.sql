-- 관리자 계정
-- 이메일: admin@example.com
-- 비밀번호: 1234
INSERT INTO member (email, password, role, created_at, kakao_id) VALUES
('admin@admin.com', 'A6xnQhbz4Vx2HuGl4lXwZ5U2I8iziLRFnhP5eNfIRvQ=', 'ADMIN', CURRENT_TIMESTAMP, NULL);

-- 테스트용 상품 데이터
INSERT INTO product (name, price, image_url) VALUES
('카카오 라이언 인형', 15000, 'https://example.com/ryan.jpg'),
('카카오 어피치 인형', 12000, 'https://example.com/apeach.jpg'),
('무지 머그컵', 8000, 'https://example.com/muzi-cup.jpg'),
('프로도 키링', 5000, 'https://example.com/frodo-keyring.jpg'),
('네오 쿠션', 20000, 'https://example.com/neo-cushion.jpg');

-- 테스트용 옵션 데이터
-- 카카오 라이언 인형 (product_id: 1)
INSERT INTO Option (name, quantity, product_id) VALUES ('소형 (20cm)', 50, 1);
INSERT INTO Option (name, quantity, product_id) VALUES ('중형 (30cm)', 30, 1);
INSERT INTO Option (name, quantity, product_id) VALUES ('대형 (40cm)', 15, 1);

-- 카카오 어피치 인형 (product_id: 2)
INSERT INTO Option (name, quantity, product_id) VALUES ('핑크', 25, 2);
INSERT INTO Option (name, quantity, product_id) VALUES ('화이트', 20, 2);
INSERT INTO Option (name, quantity, product_id) VALUES ('라벤더', 10, 2);

-- 무지 머그컵 (product_id: 3)
INSERT INTO Option (name, quantity, product_id) VALUES ('화이트', 100, 3);
INSERT INTO Option (name, quantity, product_id) VALUES ('블루', 80, 3);
INSERT INTO Option (name, quantity, product_id) VALUES ('옐로우', 60, 3);
INSERT INTO Option (name, quantity, product_id) VALUES ('그린', 40, 3);

-- 프로도 키링 (product_id: 4)
INSERT INTO Option (name, quantity, product_id) VALUES ('실버', 200, 4);
INSERT INTO Option (name, quantity, product_id) VALUES ('골드', 150, 4);
INSERT INTO Option (name, quantity, product_id) VALUES ('로즈골드', 100, 4);

-- 네오 쿠션 (product_id: 5)
INSERT INTO Option (name, quantity, product_id) VALUES ('35x35cm', 30, 5);
INSERT INTO Option (name, quantity, product_id) VALUES ('45x45cm', 25, 5);
INSERT INTO Option (name, quantity, product_id) VALUES ('55x55cm', 15, 5); 