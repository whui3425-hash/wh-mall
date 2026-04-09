-- ========================================================
-- mall-goods-service Database Initialization Script
-- ========================================================

-- 1. Create database if not exists
CREATE DATABASE IF NOT EXISTS mall_goods CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mall_goods;

-- ========================================================
-- 2. Create Tables (DDL)
-- ========================================================

-- Brand table
CREATE TABLE IF NOT EXISTS brand (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Brand ID',
    name VARCHAR(100) NOT NULL COMMENT 'Brand name',
    image VARCHAR(500) COMMENT 'Brand logo URL',
    initial VARCHAR(1) COMMENT 'Brand initial letter',
    sort INT DEFAULT 0 COMMENT 'Sort order',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Brand table';

-- Category table (three-level category system)
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Category ID',
    name VARCHAR(100) NOT NULL COMMENT 'Category name',
    sort INT DEFAULT 0 COMMENT 'Sort order',
    parent_id INT DEFAULT 0 COMMENT 'Parent category ID (0 for root)',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Product category table';

-- SPU table (Standard Product Unit)
CREATE TABLE IF NOT EXISTS spu (
    id VARCHAR(64) PRIMARY KEY COMMENT 'SPU ID (Snowflake ID)',
    name VARCHAR(200) NOT NULL COMMENT 'Product name',
    intro VARCHAR(500) COMMENT 'Product introduction',
    brand_id INT COMMENT 'Brand ID',
    category_one_id INT COMMENT 'First-level category ID',
    category_two_id INT COMMENT 'Second-level category ID',
    category_three_id INT COMMENT 'Third-level category ID',
    images TEXT COMMENT 'Product images (JSON array)',
    after_sales_service TEXT COMMENT 'After-sales service description',
    content LONGTEXT COMMENT 'Product detail content (HTML)',
    attribute_list TEXT COMMENT 'Product attributes list (JSON)',
    is_marketable TINYINT DEFAULT 1 COMMENT 'Is marketable: 1-yes, 0-no',
    is_delete TINYINT DEFAULT 0 COMMENT 'Is deleted: 0-no, 1-yes',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-approved, 0-pending',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_brand_id (brand_id),
    INDEX idx_category_three_id (category_three_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SPU table (Standard Product Unit)';

-- SKU table (Stock Keeping Unit)
CREATE TABLE IF NOT EXISTS sku (
    id VARCHAR(64) PRIMARY KEY COMMENT 'SKU ID (Snowflake ID)',
    name VARCHAR(200) NOT NULL COMMENT 'SKU name',
    price INT NOT NULL COMMENT 'Price (in cents)',
    num INT DEFAULT 0 COMMENT 'Stock quantity',
    image VARCHAR(500) COMMENT 'Main image URL',
    images TEXT COMMENT 'Image gallery (JSON array)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    spu_id VARCHAR(64) COMMENT 'SPU ID',
    category_id INT COMMENT 'Category ID',
    category_name VARCHAR(100) COMMENT 'Category name',
    brand_id INT COMMENT 'Brand ID',
    brand_name VARCHAR(100) COMMENT 'Brand name',
    sku_attribute VARCHAR(500) COMMENT 'SKU attributes (JSON)',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-normal, 2-offline, 3-deleted',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_spu_id (spu_id),
    INDEX idx_category_id (category_id),
    INDEX idx_brand_id (brand_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU table (Stock Keeping Unit)';

-- Ad items table (recommended products/ads)
CREATE TABLE IF NOT EXISTS ad_items (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Ad ID',
    name VARCHAR(100) COMMENT 'Ad name',
    type INT DEFAULT 1 COMMENT 'Type: 1-index banner, 2-category banner',
    sku_id VARCHAR(64) COMMENT 'SKU ID',
    sort INT DEFAULT 0 COMMENT 'Sort order',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Ad items table';

-- Category-Brand relationship table (middle table)
CREATE TABLE IF NOT EXISTS category_brand (
    category_id INT NOT NULL COMMENT 'Category ID',
    brand_id INT NOT NULL COMMENT 'Brand ID',
    PRIMARY KEY (category_id, brand_id),
    INDEX idx_brand_id (brand_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Category-Brand relationship';

-- Category-Attribute relationship table (middle table)
CREATE TABLE IF NOT EXISTS category_attr (
    category_id INT NOT NULL COMMENT 'Category ID',
    attr_id INT NOT NULL COMMENT 'Attribute ID',
    PRIMARY KEY (category_id, attr_id),
    INDEX idx_attr_id (attr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Category-Attribute relationship';

-- ========================================================
-- 3. Insert Test Data (DML)
-- ========================================================

-- Brand test data
INSERT INTO brand (id, name, image, initial, sort, tenant_id) VALUES
(1, 'Apple', '/images/goods/brand-apple.jpg', 'A', 1, '1001'),
(2, 'Huawei', '/images/goods/brand-huawei.jpg', 'H', 2, '1001'),
(3, 'Xiaomi', '/images/goods/brand-xiaomi.jpg', 'X', 3, '1001');

-- Category test data (three-level hierarchy)
INSERT INTO category (id, name, sort, parent_id, tenant_id) VALUES
-- Level 1 categories
(1, 'Phone & Accessories', 1, 0, '1001'),
(2, 'Computer & Office', 2, 0, '1001'),
(3, 'Home Appliance', 3, 0, '1001'),
-- Level 2 categories (under Phone)
(11, 'Mobile Phone', 1, 1, '1001'),
(12, 'Phone Accessories', 2, 1, '1001'),
-- Level 3 categories (under Mobile Phone)
(111, 'Smartphone', 1, 11, '1001'),
(112, 'Feature Phone', 2, 11, '1001');

-- SPU test data
INSERT INTO spu (id, name, intro, brand_id, category_one_id, category_two_id, category_three_id, images, after_sales_service, content, attribute_list, is_marketable, is_delete, status, tenant_id) VALUES
('100000001', 'iPhone 15 Pro Max', 'Apple flagship smartphone with A17 Pro chip', 1, 1, 11, 111, '["/images/goods/spu001-1.jpg", "/images/goods/spu001-2.jpg"]', '7-day no-reason return, 1-year warranty', '<h1>iPhone 15 Pro Max</h1><p>The most advanced iPhone ever.</p>', '[{"color": "Natural Titanium", "storage": "256GB"}]', 1, 0, 1, '1001'),
('100000002', 'Huawei Mate 60 Pro', 'Huawei flagship with Kirin 9000S chip', 2, 1, 11, 111, '["/images/goods/iphone-huawei-1.jpg", "/images/goods/iphone-huawei-2.jpg"]', '7-day no-reason return, 2-year warranty', '<h1>Huawei Mate 60 Pro</h1><p>Satellite communication capability.</p>', '[{"color": "Black", "storage": "512GB"}]', 1, 0, 1, '1001'),
('100000003', 'Xiaomi 14 Pro', 'Xiaomi flagship with Snapdragon 8 Gen 3', 3, 1, 11, 111, '["/images/goods/iphone-xiaomi-1.jpg", "/images/goods/iphone-xiaomi-2.jpg"]', '7-day no-reason return, 1-year warranty', '<h1>Xiaomi 14 Pro</h1><p>Leica optics system.</p>', '[{"color": "White", "storage": "256GB"}]', 1, 0, 1, '1001');

-- SKU test data
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
-- iPhone 15 Pro Max SKUs
('200000001', 'iPhone 15 Pro Max - Natural Titanium 256GB', 999900, 100, '/images/goods/sku-iphone-256.jpg', '["/images/goods/spu001-1.jpg"]', '100000001', 111, 'Smartphone', 1, 'Apple', '{"color": "Natural Titanium", "storage": "256GB"}', 1, '1001'),
('200000002', 'iPhone 15 Pro Max - Blue Titanium 512GB', 1199900, 80, '/images/goods/sku-iphone-512.jpg', '["/images/goods/spu001-2.jpg"]', '100000001', 111, 'Smartphone', 1, 'Apple', '{"color": "Blue Titanium", "storage": "512GB"}', 1, '1001'),
-- Huawei Mate 60 Pro SKUs
('200000003', 'Huawei Mate 60 Pro - Black 512GB', 699900, 150, '/images/goods/sku-huawei-512.jpg', '["/images/goods/iphone-huawei-1.jpg"]', '100000002', 111, 'Smartphone', 2, 'Huawei', '{"color": "Black", "storage": "512GB"}', 1, '1001'),
-- Xiaomi 14 Pro SKUs
('200000004', 'Xiaomi 14 Pro - White 256GB', 499900, 200, '/images/goods/sku-xiaomi-256.jpg', '["/images/goods/iphone-xiaomi-1.jpg"]', '100000003', 111, 'Smartphone', 3, 'Xiaomi', '{"color": "White", "storage": "256GB"}', 1, '1001');

-- Ad items test data
INSERT INTO ad_items (id, name, type, sku_id, sort, tenant_id) VALUES
(1, 'iPhone 15 Pro Max - Featured', 1, '200000001', 1, '1001'),
(2, 'Huawei Mate 60 Pro - Hot Sale', 1, '200000003', 2, '1001'),
(3, 'Xiaomi 14 Pro - New Arrival', 2, '200000004', 1, '1001');

-- Category-Brand relationship test data
INSERT INTO category_brand (category_id, brand_id) VALUES
(111, 1), -- Smartphone - Apple
(111, 2), -- Smartphone - Huawei
(111, 3); -- Smartphone - Xiaomi

-- Category-Attribute relationship test data
INSERT INTO category_attr (category_id, attr_id) VALUES
(111, 1), -- Smartphone - Color
(111, 2), -- Smartphone - Storage
(111, 3); -- Smartphone - Network

-- ========================================================
-- Initialization Complete
-- ========================================================
