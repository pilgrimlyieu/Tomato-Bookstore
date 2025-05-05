-- 检查表是否已有数据，避免重复插入
SET @cart_count = ( SELECT COUNT(*) FROM carts );

RAND(123456789); -- 设置随机种子，确保每次生成的随机数相同

-- 添加购物车测试数据
INSERT INTO
    carts (
        user_id,
        product_id,
        quantity,
        created_at,
        updated_at
    )
SELECT u.id, p.id, FLOOR(RAND() * 5) + 1, NOW(), NOW()
FROM users u
    CROSS JOIN (
        SELECT id
        FROM products
        ORDER BY RAND()
        LIMIT 3
    ) p
WHERE
    u.username IN (
        'customer_user',
        'customer_user_1',
        'customer_user_2',
        'cartuser',
        'orderuser'
    )
    AND @cart_count = 0
    AND NOT EXISTS (
        SELECT 1
        FROM carts
        WHERE
            user_id = u.id
            AND product_id = p.id
    );
