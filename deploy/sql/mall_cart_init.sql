-- ========================================================-- mall-cart-service Database Initialization Script-- ========================================================-- 1. Create database if not exists
CREATE DATABASE IF NOT EXISTS mall_cart CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mall_cart;

-- ========================================================-- 2. Create Tables (DDL)-- ========================================================

-- Cart table
CREATE TABLE IF NOT EXISTS mall_cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Cart item ID',
    user_name VARCHAR(100) NOT NULL COMMENT 'Username (buyer)',
    name VARCHAR(200) NOT NULL COMMENT 'Product name',
    price INT DEFAULT 0 COMMENT 'Unit price (in cents)',
    image VARCHAR(500) COMMENT 'Product image URL',
    sku_id VARCHAR(64) NOT NULL COMMENT 'SKU ID',
    num INT DEFAULT 1 COMMENT 'Quantity',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_user_name (user_name),
    INDEX idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Shopping cart table';

-- ========================================================-- 3. Insert Test Data (DML)-- ========================================================

-- Cart test data - Tenant A (1001)
INSERT INTO mall_cart (id, user_name, name, price, image, sku_id, num, tenant_id) VALUES
(1, 'zhangsan', 'iPhone 15 Pro Max 256GB', 999900, 'https://example.com/iphone15.jpg', 'SKU001', 1, '1001'),
(2, 'zhangsan', 'AirPods Pro 2nd Generation', 199900, 'https://example.com/airpods.jpg', 'SKU002', 2, '1001');

-- Cart test data - Tenant B (1002) - Same user, different tenant
INSERT INTO mall_cart (id, user_name, name, price, image, sku_id, num, tenant_id) VALUES
(3, 'zhangsan', 'Huawei Mate 60 Pro 512GB', 699900, 'https://example.com/mate60.jpg', 'SKU003', 1, '1002'),
(4, 'zhangsan', 'Xiaomi 14 Pro 256GB', 499900, 'https://example.com/xiaomi14.jpg', 'SKU004', 1, '1002');

-- ========================================================-- Initialization Complete-- ========================================================
