DROP TABLE IF EXISTS note_comments;
DROP TABLE IF EXISTS note_feedbacks;
DROP TABLE IF EXISTS notes;
DROP TABLE IF EXISTS carts_orders_relation;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS specifications;
DROP TABLE IF EXISTS stockpiles;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS advertisements;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;


-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255) NOT NULL UNIQUE,
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
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

-- 创建商品规格表
CREATE TABLE specifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    item VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

-- 创建购物车表
CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车商品 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID，关联用户表',
    product_id BIGINT NOT NULL COMMENT '商品 ID，关联商品表',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量，默认为 1',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) COMMENT='购物车商品表';

-- 创建订单表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    payment_method VARCHAR(50) NOT NULL COMMENT '支付方式',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单支付状态（PENDING, PAID, CANCELLED, TIMEOUT）',
    shipping_address TEXT NOT NULL COMMENT '收货地址',
    trade_no VARCHAR(64) COMMENT '支付宝交易号',
    payment_time TIMESTAMP COMMENT '支付时间',
    created_at TIMESTAMP NOT NULL COMMENT '订单创建时间',
    updated_at TIMESTAMP COMMENT '订单更新时间',
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) COMMENT='订单表';

-- 创建购物车商品与订单关联表
CREATE TABLE carts_orders_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
    cart_id BIGINT NOT NULL COMMENT '关联购物车商品 ID',
    order_id BIGINT NOT NULL COMMENT '关联订单 ID',
    quantity INT NOT NULL COMMENT '购买数量',
    FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    UNIQUE KEY uk_cart_order (cart_id, order_id) COMMENT '唯一索引，确保购物车商品与订单的唯一关联'
) COMMENT='购物车商品与订单关联表';

-- 创建广告表
CREATE TABLE advertisements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '广告 ID',
    title VARCHAR(50) NOT NULL COMMENT '广告标题，不允许为空',
    content VARCHAR(500) NOT NULL COMMENT '广告内容',
    image_url VARCHAR(500) NOT NULL COMMENT '广告图片 URL',
    product_id BIGINT NOT NULL COMMENT '所属商品 ID，不允许为空',
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) COMMENT='广告表';

-- 创建书评表
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '书评 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID，关联商品表',
    user_id BIGINT NOT NULL COMMENT '用户 ID，关联用户表',
    rating INT NOT NULL COMMENT '评分（0-10）' CHECK (rating BETWEEN 0 AND 10),
    content TEXT COMMENT '评论内容',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE KEY uk_product_user (product_id, user_id) COMMENT '一个用户对一本书只能有一个评价'
) COMMENT='书评表';

-- 创建读书笔记表
CREATE TABLE notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '笔记 ID',
    title VARCHAR(255) NOT NULL COMMENT '笔记标题',
    content TEXT NOT NULL COMMENT '笔记内容',
    product_id BIGINT NOT NULL COMMENT '书籍 ID，关联商品表',
    user_id BIGINT NOT NULL COMMENT '用户 ID，关联用户表',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE KEY uk_product_user (product_id, user_id) COMMENT '一个用户对一本书只能有一个笔记'
) COMMENT='读书笔记表';

-- 创建读书笔记反馈表
CREATE TABLE note_feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '反馈 ID',
    note_id BIGINT NOT NULL COMMENT '笔记 ID，关联笔记表',
    user_id BIGINT NOT NULL COMMENT '用户 ID，关联用户表',
    feedback_type ENUM('LIKE', 'DISLIKE') NOT NULL COMMENT '反馈类型：点赞或点踩',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE KEY uk_note_user (note_id, user_id) COMMENT '一个用户对一个笔记只能有一个反馈'
) COMMENT='读书笔记反馈表';

-- 创建读书笔记评论表
CREATE TABLE note_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论 ID',
    note_id BIGINT NOT NULL COMMENT '笔记 ID，关联笔记表',
    user_id BIGINT NOT NULL COMMENT '用户 ID，关联用户表',
    content TEXT NOT NULL COMMENT '评论内容',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) COMMENT='读书笔记评论表';

-- 为外键创建索引以提高查询性能
CREATE INDEX idx_stockpile_product ON stockpiles (product_id);
CREATE INDEX idx_specification_product ON specifications (product_id);
CREATE INDEX idx_user_username ON users (username);
CREATE INDEX idx_cart_user ON carts (user_id);
CREATE INDEX idx_cart_product ON carts (product_id);
CREATE INDEX idx_order_user ON orders (user_id);
CREATE INDEX idx_relation_cart ON carts_orders_relation (cart_id);
CREATE INDEX idx_relation_order ON carts_orders_relation (order_id);
CREATE INDEX idx_advertisement_product ON advertisements (product_id);
CREATE INDEX idx_review_user ON reviews (user_id);
CREATE INDEX idx_product_rate ON products (rate);
CREATE INDEX idx_review_product_created ON reviews (product_id, created_at DESC);
CREATE INDEX idx_note_user ON notes (user_id);
CREATE INDEX idx_note_product ON notes (product_id);
CREATE INDEX idx_note_feedback_note ON note_feedbacks (note_id);
CREATE INDEX idx_note_feedback_user ON note_feedbacks (user_id);
CREATE INDEX idx_note_comment_note ON note_comments (note_id);
CREATE INDEX idx_note_comment_user ON note_comments (user_id);
