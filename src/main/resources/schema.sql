
DROP TABLE IF EXISTS Wish;
DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS Member;

CREATE TABLE Product(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    image_url VARCHAR(255)
);

CREATE TABLE Member(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Wish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES Member(id),
    FOREIGN KEY (product_id) REFERENCES Product(id),
    UNIQUE (member_id, product_id)
);

alter table if exists wish
    add constraint fk_wish_member_id_ref_member_id
    foreign key (member_id)
    references member;

alter table if exists wish
    add constraint fk_wish_product_id_ref_product_id
    foreign key (product_id)
    references product;