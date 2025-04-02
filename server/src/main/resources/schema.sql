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

DELETE FROM products WHERE title LIKE 'test_%';

DELETE FROM users WHERE username LIKE 'test_%';
