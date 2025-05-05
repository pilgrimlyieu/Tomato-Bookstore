-- 检查表是否已有数据，避免重复插入
SET @user_count = ( SELECT COUNT(*) FROM users );

-- 只有在没有数据时才插入
-- 用户数据
-- 初始密码：password123
INSERT INTO
    users (
        username,
        password,
        email,
        phone,
        avatar,
        address,
        role,
        created_at,
        updated_at
    )
SELECT *
FROM (
        SELECT 'admin_user', '$2a$10$C/TgJT0E./j3ltVhuzDWheDO0bvxbz6f2MDfeLJL6evs0r5HMNufK', 'admin@tomato.com', '13800000000', 'https://i.pravatar.cc/150?img=1', '北京市海淀区', 'ADMIN', NOW(), NOW()
        UNION ALL
        SELECT 'customer_user', '$2a$10$C/TgJT0E./j3ltVhuzDWheDO0bvxbz6f2MDfeLJL6evs0r5HMNufK', 'customer@tomato.com', '13800000001', 'https://i.pravatar.cc/150?img=2', '上海市静安区', 'CUSTOMER', NOW(), NOW()
        UNION ALL
        SELECT 'customer_user_1', '$2a$10$C/TgJT0E./j3ltVhuzDWheDO0bvxbz6f2MDfeLJL6evs0r5HMNufK', 'customer1@tomato.com', '13800000002', 'https://i.pravatar.cc/150?img=3', '广州市天河区', 'CUSTOMER', NOW(), NOW()
        UNION ALL
        SELECT 'customer_user_2', '$2a$10$C/TgJT0E./j3ltVhuzDWheDO0bvxbz6f2MDfeLJL6evs0r5HMNufK', 'customer2@tomato.com', '13800000003', 'https://i.pravatar.cc/150?img=4', '深圳市南山区', 'CUSTOMER', NOW(), NOW()
        UNION ALL
        SELECT 'customer_user_3', '$2a$10$C/TgJT0E./j3ltVhuzDWheDO0bvxbz6f2MDfeLJL6evs0r5HMNufK', 'customer3@tomato.com', '13800000004', 'https://i.pravatar.cc/150?img=5', '成都市高新区', 'CUSTOMER', NOW(), NOW()
        UNION ALL
        SELECT 'cartuser', '$2a$10$C/TgJT0E./j3ltVhuzDWheDO0bvxbz6f2MDfeLJL6evs0r5HMNufK', 'cartuser@tomato.com', '13800000007', 'https://i.pravatar.cc/150?img=8', '武汉市洪山区', 'CUSTOMER', NOW(), NOW()
        UNION ALL
        SELECT 'orderuser', '$2a$10$C/TgJT0E./j3ltVhuzDWheDO0bvxbz6f2MDfeLJL6evs0r5HMNufK', 'orderuser@tomato.com', '13800000008', 'https://i.pravatar.cc/150?img=9', '西安市雁塔区', 'CUSTOMER', NOW(), NOW()
    ) AS new_users (
        username, password, email, phone, avatar, address, role, created_at, updated_at
    )
WHERE
    @user_count = 0 AND NOT EXISTS (
        SELECT 1
        FROM users
        WHERE username IN ('cartuser', 'orderuser')
    );
