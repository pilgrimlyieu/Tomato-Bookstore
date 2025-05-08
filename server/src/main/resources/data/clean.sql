-- 清理测试数据
DELETE FROM specifications
WHERE
    product_id IN (
        SELECT id
        FROM products
        WHERE
            title LIKE 'test_%'
    );

DELETE FROM stockpiles
WHERE
    product_id IN (
        SELECT id
        FROM products
        WHERE
            title LIKE 'test_%'
    );

DELETE FROM advertisements
WHERE
    product_id IN (
        SELECT id
        FROM products
        WHERE
            title LIKE 'test_%'
    );

-- 清理笔记评论数据
DELETE FROM note_comments
WHERE
    note_id IN (
        SELECT id
        FROM notes
        WHERE
            title LIKE 'test_%'
            OR user_id IN (
                SELECT id
                FROM users
                WHERE username LIKE 'test_%'
            )
    );

-- 清理笔记反馈数据
DELETE FROM note_feedbacks
WHERE
    note_id IN (
        SELECT id
        FROM notes
        WHERE
            title LIKE 'test_%'
            OR user_id IN (
                SELECT id
                FROM users
                WHERE username LIKE 'test_%'
            )
    );

-- 清理笔记数据
DELETE FROM notes
WHERE
    title LIKE 'test_%'
    OR user_id IN (
        SELECT id
        FROM users
        WHERE username LIKE 'test_%'
    )
    OR product_id IN (
        SELECT id
        FROM products
        WHERE title LIKE 'test_%'
    );

DELETE FROM carts_orders_relation
WHERE
    cart_id IN (
        SELECT c.id
        FROM carts c
        JOIN users u ON c.user_id = u.id
        WHERE u.username LIKE 'test_%'
    );

DELETE FROM carts
WHERE
    user_id IN (
        SELECT id
        FROM users
        WHERE username LIKE 'test_%'
    );

DELETE FROM orders
WHERE
    user_id IN (
        SELECT id
        FROM users
        WHERE username LIKE 'test_%'
    );

DELETE FROM products WHERE title LIKE 'test_%';

DELETE FROM users WHERE username LIKE 'test_%';
