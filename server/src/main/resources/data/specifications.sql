-- 《活着》规格
INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '余华'
FROM products
WHERE
    title = '活着'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '作家出版社'
FROM products
WHERE
    title = '活着'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, 'ISBN', '9787506365437'
FROM products
WHERE
    title = '活着'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = 'ISBN'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '页数', '192'
FROM products
WHERE
    title = '活着'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '页数'
    );

-- 《三体》规格
INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '刘慈欣'
FROM products
WHERE
    title = '三体'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '重庆出版社'
FROM products
WHERE
    title = '三体'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, 'ISBN', '9787536692930'
FROM products
WHERE
    title = '三体'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = 'ISBN'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '页数', '302'
FROM products
WHERE
    title = '三体'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '页数'
    );

-- 《红楼梦》规格
INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '曹雪芹'
FROM products
WHERE
    title = '红楼梦'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '人民文学出版社'
FROM products
WHERE
    title = '红楼梦'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, 'ISBN', '9787020002207'
FROM products
WHERE
    title = '红楼梦'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = 'ISBN'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '页数', '1606'
FROM products
WHERE
    title = '红楼梦'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '页数'
    );

-- 《平凡的世界》规格
INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '路遥'
FROM products
WHERE
    title = '平凡的世界'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '北京十月文艺出版社'
FROM products
WHERE
    title = '平凡的世界'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, 'ISBN', '9787530212004'
FROM products
WHERE
    title = '平凡的世界'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = 'ISBN'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '页数', '1274'
FROM products
WHERE
    title = '平凡的世界'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '页数'
    );

-- 《JavaScript 高级程序设计》规格
INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '尼古拉斯·泽卡斯'
FROM products
WHERE
    title = 'JavaScript 高级程序设计'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '人民邮电出版社'
FROM products
WHERE
    title = 'JavaScript 高级程序设计'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, 'ISBN', '9787115545381'
FROM products
WHERE
    title = 'JavaScript 高级程序设计'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = 'ISBN'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '页数', '888'
FROM products
WHERE
    title = 'JavaScript 高级程序设计'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '页数'
    );

-- 为剩余的书籍添加基本规格（作者、出版社、ISBN、页数）
INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '托马斯·科尔曼等'
FROM products
WHERE
    title = '算法导论'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '机械工业出版社'
FROM products
WHERE
    title = '算法导论'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '尤瓦尔·赫拉利'
FROM products
WHERE
    title = '人类简史'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '中信出版社'
FROM products
WHERE
    title = '人类简史'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '安东尼·德·圣埃克苏佩里'
FROM products
WHERE
    title = '小王子'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '人民文学出版社'
FROM products
WHERE
    title = '小王子'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '兰德尔·布莱恩特等'
FROM products
WHERE
    title = '深入理解计算机系统'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '机械工业出版社'
FROM products
WHERE
    title = '深入理解计算机系统'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '加西亚·马尔克斯'
FROM products
WHERE
    title = '百年孤独'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '南海出版公司'
FROM products
WHERE
    title = '百年孤独'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '钱钟书'
FROM products
WHERE
    title = '围城'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '人民文学出版社'
FROM products
WHERE
    title = '围城'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', 'Luciano Ramalho'
FROM products
WHERE
    title = '流畅的 Python'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '人民邮电出版社'
FROM products
WHERE
    title = '流畅的 Python'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '亨利·戴维·梭罗'
FROM products
WHERE
    title = '瓦尔登湖'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '译林出版社'
FROM products
WHERE
    title = '瓦尔登湖'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', 'Alfred V. Aho 等'
FROM products
WHERE
    title = '编译原理'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '机械工业出版社'
FROM products
WHERE
    title = '编译原理'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '东野圭吾'
FROM products
WHERE
    title = '白夜行'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '南海出版公司'
FROM products
WHERE
    title = '白夜行'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '作者', '史蒂芬·霍金'
FROM products
WHERE
    title = '时间简史'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '作者'
    );

INSERT INTO
    specifications (product_id, item, value)
SELECT id, '出版社', '湖南科学技术出版社'
FROM products
WHERE
    title = '时间简史'
    AND NOT EXISTS (
        SELECT 1
        FROM specifications
        WHERE
            product_id = products.id
            AND item = '出版社'
    );
