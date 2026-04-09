-- ==================================================
-- 创建 order_sku 表（订单商品明细表）
-- ==================================================

USE mall_order;

-- 如果表存在则删除（注意：生产环境慎用）
DROP TABLE IF EXISTS order_sku;

-- 创建订单SKU明细表
CREATE TABLE order_sku (
    id VARCHAR(64) PRIMARY KEY COMMENT '订单SKU记录ID',
    order_id VARCHAR(64) NOT NULL COMMENT '关联订单ID',
    sku_id VARCHAR(64) NOT NULL COMMENT 'SKU ID',
    name VARCHAR(255) NOT NULL COMMENT '商品名称',
    price INT NOT NULL COMMENT '单价（分）',
    num INT NOT NULL COMMENT '数量',
    money INT NOT NULL COMMENT '小计金额（分）= price * num',
    image VARCHAR(500) COMMENT '商品图片URL',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT '租户ID（多租户隔离）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单SKU明细表';

-- 添加索引
CREATE INDEX idx_order_id ON order_sku(order_id);
CREATE INDEX idx_sku_id ON order_sku(sku_id);
CREATE INDEX idx_tenant_id ON order_sku(tenant_id);

-- 验证表结构
DESCRIBE order_sku;
