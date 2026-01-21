-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `role` varchar(20) NOT NULL COMMENT '角色',
    `school_id` bigint DEFAULT NULL COMMENT '学校ID',
    `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_school_id` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 校园卡表
CREATE TABLE IF NOT EXISTS `ecard` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `card_no` varchar(20) NOT NULL COMMENT '卡号',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
    `status` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '状态',
    `is_visitor_card` tinyint DEFAULT '0' COMMENT '是否游客卡',
    `visitor_expire_time` datetime DEFAULT NULL COMMENT '游客卡过期时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_card_no` (`card_no`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_ecard_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园卡表';

-- 二手商品表
CREATE TABLE IF NOT EXISTS `secondhand_goods` (
                                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                  `title` varchar(100) NOT NULL COMMENT '商品标题',
    `description` text COMMENT '商品描述',
    `price` decimal(10,2) NOT NULL COMMENT '价格',
    `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
    `category` varchar(20) NOT NULL COMMENT '分类',
    `status` varchar(20) NOT NULL DEFAULT 'ON_SALE' COMMENT '状态',
    `image_urls` json DEFAULT NULL COMMENT '图片URL列表',
    `publisher_id` bigint NOT NULL COMMENT '发布者ID',
    `school_id` bigint NOT NULL COMMENT '学校ID',
    `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `contact_wechat` varchar(50) DEFAULT NULL COMMENT '微信',
    `location` varchar(200) DEFAULT NULL COMMENT '交易地点',
    `favorite_count` int DEFAULT '0' COMMENT '收藏数',
    `view_count` int DEFAULT '0' COMMENT '浏览数',
    `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_publisher_id` (`publisher_id`),
    KEY `idx_school_id` (`school_id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_goods_user` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='二手商品表';

-- 插入测试数据（开发环境：允许覆盖关键字段）
-- 注意：密码使用 BCrypt 加密，原始密码统一为：123456
-- 使用 ON DUPLICATE KEY UPDATE 确保“已存在的测试账号”也会被重置为可登录状态
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `school_id`, `avatar_url`, `phone`, `email`) VALUES
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO', '张三', 'STUDENT', 1, 'https://example.com/avatar1.jpg', '13800138000', 'zhangsan@example.com'),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO', '李老师', 'TEACHER', 1, 'https://example.com/avatar2.jpg', '13800138001', 'lisi@example.com'),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO', '王五', 'STUDENT', 1, 'https://example.com/avatar3.jpg', '13800138002', 'wangwu@example.com'),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO', '管理员', 'ADMIN', 1, 'https://example.com/avatar4.jpg', '13800138003', 'admin@example.com')
ON DUPLICATE KEY UPDATE
  `password`=VALUES(`password`),
  `nickname`=VALUES(`nickname`),
  `role`=VALUES(`role`),
  `school_id`=VALUES(`school_id`),
  `avatar_url`=VALUES(`avatar_url`),
  `phone`=VALUES(`phone`),
  `email`=VALUES(`email`),
  `is_deleted`=0;
-- 密码统一为：123456（BCrypt 加密）

INSERT IGNORE INTO `ecard` (`card_no`, `user_id`, `balance`, `status`, `is_visitor_card`) VALUES
('20230001', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 356.78, 'NORMAL', 0),
('20230002', (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 1000.00, 'NORMAL', 0),
('20230003', (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 50.00, 'NORMAL', 0);

INSERT IGNORE INTO `secondhand_goods` (`title`, `description`, `price`, `original_price`, `category`, `status`, `publisher_id`, `school_id`, `contact_phone`, `location`, `favorite_count`, `view_count`) VALUES
('华为MateBook 14 2022款', '2022年购入，95新，配置：i5-1240P/16G/512G，有原装充电器和电脑包', 4500.00, 5999.00, 'ELECTRONICS', 'ON_SALE', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 1, '13800138000', '图书馆一楼', 15, 120),
('Java核心技术 卷I', '第11版，9成新，有少量笔记', 50.00, 89.00, 'BOOKS', 'ON_SALE', (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 1, '13800138002', '教学楼B201', 8, 45),
('耐克运动鞋 42码', '黑色经典款，穿过3次，几乎全新', 280.00, 599.00, 'CLOTHING', 'ON_SALE', (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 1, '13800138002', '宿舍楼3号楼', 23, 89),
('MacBook Pro 13寸 M1', '2021年购入，9成新，配置：M1芯片/8G/256G，原装充电器', 6800.00, 9999.00, 'ELECTRONICS', 'ON_SALE', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 1, '13800138000', '图书馆一楼', 28, 156),
('高等数学 同济版', '第7版上下册，8成新，有笔记', 30.00, 68.00, 'BOOKS', 'ON_SALE', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 1, '13800138000', '教学楼A101', 12, 67),
('数据结构与算法分析', 'C语言描述，第3版，9成新', 45.00, 79.00, 'BOOKS', 'ON_SALE', (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 1, '13800138002', '图书馆二楼', 18, 89),
('iPhone 13 128GB', '2022年购入，95新，无拆修，原装充电器', 4200.00, 5999.00, 'ELECTRONICS', 'ON_SALE', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 1, '13800138000', '宿舍楼1号楼', 35, 234),
('Adidas运动鞋 43码', '白色经典款，穿过5次，8成新', 220.00, 499.00, 'CLOTHING', 'ON_SALE', (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 1, '13800138002', '体育馆', 15, 78),
('线性代数教材', '第5版，7成新，有笔记和标注', 25.00, 45.00, 'BOOKS', 'ON_SALE', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 1, '13800138000', '教学楼C201', 9, 45),
('小米手环7', '2023年购入，99新，全套包装', 180.00, 249.00, 'ELECTRONICS', 'ON_SALE', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 1, '13800138000', '图书馆一楼', 22, 123);

-- 注意：下面这些测试数据依赖 parttime / team / schedule 等表
-- 为避免“表未创建就插入”导致整段 init.sql 执行中断，已移动到对应表结构定义之后插入

-- 消费记录表
CREATE TABLE IF NOT EXISTS `consume_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `card_no` varchar(20) NOT NULL COMMENT '卡号',
    `amount` decimal(10,2) NOT NULL COMMENT '消费金额',
    `balance_after` decimal(10,2) NOT NULL COMMENT '消费后余额',
    `merchant_id` varchar(50) DEFAULT NULL COMMENT '商户ID',
    `merchant_name` varchar(100) DEFAULT NULL COMMENT '商户名称',
    `consume_type` varchar(20) NOT NULL COMMENT '消费类型',
    `description` varchar(500) DEFAULT NULL COMMENT '消费描述',
    `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
    `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
    `is_in_campus` tinyint DEFAULT '1' COMMENT '是否在校内',
    `pay_method` varchar(20) DEFAULT 'CARD' COMMENT '支付方式',
    `consume_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '消费时间',
    PRIMARY KEY (`id`),
    KEY `idx_card_no` (`card_no`),
    KEY `idx_consume_time` (`consume_time`),
    KEY `idx_consume_type` (`consume_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费记录表';

-- 兼职表
CREATE TABLE IF NOT EXISTS `parttime` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` varchar(100) NOT NULL COMMENT '兼职标题',
    `description` text COMMENT '兼职描述',
    `salary_per_hour` decimal(10,2) NOT NULL COMMENT '时薪',
    `recruit_count` int NOT NULL DEFAULT '1' COMMENT '招聘人数',
    `applied_count` int DEFAULT '0' COMMENT '已报名人数',
    `work_start_time` datetime NOT NULL COMMENT '工作开始时间',
    `work_end_time` datetime NOT NULL COMMENT '工作结束时间',
    `location` varchar(200) DEFAULT NULL COMMENT '工作地点',
    `requirements` text COMMENT '要求说明',
    `status` varchar(20) NOT NULL DEFAULT 'RECRUITING' COMMENT '状态',
    `publisher_id` bigint NOT NULL COMMENT '发布者ID',
    `school_id` bigint NOT NULL COMMENT '学校ID',
    `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `image_urls` json DEFAULT NULL COMMENT '图片URL列表',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_publisher_id` (`publisher_id`),
    KEY `idx_school_id` (`school_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_parttime_user` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兼职表';

-- 插入兼职测试数据（放在 parttime 表创建之后）
INSERT IGNORE INTO `parttime` (`title`, `description`, `salary_per_hour`, `recruit_count`, `applied_count`, `work_start_time`, `work_end_time`, `location`, `requirements`, `status`, `publisher_id`, `school_id`, `contact_name`, `contact_phone`) VALUES
('图书馆管理员助理', '协助图书馆日常管理工作，整理图书、维护秩序等', 25.00, 3, 1, '2024-02-01 08:00:00', '2024-06-30 18:00:00', '图书馆', '责任心强，有耐心，熟悉图书分类', 'RECRUITING', (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 1, '李老师', '13800138001'),
('校园外卖配送员', '负责校园内外卖配送工作，时间灵活', 20.00, 10, 3, '2024-01-20 10:00:00', '2024-12-31 22:00:00', '校园内', '身体健康，有电动车，熟悉校园路线', 'RECRUITING', (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 1, '王经理', '13800138002'),
('家教辅导（数学）', '为高中生提供数学辅导，周末上课', 80.00, 2, 0, '2024-02-01 09:00:00', '2024-07-31 18:00:00', '线上/线下', '数学专业优先，有教学经验', 'RECRUITING', (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 1, '张老师', '13800138003'),
('咖啡店服务员', '校园咖啡店服务员，负责点单、制作、清洁', 22.00, 5, 2, '2024-01-25 08:00:00', '2024-12-31 22:00:00', '校园咖啡店', '形象良好，服务意识强，能接受轮班', 'RECRUITING', (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 1, '刘店长', '13800138004'),
('数据录入员', '协助录入学生信息、成绩等数据', 18.00, 4, 1, '2024-02-01 09:00:00', '2024-05-31 17:00:00', '行政楼', '熟悉Excel，打字速度快，细心', 'RECRUITING', (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 1, '赵老师', '13800138005'),
('活动策划助理', '协助组织校园活动，负责宣传、协调等工作', 30.00, 3, 0, '2024-02-01 09:00:00', '2024-12-31 18:00:00', '学生活动中心', '有活动组织经验，沟通能力强', 'RECRUITING', (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 1, '孙老师', '13800138006');

-- 兼职报名表
CREATE TABLE IF NOT EXISTS `parttime_apply` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parttime_id` bigint NOT NULL COMMENT '兼职ID',
    `applicant_id` bigint NOT NULL COMMENT '申请人ID',
    `application_note` text COMMENT '申请备注',
    `available_time` varchar(200) DEFAULT NULL COMMENT '可工作时间',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
    `processor_id` bigint DEFAULT NULL COMMENT '处理人ID',
    `process_note` text COMMENT '处理备注',
    `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `process_time` datetime DEFAULT NULL COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY `idx_parttime_id` (`parttime_id`),
    KEY `idx_applicant_id` (`applicant_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_apply_parttime` FOREIGN KEY (`parttime_id`) REFERENCES `parttime` (`id`),
    CONSTRAINT `fk_apply_user` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兼职报名表';

-- 个人行程表
CREATE TABLE IF NOT EXISTS `personal_schedule` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` varchar(100) NOT NULL COMMENT '行程标题',
    `description` text COMMENT '行程描述',
    `start_time` datetime NOT NULL COMMENT '开始时间',
    `end_time` datetime NOT NULL COMMENT '结束时间',
    `type` varchar(20) NOT NULL COMMENT '行程类型',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `location` varchar(200) DEFAULT NULL COMMENT '地点',
    `is_all_day` tinyint DEFAULT '0' COMMENT '是否全天',
    `remind_type` varchar(20) DEFAULT NULL COMMENT '提醒类型',
    `custom_remind_minutes` int DEFAULT NULL COMMENT '自定义提醒分钟数',
    `is_repeat` tinyint DEFAULT '0' COMMENT '是否重复',
    `repeat_rule` varchar(100) DEFAULT NULL COMMENT '重复规则',
    `tag` varchar(50) DEFAULT NULL COMMENT '标签',
    `is_route_series` tinyint DEFAULT '0' COMMENT '是否为行程系列',
    `route_series_theme` varchar(100) DEFAULT NULL COMMENT '行程系列主题',
    `creator_id` bigint NOT NULL COMMENT '创建者ID',
    `synced_team_schedule_id` bigint DEFAULT NULL COMMENT '同步的团队行程ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_type` (`type`),
    CONSTRAINT `fk_schedule_user` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人行程表';

-- 团队表
CREATE TABLE IF NOT EXISTS `team` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(100) NOT NULL COMMENT '团队名称',
    `description` text COMMENT '团队描述',
    `avatar` varchar(500) DEFAULT NULL COMMENT '团队头像',
    `creator_id` bigint NOT NULL COMMENT '创建者ID',
    `school_id` bigint NOT NULL COMMENT '学校ID',
    `invite_code` varchar(20) DEFAULT NULL COMMENT '团队邀请码',
    `need_approve` tinyint DEFAULT '1' COMMENT '是否需要审批加入',
    `max_members` int DEFAULT '50' COMMENT '最大成员数',
    `member_count` int DEFAULT '1' COMMENT '当前成员数',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_invite_code` (`invite_code`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_school_id` (`school_id`),
    CONSTRAINT `fk_team_user` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队表';

-- 团队成员表
CREATE TABLE IF NOT EXISTS `team_member` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `team_id` bigint NOT NULL COMMENT '团队ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role` varchar(20) NOT NULL DEFAULT 'MEMBER' COMMENT '角色',
    `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_team_user` (`team_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_member_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`),
    CONSTRAINT `fk_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队成员表';

-- 团队邀请表
CREATE TABLE IF NOT EXISTS `team_invite` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `team_id` bigint NOT NULL COMMENT '团队ID',
    `inviter_id` bigint NOT NULL COMMENT '邀请者ID',
    `invitee_id` bigint NOT NULL COMMENT '被邀请者ID',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/ACCEPTED/REJECTED）',
    `invite_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '邀请时间',
    `process_time` datetime DEFAULT NULL COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY `idx_team_id` (`team_id`),
    KEY `idx_invitee_id` (`invitee_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_invite_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`),
    CONSTRAINT `fk_invite_inviter` FOREIGN KEY (`inviter_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_invite_invitee` FOREIGN KEY (`invitee_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队邀请表';

-- 团队行程表
CREATE TABLE IF NOT EXISTS `team_schedule` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `team_id` bigint NOT NULL COMMENT '团队ID',
    `title` varchar(100) NOT NULL COMMENT '行程标题',
    `description` text COMMENT '行程描述',
    `start_time` datetime NOT NULL COMMENT '开始时间',
    `end_time` datetime NOT NULL COMMENT '结束时间',
    `location` varchar(200) DEFAULT NULL COMMENT '地点',
    `need_confirm` tinyint DEFAULT '0' COMMENT '是否需要确认',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `creator_id` bigint NOT NULL COMMENT '创建者ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_team_id` (`team_id`),
    KEY `idx_start_time` (`start_time`),
    CONSTRAINT `fk_team_schedule_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`),
    CONSTRAINT `fk_team_schedule_user` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队行程表';

-- 团队行程参与者表
CREATE TABLE IF NOT EXISTS `team_schedule_attendee` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `schedule_id` bigint NOT NULL COMMENT '行程ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '参与状态',
    `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_schedule_user` (`schedule_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_attendee_schedule` FOREIGN KEY (`schedule_id`) REFERENCES `team_schedule` (`id`),
    CONSTRAINT `fk_attendee_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队行程参与者表';

-- ==================== 团队 / 行程：测试数据（放在表结构之后） ====================
-- 插入团队测试数据（通过用户名查询 creatorId，避免依赖固定自增ID）
INSERT IGNORE INTO `team` (`name`, `description`, `creator_id`, `school_id`, `invite_code`, `need_approve`, `max_members`, `member_count`) VALUES
('学习小组A', '高等数学学习小组，共同讨论学习问题',
 (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1),
 (SELECT school_id FROM `user` WHERE username='zhangsan' LIMIT 1),
 'TEAM001', 1, 20, 3),
('项目开发组', '软件开发项目组，协作完成项目',
 (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1),
 (SELECT school_id FROM `user` WHERE username='zhangsan' LIMIT 1),
 'TEAM002', 1, 15, 5),
('英语角', '英语口语练习小组，每周定期活动',
 (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1),
 (SELECT school_id FROM `user` WHERE username='wangwu' LIMIT 1),
 'TEAM003', 0, 30, 8);

-- 插入团队成员数据（通过 invite_code 定位 team_id）
INSERT IGNORE INTO `team_member` (`team_id`, `user_id`, `role`) VALUES
((SELECT id FROM `team` WHERE invite_code='TEAM001' LIMIT 1), (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 'CREATOR'),
((SELECT id FROM `team` WHERE invite_code='TEAM001' LIMIT 1), (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 'MEMBER'),
((SELECT id FROM `team` WHERE invite_code='TEAM001' LIMIT 1), (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 'MEMBER'),

((SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1), (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 'CREATOR'),
((SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1), (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 'ADMIN'),
((SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1), (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 'MEMBER'),
((SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1), (SELECT id FROM `user` WHERE username='admin' LIMIT 1), 'MEMBER'),

((SELECT id FROM `team` WHERE invite_code='TEAM003' LIMIT 1), (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 'CREATOR'),
((SELECT id FROM `team` WHERE invite_code='TEAM003' LIMIT 1), (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 'MEMBER'),
((SELECT id FROM `team` WHERE invite_code='TEAM003' LIMIT 1), (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 'MEMBER');

-- 插入个人行程测试数据
INSERT IGNORE INTO `personal_schedule` (`title`, `description`, `start_time`, `end_time`, `type`, `status`, `location`, `creator_id`) VALUES
('高等数学', '第二章：多元函数微分学', '2024-01-20 08:00:00', '2024-01-20 10:00:00', 'CLASS', 'PENDING', '3号教学楼201室', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1)),
('大学英语', 'Unit 3: Cultural Differences', '2024-01-20 10:00:00', '2024-01-20 12:00:00', 'CLASS', 'PENDING', '外语学院101室', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1)),
('数据结构与算法', '树与二叉树的应用', '2024-01-20 14:00:00', '2024-01-20 16:00:00', 'CLASS', 'PENDING', '计算机学院305室', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1)),
('编程俱乐部技术分享', '每周技术分享会', '2024-01-20 16:30:00', '2024-01-20 18:00:00', 'CLUB', 'PENDING', '学生活动中心B201', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1)),
('图书馆自习', '准备期末考试', '2024-01-20 19:00:00', '2024-01-20 21:00:00', 'PERSONAL', 'PENDING', '图书馆3楼自习区', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1));

-- 插入团队行程测试数据
INSERT IGNORE INTO `team_schedule` (`team_id`, `title`, `description`, `start_time`, `end_time`, `location`, `status`, `creator_id`) VALUES
((SELECT id FROM `team` WHERE invite_code='TEAM001' LIMIT 1), '学习讨论会', '讨论高等数学第三章内容', '2024-01-25 14:00:00', '2024-01-25 16:00:00', '图书馆讨论室', 'PENDING', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1)),
((SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1), '项目进度会议', '讨论项目进度和下一步计划', '2024-01-22 19:00:00', '2024-01-22 21:00:00', '线上', 'PENDING', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1)),
((SELECT id FROM `team` WHERE invite_code='TEAM003' LIMIT 1), '英语角活动', '每周英语口语练习', '2024-01-21 18:00:00', '2024-01-21 20:00:00', '学生活动中心', 'PENDING', (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1));

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `target_type` varchar(20) NOT NULL COMMENT '目标类型（SECONDHAND/PARTTIME）',
    `target_id` bigint NOT NULL COMMENT '目标ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    KEY `idx_target` (`target_type`, `target_id`),
    CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 高校表
CREATE TABLE IF NOT EXISTS `university` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(100) NOT NULL COMMENT '高校名称',
    `code` varchar(50) NOT NULL COMMENT '高校代码',
    `logo_url` varchar(500) DEFAULT NULL COMMENT 'Logo URL',
    `description` text COMMENT '高校描述',
    `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `contact_email` varchar(100) DEFAULT NULL COMMENT '联系邮箱',
    `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度（校园中心）',
    `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度（校园中心）',
    `campus_radius` int DEFAULT '1000' COMMENT '校园半径（米）',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高校表';

-- 兼容旧库：如果 university 表已存在但缺少 is_deleted 字段，补齐它（允许失败，避免重复执行中断）
ALTER TABLE `university` ADD COLUMN `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除';

-- 插入高校基础数据（用于 school_id=1 的场景；允许覆盖关键字段以保证展示正常）
INSERT INTO `university` (`id`, `name`, `code`, `status`, `is_deleted`) VALUES
(1, '示例大学', 'UNI001', 'APPROVED', 0)
ON DUPLICATE KEY UPDATE
  `name`=VALUES(`name`),
  `code`=VALUES(`code`),
  `status`=VALUES(`status`),
  `is_deleted`=VALUES(`is_deleted`);

-- 公告表
CREATE TABLE IF NOT EXISTS `announcement` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` varchar(100) NOT NULL COMMENT '公告标题',
    `content` text NOT NULL COMMENT '公告内容',
    `type` varchar(20) NOT NULL COMMENT '公告类型',
    `is_top` tinyint DEFAULT '0' COMMENT '是否置顶',
    `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `view_count` int DEFAULT '0' COMMENT '浏览次数',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 帮助文档表
CREATE TABLE IF NOT EXISTS `help_article` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` varchar(100) NOT NULL COMMENT '文档标题',
    `content` text NOT NULL COMMENT '文档内容',
    `category` varchar(50) NOT NULL COMMENT '分类',
    `view_count` int DEFAULT '0' COMMENT '浏览次数',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帮助文档表';

-- 反馈表
CREATE TABLE IF NOT EXISTS `feedback` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID（匿名反馈可为空）',
    `feedback_type` varchar(20) NOT NULL COMMENT '反馈类型',
    `title` varchar(100) NOT NULL COMMENT '反馈标题',
    `content` text NOT NULL COMMENT '反馈内容',
    `contact` varchar(100) DEFAULT NULL COMMENT '联系方式',
    `screenshots` json DEFAULT NULL COMMENT '截图URL列表',
    `status` varchar(20) NOT NULL DEFAULT 'SUBMITTED' COMMENT '处理状态',
    `processor_id` bigint DEFAULT NULL COMMENT '处理人ID',
    `process_note` text COMMENT '处理备注',
    `submit_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `process_time` datetime DEFAULT NULL COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_feedback_type` (`feedback_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈表';

-- 商户表
CREATE TABLE IF NOT EXISTS `merchant` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `merchant_name` varchar(100) NOT NULL COMMENT '商户名称',
    `merchant_type` varchar(20) NOT NULL COMMENT '商户类型（COMPANY/INDIVIDUAL）',
    `business_scope` varchar(500) DEFAULT NULL COMMENT '经营范围',
    `credit_code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
    `contact_name` varchar(50) NOT NULL COMMENT '联系人姓名',
    `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
    `contact_email` varchar(100) NOT NULL COMMENT '联系人邮箱',
    `business_license_url` varchar(500) DEFAULT NULL COMMENT '营业执照URL',
    `legal_id_card_url` varchar(500) DEFAULT NULL COMMENT '法人身份证URL',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/APPROVED/REJECTED/SUSPENDED）',
    `deposit_amount` decimal(10,2) DEFAULT '0.00' COMMENT '保证金金额',
    `credit_score` int DEFAULT '100' COMMENT '信用分（0-100）',
    `approved_time` datetime DEFAULT NULL COMMENT '审核通过时间',
    `approver_id` bigint DEFAULT NULL COMMENT '审核人ID',
    `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝原因',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_merchant_type` (`merchant_type`),
    CONSTRAINT `fk_merchant_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户表';

-- 保证金记录表
CREATE TABLE IF NOT EXISTS `deposit_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_id` bigint NOT NULL COMMENT '商户ID',
    `order_id` varchar(50) NOT NULL COMMENT '订单号',
    `amount` decimal(10,2) NOT NULL COMMENT '保证金金额',
    `pay_method` varchar(20) NOT NULL COMMENT '支付方式（ALIPAY/WECHAT/BANK）',
    `pay_status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '支付状态（PENDING/PAID/FAILED/REFUNDED）',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
    `refund_reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
    `transaction_id` varchar(100) DEFAULT NULL COMMENT '第三方交易号',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_pay_status` (`pay_status`),
    CONSTRAINT `fk_deposit_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保证金记录表';

-- 高校功能配置表
CREATE TABLE IF NOT EXISTS `university_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `university_id` bigint NOT NULL COMMENT '高校ID',
    `config_key` varchar(50) NOT NULL COMMENT '配置键',
    `config_value` text COMMENT '配置值（JSON格式）',
    `description` varchar(200) DEFAULT NULL COMMENT '配置说明',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_university_key` (`university_id`, `config_key`),
    KEY `idx_university_id` (`university_id`),
    CONSTRAINT `fk_config_university` FOREIGN KEY (`university_id`) REFERENCES `university` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高校功能配置表';

-- 通知表
CREATE TABLE IF NOT EXISTS `notification` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `university_id` bigint DEFAULT NULL COMMENT '高校ID（NULL表示平台通知）',
    `title` varchar(100) NOT NULL COMMENT '通知标题',
    `content` text NOT NULL COMMENT '通知内容',
    `target_type` varchar(20) NOT NULL DEFAULT 'ALL' COMMENT '目标类型（ALL/STUDENT/TEACHER/VISITOR）',
    `target_user_id` bigint DEFAULT NULL COMMENT '目标用户ID（NULL表示群发）',
    `is_urgent` tinyint DEFAULT '0' COMMENT '是否紧急',
    `is_read` tinyint DEFAULT '0' COMMENT '是否已读',
    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
    `sender_id` bigint DEFAULT NULL COMMENT '发送人ID',
    `send_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_university_id` (`university_id`),
    KEY `idx_target_user_id` (`target_user_id`),
    KEY `idx_send_time` (`send_time`),
    KEY `idx_is_read` (`is_read`),
    CONSTRAINT `fk_notification_university` FOREIGN KEY (`university_id`) REFERENCES `university` (`id`),
    CONSTRAINT `fk_notification_user` FOREIGN KEY (`target_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 用户登录日志表
CREATE TABLE IF NOT EXISTS `user_login_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `login_ip` varchar(50) DEFAULT NULL COMMENT '登录IP',
    `login_device` varchar(100) DEFAULT NULL COMMENT '登录设备',
    `login_location` varchar(200) DEFAULT NULL COMMENT '登录地点',
    `login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_login_time` (`login_time`),
    CONSTRAINT `fk_login_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录日志表';

-- 学生信息表（用于学生身份认证，替代教务系统接口）
CREATE TABLE IF NOT EXISTS `student_info` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `school_id` bigint NOT NULL COMMENT '学校ID',
    `student_id` varchar(50) NOT NULL COMMENT '学号',
    `name` varchar(50) NOT NULL COMMENT '姓名',
    `id_card` varchar(18) NOT NULL COMMENT '身份证号',
    `major` varchar(100) DEFAULT NULL COMMENT '专业',
    `grade` varchar(20) DEFAULT NULL COMMENT '年级',
    `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
    `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE/GRADUATED/SUSPENDED）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_school_student` (`school_id`, `student_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_id_card` (`id_card`),
    KEY `idx_school_id` (`school_id`),
    CONSTRAINT `fk_student_university` FOREIGN KEY (`school_id`) REFERENCES `university` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 教师信息表（用于教师身份认证，替代教师认证系统接口）
CREATE TABLE IF NOT EXISTS `teacher_info` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `school_id` bigint NOT NULL COMMENT '学校ID',
    `teacher_id` varchar(50) NOT NULL COMMENT '工号',
    `name` varchar(50) NOT NULL COMMENT '姓名',
    `id_card` varchar(18) NOT NULL COMMENT '身份证号',
    `department` varchar(100) DEFAULT NULL COMMENT '部门',
    `title` varchar(50) DEFAULT NULL COMMENT '职称',
    `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE/RESIGNED/SUSPENDED）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_school_teacher` (`school_id`, `teacher_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_id_card` (`id_card`),
    KEY `idx_school_id` (`school_id`),
    CONSTRAINT `fk_teacher_university` FOREIGN KEY (`school_id`) REFERENCES `university` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- 插入高校测试数据
INSERT IGNORE INTO `university` (`name`, `code`, `longitude`, `latitude`, `campus_radius`, `status`) VALUES
('清华大学', 'TSINGHUA', 116.3269, 40.0036, 1500, 'APPROVED'),
('北京大学', 'PEKING', 116.3103, 39.9925, 1200, 'APPROVED');

-- 插入测试学生数据（清华大学）
INSERT IGNORE INTO `student_info` (`school_id`, `student_id`, `name`, `id_card`, `major`, `grade`, `class_name`, `status`) VALUES
-- 2023级学生
(1, '20230001', '张三', '110101199001011234', '计算机科学与技术', '2023', '计科2301', 'ACTIVE'),
(1, '20230002', '李四', '110101199002021234', '软件工程', '2023', '软工2301', 'ACTIVE'),
(1, '20230003', '王五', '110101199003031234', '计算机科学与技术', '2023', '计科2302', 'ACTIVE'),
(1, '20230004', '赵六', '110101199004041234', '软件工程', '2023', '软工2302', 'ACTIVE'),
(1, '20230005', '钱七', '110101199005051234', '人工智能', '2023', 'AI2301', 'ACTIVE'),
(1, '20230006', '孙八', '110101199006061234', '数据科学与大数据技术', '2023', '数据2301', 'ACTIVE'),
-- 2022级学生
(1, '20220001', '周九', '110101199103031234', '电子信息工程', '2022', '电信2201', 'ACTIVE'),
(1, '20220002', '吴十', '110101199104041234', '通信工程', '2022', '通信2201', 'ACTIVE'),
(1, '20220003', '郑十一', '110101199105051234', '自动化', '2022', '自动化2201', 'ACTIVE'),
(1, '20220004', '王十二', '110101199106061234', '计算机科学与技术', '2022', '计科2201', 'ACTIVE'),
-- 2021级学生
(1, '20210001', '李十三', '110101200001011234', '软件工程', '2021', '软工2101', 'ACTIVE'),
(1, '20210002', '张十四', '110101200002021234', '计算机科学与技术', '2021', '计科2101', 'ACTIVE'),
(1, '20210003', '刘十五', '110101200003031234', '网络工程', '2021', '网络2101', 'ACTIVE'),
-- 2020级学生（已毕业）
(1, '20200001', '陈十六', '110101199901011234', '计算机科学与技术', '2020', '计科2001', 'GRADUATED'),
(1, '20200002', '杨十七', '110101199902021234', '软件工程', '2020', '软工2001', 'GRADUATED'),
-- 北京大学学生
(2, '20230001', '林十八', '110101199007071234', '计算机科学与技术', '2023', '计科2301', 'ACTIVE'),
(2, '20230002', '黄十九', '110101199008081234', '软件工程', '2023', '软工2301', 'ACTIVE'),
(2, '20220001', '徐二十', '110101199107071234', '电子信息工程', '2022', '电信2201', 'ACTIVE');

-- 插入测试教师数据（清华大学）
INSERT IGNORE INTO `teacher_info` (`school_id`, `teacher_id`, `name`, `id_card`, `department`, `title`, `phone`, `email`, `status`) VALUES
-- 计算机学院
(1, 'T001', '张教授', '110101197001011234', '计算机学院', '教授', '13800138001', 'zhang@tsinghua.edu.cn', 'ACTIVE'),
(1, 'T002', '李副教授', '110101198002021234', '计算机学院', '副教授', '13800138002', 'li@tsinghua.edu.cn', 'ACTIVE'),
(1, 'T003', '王讲师', '110101199003031234', '计算机学院', '讲师', '13800138003', 'wang@tsinghua.edu.cn', 'ACTIVE'),
(1, 'T004', '赵教授', '110101197004041234', '计算机学院', '教授', '13800138004', 'zhao@tsinghua.edu.cn', 'ACTIVE'),
-- 软件学院
(1, 'T005', '钱副教授', '110101198005051234', '软件学院', '副教授', '13800138005', 'qian@tsinghua.edu.cn', 'ACTIVE'),
(1, 'T006', '孙讲师', '110101199006061234', '软件学院', '讲师', '13800138006', 'sun@tsinghua.edu.cn', 'ACTIVE'),
-- 电子工程系
(1, 'T007', '周教授', '110101197007071234', '电子工程系', '教授', '13800138007', 'zhou@tsinghua.edu.cn', 'ACTIVE'),
(1, 'T008', '吴副教授', '110101198008081234', '电子工程系', '副教授', '13800138008', 'wu@tsinghua.edu.cn', 'ACTIVE'),
-- 自动化系
(1, 'T009', '郑讲师', '110101199009091234', '自动化系', '讲师', '13800138009', 'zheng@tsinghua.edu.cn', 'ACTIVE'),
(1, 'T010', '王教授', '110101197010101234', '自动化系', '教授', '13800138010', 'wang2@tsinghua.edu.cn', 'ACTIVE'),
-- 已离职教师
(1, 'T011', '李老师', '110101198011111234', '计算机学院', '讲师', '13800138011', 'li2@tsinghua.edu.cn', 'RESIGNED'),
-- 北京大学教师
(2, 'T001', '林教授', '110101197012121234', '计算机学院', '教授', '13800138020', 'lin@pku.edu.cn', 'ACTIVE'),
(2, 'T002', '黄副教授', '110101198013131234', '软件学院', '副教授', '13800138021', 'huang@pku.edu.cn', 'ACTIVE');

-- 邮箱验证码表（用于邮箱登录）
CREATE TABLE IF NOT EXISTS `email_verification_code` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `email` varchar(100) NOT NULL COMMENT '邮箱地址',
    `code` varchar(10) NOT NULL COMMENT '验证码',
    `code_id` varchar(50) NOT NULL COMMENT '验证码ID（用于验证）',
    `type` varchar(20) NOT NULL DEFAULT 'LOGIN' COMMENT '验证码类型（LOGIN/RESET_PASSWORD）',
    `expire_time` datetime NOT NULL COMMENT '过期时间',
    `is_used` tinyint DEFAULT '0' COMMENT '是否已使用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_email` (`email`),
    KEY `idx_code_id` (`code_id`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱验证码表';

-- 浏览记录表（用于记录用户浏览历史）
CREATE TABLE IF NOT EXISTS `browse_history` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `content_type` varchar(20) NOT NULL COMMENT '内容类型（SECONDHAND/PARTTIME）',
    `content_id` bigint NOT NULL COMMENT '内容ID（商品ID或兼职ID）',
    `view_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_content` (`content_type`, `content_id`),
    KEY `idx_view_time` (`view_time`),
    CONSTRAINT `fk_browse_history_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览记录表';

-- 消息表（系统消息和用户消息）
CREATE TABLE IF NOT EXISTS `message` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '接收用户ID',
    `type` varchar(20) NOT NULL COMMENT '消息类型（SYSTEM/PARTTIME/SECONDHAND）',
    `title` varchar(200) NOT NULL COMMENT '消息标题',
    `content` text COMMENT '消息内容',
    `related_id` bigint DEFAULT NULL COMMENT '关联ID（如商品ID、兼职ID）',
    `is_read` tinyint DEFAULT '0' COMMENT '是否已读',
    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_message_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 聊天会话表
CREATE TABLE IF NOT EXISTS `chat_conversation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user1_id` bigint NOT NULL COMMENT '用户1 ID',
    `user2_id` bigint NOT NULL COMMENT '用户2 ID',
    `last_message_id` bigint DEFAULT NULL COMMENT '最后一条消息ID',
    `last_message_time` datetime DEFAULT NULL COMMENT '最后消息时间',
    `user1_unread_count` int DEFAULT '0' COMMENT '用户1未读消息数',
    `user2_unread_count` int DEFAULT '0' COMMENT '用户2未读消息数',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users` (`user1_id`, `user2_id`),
    KEY `idx_user1` (`user1_id`),
    KEY `idx_user2` (`user2_id`),
    KEY `idx_last_message_time` (`last_message_time`),
    CONSTRAINT `fk_conversation_user1` FOREIGN KEY (`user1_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_conversation_user2` FOREIGN KEY (`user2_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS `chat_message` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `conversation_id` bigint NOT NULL COMMENT '会话ID',
    `sender_id` bigint NOT NULL COMMENT '发送者ID',
    `receiver_id` bigint NOT NULL COMMENT '接收者ID',
    `content` text NOT NULL COMMENT '消息内容',
    `type` varchar(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型（TEXT/IMAGE/FILE）',
    `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL（如果是图片消息）',
    `file_url` varchar(500) DEFAULT NULL COMMENT '文件URL（如果是文件消息）',
    `is_read` tinyint DEFAULT '0' COMMENT '是否已读',
    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_receiver_id` (`receiver_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_message_conversation` FOREIGN KEY (`conversation_id`) REFERENCES `chat_conversation` (`id`),
    CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 群聊信息表（用于团队消息群/普通群）
CREATE TABLE IF NOT EXISTS `chat_group` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `team_id` bigint DEFAULT NULL COMMENT '关联团队ID（可为空，表示普通群）',
    `name` varchar(100) NOT NULL COMMENT '群名称',
    `owner_id` bigint NOT NULL COMMENT '群主用户ID',
    `max_admins` int NOT NULL DEFAULT '4' COMMENT '最大管理员数',
    `notice` varchar(500) DEFAULT NULL COMMENT '群公告',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_team_id` (`team_id`),
    KEY `idx_owner_id` (`owner_id`),
    CONSTRAINT `fk_group_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群聊信息表';

-- 群聊成员表
CREATE TABLE IF NOT EXISTS `chat_group_member` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id` bigint NOT NULL COMMENT '群ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role` varchar(20) NOT NULL DEFAULT 'MEMBER' COMMENT '角色（OWNER/ADMIN/MEMBER）',
    `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_user` (`group_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_group_member_group` FOREIGN KEY (`group_id`) REFERENCES `chat_group` (`id`),
    CONSTRAINT `fk_group_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群聊成员表';

-- 群聊消息表
CREATE TABLE IF NOT EXISTS `chat_group_message` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id` bigint NOT NULL COMMENT '群ID',
    `sender_id` bigint NOT NULL COMMENT '发送者ID',
    `content` text NOT NULL COMMENT '消息内容',
    `type` varchar(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型（TEXT/IMAGE/FILE）',
    `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL（如果是图片消息）',
    `file_url` varchar(500) DEFAULT NULL COMMENT '文件URL（如果是文件消息）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_group_id` (`group_id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_group_message_group` FOREIGN KEY (`group_id`) REFERENCES `chat_group` (`id`),
    CONSTRAINT `fk_group_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群聊消息表';

-- ==================== 团队群聊：测试数据（放在 chat_group 表结构之后） ====================
-- 为已有团队自动补齐群聊（createTeam 接口也会自动创建，这里用于“初始数据团队”可直接测试群聊）
INSERT IGNORE INTO `chat_group` (`team_id`, `name`, `owner_id`, `max_admins`) VALUES
((SELECT id FROM `team` WHERE invite_code='TEAM001' LIMIT 1), '学习小组A', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 4),
((SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1), '项目开发组', (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 4),
((SELECT id FROM `team` WHERE invite_code='TEAM003' LIMIT 1), '英语角', (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 4);

-- 群成员（按团队成员角色映射：CREATOR->OWNER，ADMIN->ADMIN，MEMBER->MEMBER）
INSERT IGNORE INTO `chat_group_member` (`group_id`, `user_id`, `role`) VALUES
((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM001' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 'OWNER'),
((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM001' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 'MEMBER'),
((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM001' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 'MEMBER'),

((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 'OWNER'),
((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 'ADMIN'),
((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 'MEMBER'),
((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM002' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='admin' LIMIT 1), 'MEMBER'),

((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM003' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='wangwu' LIMIT 1), 'OWNER'),
((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM003' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='zhangsan' LIMIT 1), 'MEMBER'),
((SELECT id FROM `chat_group` WHERE team_id=(SELECT id FROM `team` WHERE invite_code='TEAM003' LIMIT 1) LIMIT 1), (SELECT id FROM `user` WHERE username='lisi' LIMIT 1), 'MEMBER');

-- 用户身份认证申请表
CREATE TABLE IF NOT EXISTS `user_auth_apply` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
    `id_card` varchar(18) NOT NULL COMMENT '身份证号',
    `student_card_front` varchar(500) DEFAULT NULL COMMENT '学生证正面照片URL',
    `student_card_back` varchar(500) DEFAULT NULL COMMENT '学生证背面照片URL',
    `school_name` varchar(100) DEFAULT NULL COMMENT '学校名称',
    `major` varchar(100) DEFAULT NULL COMMENT '专业',
    `grade` varchar(20) DEFAULT NULL COMMENT '年级',
    `apply_role` varchar(20) NOT NULL COMMENT '申请角色（STUDENT/TEACHER）',
    `status` int NOT NULL DEFAULT '0' COMMENT '审核状态（0-待审核，1-已通过，2-已拒绝）',
    `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
    `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
    `auditor_id` bigint DEFAULT NULL COMMENT '审核人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_apply_role` (`apply_role`),
    CONSTRAINT `fk_auth_apply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_auth_apply_auditor` FOREIGN KEY (`auditor_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户身份认证申请表';

-- 账本表（用于记录用户消费账本）
CREATE TABLE IF NOT EXISTS `account_book` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `amount` decimal(10,2) NOT NULL COMMENT '消费金额',
    `category` varchar(50) NOT NULL COMMENT '消费分类：餐饮、交通、购物、娱乐、学习、其他',
    `description` varchar(500) DEFAULT NULL COMMENT '消费描述',
    `consume_date` date NOT NULL COMMENT '消费日期',
    `is_auto_import` tinyint DEFAULT '0' COMMENT '是否自动导入（来自E卡通）',
    `ecard_record_id` bigint DEFAULT NULL COMMENT '关联的E卡通消费记录ID（如果是自动导入）',
    `location` varchar(200) DEFAULT NULL COMMENT '消费地点',
    `pay_method` varchar(50) DEFAULT 'CARD' COMMENT '支付方式：CARD（校园卡）、CASH（现金）、ALIPAY（支付宝）、WECHAT（微信）、OTHER（其他）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_consume_date` (`consume_date`),
    KEY `idx_category` (`category`),
    KEY `idx_ecard_record_id` (`ecard_record_id`),
    CONSTRAINT `fk_account_book_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账本表';

-- 饮食记录表（用于记录用户饮食信息）
CREATE TABLE IF NOT EXISTS `diet_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `diet_date` date NOT NULL COMMENT '饮食日期',
    `meal_type` varchar(50) NOT NULL COMMENT '餐次：BREAKFAST（早餐）、LUNCH（午餐）、DINNER（晚餐）、SNACK（加餐）',
    `food_name` varchar(200) NOT NULL COMMENT '食物名称',
    `food_detail` varchar(500) DEFAULT NULL COMMENT '食物详情描述',
    `location` varchar(200) DEFAULT NULL COMMENT '用餐地点',
    `source` varchar(50) NOT NULL DEFAULT 'OTHER' COMMENT '来源：CANTEEN（食堂）、TAKEOUT（外卖）、RESTAURANT（餐厅）、HOME（家里）、OTHER（其他）',
    `is_auto_import` tinyint DEFAULT '0' COMMENT '是否自动导入（来自E卡通食堂消费）',
    `ecard_record_id` bigint DEFAULT NULL COMMENT '关联的E卡通消费记录ID（如果是自动导入）',
    `background_color` varchar(20) DEFAULT '#FFB6C1' COMMENT '背景颜色（用户自定义）',
    `calories` int DEFAULT NULL COMMENT '卡路里（可选）',
    `price` decimal(10,2) DEFAULT NULL COMMENT '价格（可选）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_diet_date` (`diet_date`),
    KEY `idx_meal_type` (`meal_type`),
    KEY `idx_ecard_record_id` (`ecard_record_id`),
    CONSTRAINT `fk_diet_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮食记录表';

-- 课程表（用于记录用户课程信息）
CREATE TABLE IF NOT EXISTS `course_table` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `course_name` varchar(100) NOT NULL COMMENT '课程名称',
    `day_of_week` varchar(10) NOT NULL COMMENT '星期（周一、周二...周日）',
    `start_time` time NOT NULL COMMENT '开始时间',
    `end_time` time NOT NULL COMMENT '结束时间',
    `location` varchar(200) DEFAULT NULL COMMENT '上课地点',
    `teacher` varchar(50) DEFAULT NULL COMMENT '授课教师',
    `week_range` varchar(50) DEFAULT NULL COMMENT '周次范围（如：1-16周）',
    `background_color` varchar(20) DEFAULT NULL COMMENT '背景颜色',
    `source` varchar(20) NOT NULL DEFAULT 'MANUAL' COMMENT '来源：AUTO（自动导入）、MANUAL（手动添加）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_day_of_week` (`day_of_week`),
    KEY `idx_source` (`source`),
    CONSTRAINT `fk_course_table_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 时间表（用于记录用户自定义的时间段设置）
CREATE TABLE IF NOT EXISTS `time_table` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `period_name` varchar(50) NOT NULL COMMENT '时间段名称（如：第一节、第二节）',
    `start_time` time NOT NULL COMMENT '开始时间',
    `end_time` time NOT NULL COMMENT '结束时间',
    `sort_order` int DEFAULT '0' COMMENT '排序顺序',
    `is_default` tinyint DEFAULT '0' COMMENT '是否默认时间表',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_default` (`is_default`),
    CONSTRAINT `fk_time_table_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间表';

-- 路线系列地点表（用于记录行程系列中的地点信息）
CREATE TABLE IF NOT EXISTS `route_series_location` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `schedule_id` bigint NOT NULL COMMENT '行程ID',
    `name` varchar(200) NOT NULL COMMENT '地点名称',
    `address` varchar(500) DEFAULT NULL COMMENT '地址',
    `longitude` decimal(10,7) NOT NULL COMMENT '经度',
    `latitude` decimal(10,7) NOT NULL COMMENT '纬度',
    `sort_order` int DEFAULT '0' COMMENT '排序顺序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_schedule_id` (`schedule_id`),
    KEY `idx_sort_order` (`schedule_id`, `sort_order`),
    CONSTRAINT `fk_route_location_schedule` FOREIGN KEY (`schedule_id`) REFERENCES `personal_schedule` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线系列地点表';

-- 路线系列路线表（用于记录行程系列中的路线规划结果）
CREATE TABLE IF NOT EXISTS `route_series_route` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `schedule_id` bigint NOT NULL COMMENT '行程ID',
    `total_distance` decimal(10,2) DEFAULT NULL COMMENT '总距离（公里）',
    `total_duration` int DEFAULT NULL COMMENT '总时长（分钟）',
    `route_data` json DEFAULT NULL COMMENT '路线详细数据（JSON格式）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_schedule_id` (`schedule_id`),
    CONSTRAINT `fk_route_route_schedule` FOREIGN KEY (`schedule_id`) REFERENCES `personal_schedule` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线系列路线表';

-- ===========================
-- 兼容性修复（可重复执行）
-- 说明：由于 spring.sql.init.mode=always，下面语句必须幂等
-- ===========================

-- 1) 修复 user 表缺失列（兼容历史数据库）
SET @dbname = DATABASE();
SET @tablename = 'user';

SET @columnname = 'email';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname) AND (TABLE_NAME = @tablename) AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE `', @tablename, '` ADD COLUMN `', @columnname, '` varchar(100) DEFAULT NULL COMMENT ''邮箱'' AFTER `phone`;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'is_deleted';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname) AND (TABLE_NAME = @tablename) AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE `', @tablename, '` ADD COLUMN `', @columnname, '` tinyint DEFAULT ''0'' COMMENT ''逻辑删除'' AFTER `update_time`;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 2) 修复 personal_schedule 行程系列字段（兼容历史数据库）
SET @tablename = 'personal_schedule';

SET @columnname = 'is_route_series';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname) AND (TABLE_NAME = @tablename) AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE `', @tablename, '` ADD COLUMN `', @columnname, '` tinyint DEFAULT ''0'' COMMENT ''是否为行程系列'' AFTER `tag`;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'route_series_theme';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname) AND (TABLE_NAME = @tablename) AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE `', @tablename, '` ADD COLUMN `', @columnname, '` varchar(100) DEFAULT NULL COMMENT ''行程系列主题'' AFTER `is_route_series`;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 3) 兼容更新测试用户密码为 BCrypt（仅当不是 BCrypt 格式时才更新）
-- 原始密码：123456
UPDATE `user`
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO'
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin')
  AND (`password` IS NULL OR `password` NOT LIKE '$2%');