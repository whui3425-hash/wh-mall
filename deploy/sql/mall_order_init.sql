-- ========================================================-- mall-order-service Database Initialization Script-- ========================================================-- 1. Create database if not exists
CREATE DATABASE IF NOT EXISTS mall_order CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mall_order;

-- ========================================================-- 2. Create Tables (DDL)-- ========================================================

-- Order info table
CREATE TABLE IF NOT EXISTS order_info (
    id VARCHAR(64) NOT NULL COMMENT 'Order ID (Snowflake ID)',
    pay_type VARCHAR(50) COMMENT 'Payment type',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    pay_time DATETIME COMMENT 'Payment time',
    consign_time DATETIME COMMENT 'Consignment time',
    end_time DATETIME COMMENT 'Order end time',
    username VARCHAR(100) NOT NULL COMMENT 'Username',
    recipients VARCHAR(100) COMMENT 'Recipient name',
    recipients_mobile VARCHAR(20) COMMENT 'Recipient mobile',
    recipients_address VARCHAR(500) COMMENT 'Recipient address',
    weixin_transaction_id VARCHAR(100) COMMENT 'WeChat transaction ID',
    total_num INT DEFAULT 0 COMMENT 'Total quantity',
    moneys INT DEFAULT 0 COMMENT 'Total amount (in cents)',
    order_status TINYINT DEFAULT 0 COMMENT 'Order status: 0-pending, 1-paid, 2-shipped, 3-completed',
    pay_status TINYINT DEFAULT 0 COMMENT 'Payment status: 0-unpaid, 1-paid',
    is_delete TINYINT DEFAULT 0 COMMENT 'Is deleted: 0-no, 1-yes',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    PRIMARY KEY (id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_username (username),
    INDEX idx_order_status (order_status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Order information table';

-- Order SKU table (order items)
CREATE TABLE IF NOT EXISTS order_sku (
    id VARCHAR(64) NOT NULL COMMENT 'Order SKU ID (Snowflake ID)',
    image VARCHAR(500) COMMENT 'Product image URL',
    sku_id VARCHAR(64) NOT NULL COMMENT 'SKU ID',
    order_id VARCHAR(64) NOT NULL COMMENT 'Order ID',
    name VARCHAR(200) NOT NULL COMMENT 'Product name',
    price INT DEFAULT 0 COMMENT 'Unit price (in cents)',
    num INT DEFAULT 0 COMMENT 'Quantity',
    money INT DEFAULT 0 COMMENT 'Total amount (in cents)',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    PRIMARY KEY (id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_order_id (order_id),
    INDEX idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Order SKU items table';

-- Order refund table
CREATE TABLE IF NOT EXISTS order_refund (
    id VARCHAR(64) NOT NULL COMMENT 'Refund ID (Snowflake ID)',
    order_no VARCHAR(64) NOT NULL COMMENT 'Order number',
    refund_type TINYINT DEFAULT 1 COMMENT 'Refund type: 1-full, 2-partial',
    order_sku_id VARCHAR(64) COMMENT 'Order SKU ID (for partial refund)',
    username VARCHAR(100) NOT NULL COMMENT 'Username',
    status TINYINT DEFAULT 0 COMMENT 'Refund status: 0-pending, 1-approved, 2-rejected, 3-completed',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    money INT DEFAULT 0 COMMENT 'Refund amount (in cents)',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    PRIMARY KEY (id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_order_no (order_no),
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Order refund table';

-- ========================================================-- 3. Insert Test Data (DML)-- ========================================================

-- Order test data
INSERT INTO order_info (id, pay_type, username, recipients, recipients_mobile, recipients_address, 
                        weixin_transaction_id, total_num, moneys, order_status, pay_status, tenant_id) VALUES
('ORDER001', 'weixin', 'zhangsan', '张三', '13800138001', '北京市朝阳区科技园区A座101室', 
 'WX2024010112345678', 3, 299900, 1, 1, '1001'),
('ORDER002', 'alipay', 'lisi', '李四', '13900139002', '上海市浦东新区陆家嘴金融中心88层', 
 'ZFB2024010223456789', 2, 159900, 2, 1, '1001'),
('ORDER003', 'weixin', 'wangwu', '王五', '13700137003', '广州市天河区珠江新城花城大道1号', 
 'WX2024010334567890', 1, 89900, 0, 0, '1001');

-- Order SKU test data
INSERT INTO order_sku (id, image, sku_id, order_id, name, price, num, money, tenant_id) VALUES
('ORDERSKU001', 'https://example.com/iphone15.jpg', 'SKU001', 'ORDER001', 'iPhone 15 Pro Max 256GB', 999900, 1, 999900, '1001'),
('ORDERSKU002', 'https://example.com/airpods.jpg', 'SKU002', 'ORDER001', 'AirPods Pro 2', 199900, 2, 399800, '1001'),
('ORDERSKU003', 'https://example.com/mate60.jpg', 'SKU003', 'ORDER002', 'Huawei Mate 60 Pro 512GB', 699900, 1, 699900, '1001'),
('ORDERSKU004', 'https://example.com/xiaomi14.jpg', 'SKU004', 'ORDER002', 'Xiaomi 14 Pro 256GB', 499900, 1, 499900, '1001'),
('ORDERSKU005', 'https://example.com/ipad.jpg', 'SKU005', 'ORDER003', 'iPad Pro 12.9 inch', 899900, 1, 899900, '1001');

-- Order refund test data
INSERT INTO order_refund (id, order_no, refund_type, order_sku_id, username, status, money, tenant_id) VALUES
('REFUND001', 'ORDER001', 2, 'ORDERSKU002', 'zhangsan', 0, 199900, '1001'),
('REFUND002', 'ORDER002', 1, NULL, 'lisi', 1, 159900, '1001'),
('REFUND003', 'ORDER003', 1, NULL, 'wangwu', 3, 89900, '1001');

-- ========================================================-- Initialization Complete-- ========================================================
