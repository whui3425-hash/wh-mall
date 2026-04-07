-- ============================================
-- C 端买家用户表初始化脚本
-- 租户 1001 和 1002 各创建 2 个测试用户
-- ============================================

USE mall_goods;

-- 创建 user_info 表
CREATE TABLE IF NOT EXISTS `user_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（明文存储，仅测试用）',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `name` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
    `points` INT DEFAULT 0 COMMENT '积分',
    `roles` VARCHAR(200) DEFAULT 'USER' COMMENT '角色（多个用逗号分隔）',
    `tenant_id` VARCHAR(32) DEFAULT '1001' COMMENT '租户ID（多租户隔离）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    INDEX `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='C端买家用户信息表';

-- ============================================
-- 租户 1001 的用户（张三、李四）
-- ============================================
INSERT INTO `user_info` (`id`, `username`, `password`, `phone`, `name`, `points`, `roles`, `tenant_id`) VALUES
(1, 'zhangsan', '123456', '13800138001', '张三', 100, 'USER', '1001'),
(2, 'lisi', '123456', '13800138002', '李四', 200, 'USER,VIP', '1001');

-- ============================================
-- 租户 1002 的用户（王五、赵六）
-- ============================================
INSERT INTO `user_info` (`id`, `username`, `password`, `phone`, `name`, `points`, `roles`, `tenant_id`) VALUES
(3, 'wangwu', '123456', '13900139001', '王五', 150, 'USER', '1002'),
(4, 'zhaoliu', '123456', '13900139002', '赵六', 300, 'USER,VIP', '1002');
