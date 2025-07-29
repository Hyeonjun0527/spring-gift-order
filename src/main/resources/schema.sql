
DROP TABLE IF EXISTS Wish;
DROP TABLE IF EXISTS "Order";
DROP TABLE IF EXISTS "option";
DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS Member;

CREATE TABLE Product(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    image_url VARCHAR(255)
);

CREATE TABLE "option"(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    product_id BIGINT,
    FOREIGN KEY (product_id) REFERENCES Product(id),
    UNIQUE (product_id, name)
);

CREATE TABLE Member(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    kakao_id BIGINT UNIQUE,
    kakao_access_token VARCHAR(255),
    kakao_refresh_token VARCHAR(255)
);

CREATE TABLE Wish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    option_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES Member(id),
    FOREIGN KEY (product_id) REFERENCES Product(id),
    UNIQUE (member_id, option_id)
);

CREATE TABLE "Order" (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    option_name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    quantity INT NOT NULL,
    message VARCHAR(255),
    order_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES Member(id),
    FOREIGN KEY (option_id) REFERENCES "option"(id)
);