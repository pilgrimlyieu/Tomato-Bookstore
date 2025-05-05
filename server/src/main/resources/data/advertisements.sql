-- 检查广告表是否有数据
SET @advertisement_count = (SELECT COUNT(*) FROM advertisements);

-- 1. 添加《活着》的广告
INSERT INTO advertisements (title, content, image_url, product_id)
SELECT '热销图书：活着', '余华代表作，一本直击人心的生命之书，讲述了农村人福贵悲惨的人生', 'https://img3.doubanio.com/view/subject/l/public/s29053580.jpg', id
FROM products
WHERE title = '活着' AND @advertisement_count = 0
LIMIT 1;

-- 2. 添加《三体》的广告
INSERT INTO advertisements (title, content, image_url, product_id)
SELECT '科幻巨著：三体', '刘慈欣的科幻巨著，讲述人类文明与三体文明的壮阔故事', 'https://img2.doubanio.com/view/subject/l/public/s2768378.jpg', id
FROM products
WHERE title = '三体' AND @advertisement_count = 0
LIMIT 1;

-- 3. 添加《流畅的 Python》的广告
INSERT INTO advertisements (title, content, image_url, product_id)
SELECT 'Python 进阶指南', '全面介绍 Python3 的新特性和编程技巧，Python 爱好者必读', 'https://img3.doubanio.com/view/subject/l/public/s29434304.jpg', id
FROM products
WHERE title = '流畅的 Python' AND @advertisement_count = 0
LIMIT 1;

-- 4. 添加《深入理解计算机系统》的广告
INSERT INTO advertisements (title, content, image_url, product_id)
SELECT '计算机系统深度解析', '透彻讲解计算机系统的工作原理，程序员必备', 'https://img1.doubanio.com/view/subject/l/public/s29195878.jpg', id
FROM products
WHERE title = '深入理解计算机系统' AND @advertisement_count = 0
LIMIT 1;
