CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    price INT NOT NULL,
    image_url VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS wish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1, -- 수량 기본값 1
    CONSTRAINT uk_member_product UNIQUE (member_id, product_id), -- 사용자가 같은 상품을 여러 번 등록하지 못하도록 제약조건 추가
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE, -- 사용자 삭제 시 위시리스트 삭제
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE -- 상품 삭제 시 위시리스트 삭제
);