-- ==================================================
-- 在 mall_goods 数据库中创建订单相关表
-- ==================================================

USE mall_goods;

-- ==================== 订单主表 ====================
DROP TABLE IF EXISTS order_info;

CREATE TABLE order_info (
    id VARCHAR(64) PRIMARY KEY COMMENT '订单ID',
    pay_type VARCHAR(20) DEFAULT 'weixin' COMMENT '支付方式',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    pay_time DATETIME COMMENT '支付时间',
    consign_time DATETIME COMMENT '发货时间',
    end_time DATETIME COMMENT '结束时间',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    recipients VARCHAR(64) COMMENT '收货人',
    recipients_mobile VARCHAR(20) COMMENT '收货人手机号',
    recipients_address VARCHAR(255) COMMENT '收货地址',
    weixin_transaction_id VARCHAR(64) COMMENT '微信支付流水号',
    total_num INT DEFAULT 0 COMMENT '商品总数量',
    moneys INT DEFAULT 0 COMMENT '订单总金额（分）',
    order_status INT DEFAULT 0 COMMENT '订单状态：0-未支付，1-已支付待发货，2-已发货，3-已完成，4-退款中，5-已退款',
    pay_status INT DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付',
    is_delete INT DEFAULT 0 COMMENT '是否删除：0-正常，1-已删除',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT '租户ID',
    out_trade_no VARCHAR(64) COMMENT '外部交易流水号，用于支付',
    INDEX idx_username (username),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_out_trade_no (out_trade_no),
    INDEX idx_order_status (order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- ==================== 订单SKU明细表 ====================
DROP TABLE IF EXISTS order_sku;

CREATE TABLE order_sku (
    id VARCHAR(64) PRIMARY KEY COMMENT '订单SKU记录ID',
    order_id VARCHAR(64) NOT NULL COMMENT '关联订单ID',
    sku_id VARCHAR(64) NOT NULL COMMENT 'SKU ID',
    name VARCHAR(255) NOT NULL COMMENT '商品名称',
    price INT NOT NULL COMMENT '单价（分）',
    num INT NOT NULL COMMENT '数量',
    money INT NOT NULL COMMENT '小计金额（分）= price * num',
    image VARCHAR(500) COMMENT '商品图片URL',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT '租户ID',
    INDEX idx_order_id (order_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单SKU明细表';

-- 验证表结构
SHOW TABLES LIKE 'order%';
