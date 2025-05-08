-- 检查表是否已有数据，避免重复插入
SET @notes_count = ( SELECT COUNT(*) FROM notes );

-- 只有在没有数据时才插入读书笔记测试数据
INSERT INTO
    notes (title, content, product_id, user_id, created_at, updated_at)
SELECT *
FROM (
        SELECT '《活着》读后感：生命的坚韧与意义', '余华的《活着》讲述了福贵一生的苦难，从地主到农民，经历了家庭的悲剧和时代的变迁。小说通过福贵的视角反映了中国近现代历史的苦难历程。作品中最打动我的是福贵面对苦难时的生命力，即使经历了那么多不幸，他依然选择活着。这种坚韧让我思考生命的意义不在于拥有什么，而在于如何面对苦难。', 1, 3, NOW() - INTERVAL 30 DAY, NOW() - INTERVAL 30 DAY
        UNION ALL
        SELECT '《平凡的世界》：不平凡的生命力量', '路遥的《平凡的世界》通过孙少平和孙少安兄弟的奋斗故事，展现了中国农村改革开放初期的巨大变化。作品中最吸引我的是人物面对困境时的坚韧与奋斗精神。少平从农村到城市，从煤矿工人到自学成才；少安在农村创业，面对各种困难却始终不放弃。这部作品教会我们：平凡人的生活可能充满艰辛，但通过自己的努力和坚持，可以创造不平凡的价值。', 4, 4, NOW() - INTERVAL 28 DAY, NOW() - INTERVAL 28 DAY
        UNION ALL
        SELECT '《百年孤独》：魔幻与现实的交融', '马尔克斯的《百年孤独》讲述了布恩迪亚家族七代人的故事，以魔幻现实主义手法展现了拉丁美洲的历史与文化。小说中时间的循环、人物命运的重复、孤独感的延续等元素构成了一个既奇幻又真实的世界。通过阅读这部作品，我感受到了人类历史的轮回与宿命，以及在这种宿命中人们对爱与希望的永恒追求。每个角色都在寻找自己的身份和归属，却又最终陷入孤独的命运，这种矛盾反映了人类生存的本质状态。', 10, 5, NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 20 DAY
        UNION ALL
        SELECT '《围城》：人生的困境与选择', '钱钟书的《围城》以幽默辛辣的笔调描绘了知识分子方鸿渐的生活与爱情故事。"城里的人想出去，城外的人想进来"这句名言道出了人生的普遍困境。通过阅读这部作品，我深刻理解了人们对自身处境的不满与对未知生活的向往之间的矛盾。小说中的婚姻、事业和人际关系都是一座座"围城"，提醒我们要认清自己的内心，做出真正适合自己的选择。', 11, 3, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 18 DAY
        UNION ALL
        SELECT '《三体》：科幻视野下的人性思考', '刘慈欣的《三体》通过描述人类与三体文明的接触与冲突，展现了宏大的宇宙图景和深刻的哲学思考。作品中"黑暗森林法则"的提出让我思考文明间交流的本质与局限。面对危机，人类内部的分化与统一也反映了人性的复杂性。这部作品启发我们思考技术发展与人类未来、个体价值与集体生存等重大议题，同时也警示我们理性看待科技进步带来的挑战。', 2, 4, NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 15 DAY
    ) AS new_notes (
        title, content, product_id, user_id, created_at, updated_at
    )
WHERE
    @notes_count = 0;

-- 检查笔记反馈表是否已有数据
SET @feedback_count = ( SELECT COUNT(*) FROM note_feedbacks );

-- 添加读书笔记反馈测试数据
INSERT INTO
    note_feedbacks (note_id, user_id, feedback_type, created_at, updated_at)
SELECT *
FROM (
        SELECT 1, 4, 'LIKE', NOW() - INTERVAL 29 DAY, NOW() - INTERVAL 29 DAY
        UNION ALL
        SELECT 1, 5, 'LIKE', NOW() - INTERVAL 28 DAY, NOW() - INTERVAL 28 DAY
        UNION ALL
        SELECT 1, 6, 'DISLIKE', NOW() - INTERVAL 27 DAY, NOW() - INTERVAL 27 DAY
        UNION ALL
        SELECT 2, 3, 'LIKE', NOW() - INTERVAL 27 DAY, NOW() - INTERVAL 27 DAY
        UNION ALL
        SELECT 2, 5, 'LIKE', NOW() - INTERVAL 26 DAY, NOW() - INTERVAL 26 DAY
        UNION ALL
        SELECT 2, 6, 'LIKE', NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 25 DAY
        UNION ALL
        SELECT 3, 3, 'LIKE', NOW() - INTERVAL 24 DAY, NOW() - INTERVAL 24 DAY
        UNION ALL
        SELECT 3, 4, 'DISLIKE', NOW() - INTERVAL 23 DAY, NOW() - INTERVAL 23 DAY
        UNION ALL
        SELECT 3, 6, 'DISLIKE', NOW() - INTERVAL 22 DAY, NOW() - INTERVAL 22 DAY
        UNION ALL
        SELECT 4, 4, 'LIKE', NOW() - INTERVAL 19 DAY, NOW() - INTERVAL 19 DAY
        UNION ALL
        SELECT 4, 5, 'LIKE', NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 18 DAY
        UNION ALL
        SELECT 5, 3, 'LIKE', NOW() - INTERVAL 14 DAY, NOW() - INTERVAL 14 DAY
        UNION ALL
        SELECT 5, 5, 'LIKE', NOW() - INTERVAL 13 DAY, NOW() - INTERVAL 13 DAY
        UNION ALL
        SELECT 5, 6, 'LIKE', NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 12 DAY
    ) AS new_feedbacks (
        note_id, user_id, feedback_type, created_at, updated_at
    )
WHERE
    @feedback_count = 0;

-- 检查笔记评论表是否已有数据
SET @comment_count = ( SELECT COUNT(*) FROM note_comments );

-- 添加读书笔记评论测试数据
INSERT INTO
    note_comments (note_id, user_id, content, created_at, updated_at)
SELECT *
FROM (
        SELECT 1, 4, '你对福贵的理解很深刻，我也被他的坚韧所感动。', NOW() - INTERVAL 29 DAY, NOW() - INTERVAL 29 DAY
        UNION ALL
        SELECT 1, 5, '同感，这本书让我重新思考了生命的意义和价值。', NOW() - INTERVAL 28 DAY, NOW() - INTERVAL 28 DAY
        UNION ALL
        SELECT 1, 6, '我觉得作者对苦难的描写有些过于沉重了，不过你的理解角度很独特。', NOW() - INTERVAL 27 DAY, NOW() - INTERVAL 27 DAY
        UNION ALL
        SELECT 2, 3, '《平凡的世界》确实是一部非常励志的作品，特别是对于我们这一代人。', NOW() - INTERVAL 27 DAY, NOW() - INTERVAL 27 DAY
        UNION ALL
        SELECT 2, 5, '少平的自学精神一直激励着我，你的笔记总结得很到位。', NOW() - INTERVAL 26 DAY, NOW() - INTERVAL 26 DAY
        UNION ALL
        SELECT 3, 3, '马尔克斯的魔幻现实主义手法确实独特，你对孤独主题的解读很有深度。', NOW() - INTERVAL 24 DAY, NOW() - INTERVAL 24 DAY
        UNION ALL
        SELECT 3, 4, '我读这本书时感到有些混乱，可能是因为人物太多了，不过你的笔记帮助我理清了一些思路。', NOW() - INTERVAL 23 DAY, NOW() - INTERVAL 23 DAY
        UNION ALL
        SELECT 4, 5, '钱钟书的文笔真是太精彩了，你对"围城"概念的理解也很到位。', NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 18 DAY
        UNION ALL
        SELECT 5, 3, '《三体》确实让人思考很多关于人类文明和未来的问题，你的见解很有启发性。', NOW() - INTERVAL 14 DAY, NOW() - INTERVAL 14 DAY
        UNION ALL
        SELECT 5, 6, '黑暗森林法则真的很有震撼力，我一直在思考这个理论在现实中的应用可能性。', NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 12 DAY
    ) AS new_comments (
        note_id, user_id, content, created_at, updated_at
    )
WHERE
    @comment_count = 0;
