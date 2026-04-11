-- ============================================================
-- 【订单表】添加 user_id 字段，用于C端买家查询自己的订单
-- ============================================================

USE mall_goods;

-- 添加 user_id 字段到 order_info 表
ALTER TABLE order_info
ADD COLUMN user_id VARCHAR(32) COMMENT '买家用户ID，C端查询订单时使用' AFTER id;

-- 创建索引加速查询（买家+租户组合查询）
CREATE INDEX idx_user_tenant ON order_info(user_id, tenant_id);

-- 可选：为历史数据设置默认值（如果需要）
-- UPDATE order_info SET user_id = '1' WHERE user_id IS NULL;

SELECT 'order_info 表已添加 user_id 字段并创建索引' AS result;
