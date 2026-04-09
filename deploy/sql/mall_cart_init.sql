-- ============================================
-- 购物车表初始化脚本
-- 包含 mall_cart 表 DDL 和 DML（多租户、用户隔离设计）
-- ============================================

USE mall_goods;

-- ============================================
-- 删除旧表（如需重新创建）
-- ============================================
-- DROP TABLE IF EXISTS mall_cart;

-- ============================================
-- 创建购物车表
-- ============================================
CREATE TABLE IF NOT EXISTS `mall_cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车记录ID（主键）',
    `user_id` VARCHAR(64) NOT NULL DEFAULT '1' COMMENT '用户ID（从JWT Header X-User-Id获取）',
    `user_name` VARCHAR(100) NOT NULL COMMENT '用户名',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称（通过Feign从SKU同步）',
    `price` INT NOT NULL COMMENT '商品价格（单位：分，通过Feign从SKU同步）',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片URL（通过Feign从SKU同步）',
    `sku_id` VARCHAR(64) NOT NULL COMMENT 'SKU ID（关联商品）',
    `num` INT NOT NULL DEFAULT 1 COMMENT '购买数量（累加模式）',
    `tenant_id` VARCHAR(32) DEFAULT '1001' COMMENT '租户ID（多租户隔离）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_sku` (`user_id`, `sku_id`, `tenant_id`) COMMENT '同一用户同一租户下同一SKU只能有一条记录',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_sku_id` (`sku_id`),
    INDEX `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表（支持多租户、用户隔离）';

-- ============================================
-- 清理现有数据（可选）
-- ============================================
-- TRUNCATE TABLE mall_cart;

-- ============================================
-- 插入测试数据（可选）
-- ============================================
-- 租户 1001 用户 zhangsan (user_id=1) 的购物车
-- INSERT INTO `mall_cart` (`user_id`, `user_name`, `name`, `price`, `image`, `sku_id`, `num`, `tenant_id`) VALUES
-- ('1', 'zhangsan', 'iPhone 15 Pro Max', 99900, '/images/goods/spu001-1.jpg', 'SKU_001', 2, '1001'),
-- ('1', 'zhangsan', 'MacBook Pro M3', 129900, '/images/goods/spu002-1.jpg', 'SKU_002', 1, '1001');

-- 租户 1002 用户 wangwu (user_id=3) 的购物车  
-- INSERT INTO `mall_cart` (`user_id`, `user_name`, `name`, `price`, `image`, `sku_id`, `num`, `tenant_id`) VALUES
-- ('3', 'wangwu', 'SK-II神仙水', 154000, '/images/goods/spu005-1.jpg', 'SKU_101', 1, '1002');

-- ============================================
-- 查看表结构
-- ============================================
DESCRIBE mall_cart;
