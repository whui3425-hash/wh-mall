-- ============================================
-- 购物车表结构更新脚本
-- 添加 user_id 字段支持从 JWT Header 获取用户ID
-- ============================================

USE mall_goods;


CREATE TABLE `cart_item` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `user_id` bigint(20) NOT NULL COMMENT '买家ID',
                             `tenant_id` bigint(20) NOT NULL COMMENT '所属租户ID',
                             `sku_id` bigint(20) NOT NULL COMMENT '商品SKU ID',
                             `num` int(11) NOT NULL DEFAULT '1' COMMENT '购买数量',
                             `price` decimal(10,2) DEFAULT NULL COMMENT '加入时单价',
                             `sku_name` varchar(200) DEFAULT NULL COMMENT '商品名称',
                             `sku_image` varchar(500) DEFAULT NULL COMMENT '商品图片',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             KEY `uk_user_tenant_sku` (`user_id`,`tenant_id`,`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车明细表';

-- 添加 user_id 字段到 mall_cart 表
ALTER TABLE mall_cart 
ADD COLUMN user_id VARCHAR(64) NOT NULL DEFAULT '1' COMMENT '用户ID（从JWT Header X-User-Id获取）' AFTER id;

-- 添加索引加速查询
ALTER TABLE mall_cart 
ADD INDEX idx_user_id (user_id),
ADD INDEX idx_user_sku (user_id, sku_id);

-- 更新现有数据的 user_id（根据 user_name 推断）
UPDATE mall_cart SET user_id = '1' WHERE user_name = 'zhangsan';
UPDATE mall_cart SET user_id = '2' WHERE user_name = 'lisi';
UPDATE mall_cart SET user_id = '3' WHERE user_name = 'wangwu';
UPDATE mall_cart SET user_id = '4' WHERE user_name = 'zhaoliu';

-- 查看表结构
DESCRIBE mall_cart;
