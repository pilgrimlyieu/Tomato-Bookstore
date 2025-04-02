DROP TABLE IF EXISTS specifications;

DROP TABLE IF EXISTS stockpiles;

DROP TABLE IF EXISTS products;

DROP TABLE IF EXISTS users;

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    address VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- 创建商品表
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    rate INT NOT NULL,
    description TEXT,
    cover VARCHAR(255),
    detail TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- 创建库存表
CREATE TABLE stockpiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    amount INT NOT NULL,
    frozen INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id)
);

-- 创建商品规格表
CREATE TABLE specifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    item VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id)
);

-- 为外键创建索引以提高查询性能
CREATE INDEX idx_stockpile_product ON stockpiles (product_id);

CREATE INDEX idx_specification_product ON specifications (product_id);

CREATE INDEX idx_user_username ON users (username);
