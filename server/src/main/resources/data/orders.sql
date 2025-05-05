-- 检查表是否已有数据，避免重复插入
SET @order_count = ( SELECT COUNT(*) FROM orders );

-- 1. 首先创建未支付订单
INSERT INTO
    orders (user_id, total_amount, payment_method, status, shipping_address, created_at, updated_at)
SELECT
    u.id,
    p.price * FLOOR(RAND() * 3) + 1,
    'ALIPAY',
    'PENDING',
    CASE
        WHEN u.username = 'customer_user' THEN '北京市海淀区清华大学'
        WHEN u.username = 'customer_user_1' THEN '上海市浦东新区张江高科技园区'
        WHEN u.username = 'customer_user_2' THEN '广州市天河区体育中心'
        WHEN u.username = 'orderuser' THEN '西安市雁塔区大唐西市'
        ELSE '默认地址'
    END,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY)
FROM
    users u
    CROSS JOIN products p
WHERE
    u.username IN ('customer_user', 'customer_user_1', 'orderuser')
    AND p.id = (SELECT id FROM products ORDER BY RAND() LIMIT 1)
    AND @order_count = 0
LIMIT 3;

-- 2. 创建已支付订单
INSERT INTO
    orders (user_id, total_amount, payment_method, status, shipping_address, trade_no, payment_time, created_at, updated_at)
SELECT
    u.id,
    p.price * FLOOR(RAND() * 3) + 1,
    'ALIPAY',
    'PAID',
    CASE
        WHEN u.username = 'customer_user' THEN '北京市海淀区清华大学'
        WHEN u.username = 'customer_user_1' THEN '上海市浦东新区张江高科技园区'
        WHEN u.username = 'customer_user_2' THEN '广州市天河区体育中心'
        WHEN u.username = 'orderuser' THEN '西安市雁塔区大唐西市'
        ELSE '默认地址'
    END,
    CONCAT('2023112722001', FLOOR(RAND() * 1000000000)),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 5) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 5) DAY)
FROM
    users u
    CROSS JOIN products p
WHERE
    u.username IN ('customer_user_1', 'customer_user_2', 'orderuser')
    AND p.id = (SELECT id FROM products ORDER BY RAND() LIMIT 1)
    AND @order_count = 0
LIMIT 3;

-- 3. 创建已取消订单
INSERT INTO
    orders (user_id, total_amount, payment_method, status, shipping_address, created_at, updated_at)
SELECT
    u.id,
    p.price * FLOOR(RAND() * 3) + 1,
    'ALIPAY',
    'CANCELLED',
    CASE
        WHEN u.username = 'customer_user' THEN '北京市海淀区清华大学'
        WHEN u.username = 'customer_user_1' THEN '上海市浦东新区张江高科技园区'
        WHEN u.username = 'customer_user_2' THEN '广州市天河区体育中心'
        WHEN u.username = 'orderuser' THEN '西安市雁塔区大唐西市'
        ELSE '默认地址'
    END,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 15) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 14) DAY)
FROM
    users u
    CROSS JOIN products p
WHERE
    u.username IN ('customer_user', 'customer_user_2', 'orderuser')
    AND p.id = (SELECT id FROM products ORDER BY RAND() LIMIT 1)
    AND @order_count = 0
LIMIT 2;

-- 4. 为订单创建订单项关联
-- 首先获取已创建的订单和购物车项
INSERT INTO
    carts_orders_relation (cart_id, order_id, quantity)
SELECT
    c.id,
    o.id,
    c.quantity
FROM
    orders o
    JOIN users u ON o.user_id = u.id
    JOIN carts c ON c.user_id = u.id
WHERE
    o.id IN (SELECT id FROM orders)
    AND NOT EXISTS (
        SELECT 1 FROM carts_orders_relation cor
        WHERE cor.order_id = o.id
    )
LIMIT 10;  -- 限制关联数量，避免数据过多

-- 更新订单总金额以匹配实际订单项
UPDATE orders o
SET o.total_amount = (
    SELECT COALESCE(SUM(p.price * cor.quantity), o.total_amount)
    FROM carts_orders_relation cor
    JOIN carts c ON cor.cart_id = c.id
    JOIN products p ON c.product_id = p.id
    WHERE cor.order_id = o.id
)
WHERE EXISTS (
    SELECT 1 FROM carts_orders_relation cor
    WHERE cor.order_id = o.id
);

-- 5. 创建已超时订单
INSERT INTO
    orders (user_id, total_amount, payment_method, status, shipping_address, created_at, updated_at)
SELECT
    u.id,
    p.price * FLOOR(RAND() * 2) + 1,
    'ALIPAY',
    'TIMEOUT',
    CASE
        WHEN u.username = 'customer_user' THEN '北京市海淀区清华大学'
        WHEN u.username = 'customer_user_1' THEN '上海市浦东新区张江高科技园区'
        ELSE '默认地址'
    END,
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM
    users u
    CROSS JOIN products p
WHERE
    u.username IN ('customer_user', 'customer_user_1')
    AND p.id = (SELECT id FROM products ORDER BY RAND() LIMIT 1)
    AND @order_count = 0
LIMIT 2;
