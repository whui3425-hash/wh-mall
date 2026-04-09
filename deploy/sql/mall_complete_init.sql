-- ========================================================-- SaaS Multi-Tenant Mall Complete Database Initialization-- 包含所有必需的表结构及测试数据-- ========================================================

-- ========================================================-- 1. Create Databases-- ========================================================CREATE DATABASE IF NOT EXISTS mall_goods CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS mall_order CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ========================================================-- 2. mall_goods Database Tables (商品、用户、权限、购物车)-- ========================================================USE mall_goods;

-- -------------------- Brand 品牌表 --------------------
CREATE TABLE IF NOT EXISTS brand (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Brand ID',
    name VARCHAR(100) NOT NULL COMMENT 'Brand name',
    image VARCHAR(500) COMMENT 'Brand logo URL',
    initial VARCHAR(1) COMMENT 'Brand initial letter',
    sort INT DEFAULT 0 COMMENT 'Sort order',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Brand table';

-- -------------------- Category 分类表 --------------------
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Category ID',
    name VARCHAR(100) NOT NULL COMMENT 'Category name',
    sort INT DEFAULT 0 COMMENT 'Sort order',
    parent_id INT DEFAULT 0 COMMENT 'Parent category ID (0 for root)',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Product category table';

-- -------------------- SPU 商品主表 --------------------
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

-- -------------------- SKU 商品规格表 --------------------
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

-- -------------------- Ad Items 广告表 --------------------
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

-- -------------------- Category-Brand 关联表 --------------------
CREATE TABLE IF NOT EXISTS category_brand (
    category_id INT NOT NULL COMMENT 'Category ID',
    brand_id INT NOT NULL COMMENT 'Brand ID',
    PRIMARY KEY (category_id, brand_id),
    INDEX idx_brand_id (brand_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Category-Brand relationship';

-- -------------------- Category-Attr 关联表 --------------------
CREATE TABLE IF NOT EXISTS category_attr (
    category_id INT NOT NULL COMMENT 'Category ID',
    attr_id INT NOT NULL COMMENT 'Attribute ID',
    PRIMARY KEY (category_id, attr_id),
    INDEX idx_attr_id (attr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Category-Attribute relationship';

-- -------------------- User Info C端用户表 --------------------
CREATE TABLE IF NOT EXISTS user_info (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID（主键）',
    username VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    password VARCHAR(100) NOT NULL COMMENT '密码（明文存储，仅测试用）',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    name VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
    points INT DEFAULT 0 COMMENT '积分',
    roles VARCHAR(200) DEFAULT 'USER' COMMENT '角色（多个用逗号分隔）',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT '租户ID（多租户隔离）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='C端买家用户信息表';

-- -------------------- Cart 购物车表 --------------------
CREATE TABLE IF NOT EXISTS mall_cart (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '购物车记录ID（主键）',
    user_id VARCHAR(64) NOT NULL DEFAULT '1' COMMENT '用户ID（从JWT Header X-User-Id获取）',
    user_name VARCHAR(100) NOT NULL COMMENT '用户名',
    name VARCHAR(200) NOT NULL COMMENT '商品名称（通过Feign从SKU同步）',
    price INT NOT NULL COMMENT '商品价格（单位：分，通过Feign从SKU同步）',
    image VARCHAR(500) DEFAULT NULL COMMENT '商品图片URL（通过Feign从SKU同步）',
    sku_id VARCHAR(64) NOT NULL COMMENT 'SKU ID（关联商品）',
    num INT NOT NULL DEFAULT 1 COMMENT '购买数量（累加模式）',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT '租户ID（多租户隔离）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_sku (user_id, sku_id, tenant_id) COMMENT '同一用户同一租户下同一SKU只能有一条记录',
    INDEX idx_user_id (user_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表（支持多租户、用户隔离）';

-- -------------------- Sys Admin B端管理员表 --------------------
CREATE TABLE IF NOT EXISTS sys_admin (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Admin ID',
    username VARCHAR(100) NOT NULL COMMENT 'Admin username',
    password VARCHAR(100) NOT NULL COMMENT 'Admin password',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID for SaaS isolation',
    UNIQUE KEY uk_username (username),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System administrator table';

-- -------------------- Role Info 角色表 --------------------
CREATE TABLE IF NOT EXISTS role_info (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Role ID',
    role_name VARCHAR(100) NOT NULL COMMENT 'Role name',
    description VARCHAR(500) COMMENT 'Role description'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role information table';

-- -------------------- Permission 权限表 --------------------
CREATE TABLE IF NOT EXISTS permission (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Permission ID',
    source_name VARCHAR(100) COMMENT 'Permission source name',
    url VARCHAR(500) NOT NULL COMMENT 'API URL pattern',
    url_match INT DEFAULT 0 COMMENT 'URL match mode: 0-exact match, 1-wildcard match',
    service_name VARCHAR(100) COMMENT 'Service name',
    method VARCHAR(20) COMMENT 'HTTP method'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API permission table';

-- -------------------- Role-Permission 角色权限关联表 --------------------
CREATE TABLE IF NOT EXISTS role_permission (
    rid INT NOT NULL COMMENT 'Role ID',
    pid INT NOT NULL COMMENT 'Permission ID',
    PRIMARY KEY (rid, pid),
    INDEX idx_pid (pid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role-Permission mapping table';

-- ========================================================-- 3. mall_order Database Tables (订单表)-- ========================================================USE mall_order;

-- -------------------- Order Info 订单主表 --------------------
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

-- -------------------- Order SKU 订单商品表 --------------------
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

-- -------------------- Order Refund 退款表 --------------------
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

-- ========================================================-- 4. Insert Test Data (mall_goods)-- ========================================================USE mall_goods;

-- -------------------- Clean Old Data --------------------
-- 清空旧数据（避免主键冲突）
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM brand;
DELETE FROM category;
DELETE FROM spu;
DELETE FROM sku;
DELETE FROM ad_items;
DELETE FROM category_brand;
DELETE FROM user_info;
DELETE FROM mall_cart;
DELETE FROM sys_admin;
DELETE FROM role_info;
DELETE FROM permission;
DELETE FROM role_permission;
SET FOREIGN_KEY_CHECKS = 1;

-- -------------------- Brand Data --------------------
INSERT INTO brand (id, name, image, initial, sort, tenant_id) VALUES
(1, 'Apple', '/images/goods/brand-apple.jpg', 'A', 1, '1001'),
(2, 'Sony', '/images/goods/brand-sony.jpg', 'S', 2, '1001'),
(3, 'Logitech', '/images/goods/brand-logitech.jpg', 'L', 3, '1001'),
(4, 'DJI', '/images/goods/brand-dji.jpg', 'D', 4, '1001'),
(5, 'Estée Lauder', '/images/goods/brand-estee-lauder.jpg', 'E', 1, '1002'),
(6, 'Nike', '/images/goods/brand-nike.jpg', 'N', 2, '1002'),
(7, 'Adidas', '/images/goods/brand-adidas.jpg', 'A', 3, '1002'),
(8, 'Zara', '/images/goods/brand-zara.jpg', 'Z', 4, '1002'),
(9, 'Samsung', '/images/goods/brand-samsung.jpg', 'S', 5, '1001'),
(10, 'Huawei', '/images/goods/brand-huawei.jpg', 'H', 6, '1001'),
(11, 'Xiaomi', '/images/goods/brand-xiaomi.jpg', 'X', 7, '1001');

-- -------------------- Category Data --------------------
INSERT INTO category (id, name, sort, parent_id, tenant_id) VALUES
-- Level 1
(1, 'Phone & Accessories', 1, 0, '1001'),
(2, 'Computer & Office', 2, 0, '1001'),
(3, 'Beauty & Fashion', 3, 0, '1001'),
-- Level 2
(11, 'Mobile Phone', 1, 1, '1001'),
(12, 'Phone Accessories', 2, 1, '1001'),
(13, 'Laptop', 1, 2, '1001'),
(14, 'Keyboard & Mouse', 2, 2, '1001'),
(15, 'Skincare', 1, 3, '1001'),
(16, 'Shoes', 2, 3, '1001'),
(17, 'Clothing', 3, 3, '1001'),
-- Level 3
(111, 'Smartphone', 1, 11, '1001'),
(112, 'Feature Phone', 2, 11, '1001'),
(121, 'Headphones', 1, 12, '1001'),
(131, 'Business Laptop', 1, 13, '1001'),
(141, 'Mechanical Keyboard', 1, 14, '1001'),
(151, 'Face Care', 1, 15, '1001'),
(161, 'Sports Shoes', 1, 16, '1001'),
(171, 'Casual Wear', 1, 17, '1001');

-- -------------------- SPU Data --------------------
INSERT INTO spu (id, name, intro, brand_id, category_one_id, category_two_id, category_three_id, images, after_sales_service, content, attribute_list, is_marketable, is_delete, status, tenant_id) VALUES
('SPU001', 'iPhone 15 Pro Max', 'Apple flagship smartphone with A17 Pro chip', 1, 1, 11, 111, '["/images/goods/spu001-1.jpg", "/images/goods/spu001-2.jpg"]', '7-day no-reason return, 1-year warranty', '<h1>iPhone 15 Pro Max</h1><p>The most advanced iPhone ever.</p>', '[{"color": "Natural Titanium", "storage": "256GB"}]', 1, 0, 1, '1001'),
('SPU002', 'MacBook Pro 16 M3', 'Professional laptop with M3 Max chip', 1, 2, 13, 131, '["/images/goods/spu002-1.jpg", "/images/goods/spu002-2.jpg"]', '1 year official warranty, AppleCare+ available', '<h2>MacBook Pro 16</h2><p>Supercharged by M3 Max chip for professionals.</p>', '[{"color": "Space Black", "memory": "36GB"}]', 1, 0, 1, '1001'),
('SPU003', 'Sony WH-1000XM5', 'Industry-leading noise canceling headphones', 2, 1, 12, 121, '["/images/goods/spu003-1.jpg", "/images/goods/spu003-2.jpg"]', '1 year warranty, 30-day return policy', '<h2>Sony WH-1000XM5</h2><p>Best noise canceling with 30-hour battery life.</p>', '[{"color": "Black", "feature": "Active Noise Canceling"}]', 1, 0, 1, '1001'),
('SPU004', 'Logitech MX Mechanical', 'Wireless mechanical keyboard for professionals', 3, 2, 14, 141, '["/images/goods/spu004-1.jpg", "/images/goods/spu004-2.jpg"]', '2 year warranty, business day support', '<h2>MX Mechanical</h2><p>Smart illumination and multi-device connectivity.</p>', '[{"switch": "Tactile", "layout": "Full Size"}]', 1, 0, 1, '1001'),
('SPU005', 'Advanced Night Repair', 'Estée Lauder iconic serum for radiant skin', 5, 3, 15, 151, '["/images/goods/spu005-1.jpg", "/images/goods/spu005-2.jpg"]', 'Authentic guarantee, 30-day return if unopened', '<h2>Advanced Night Repair</h2><p>Reduces signs of aging, hydrates deeply.</p>', '[{"size": "50ml", "skinType": "All Skin Types"}]', 1, 0, 1, '1002'),
('SPU006', 'Air Jordan 1 Retro', 'Classic basketball shoe, streetwear icon', 6, 3, 16, 161, '["/images/goods/spu006-1.jpg", "/images/goods/spu006-2.jpg"]', 'Authentic guarantee, size exchange within 7 days', '<h2>Air Jordan 1 Retro</h2><p>Legendary design that changed basketball forever.</p>', '[{"colorway": "Chicago", "size": "US 9"}]', 1, 0, 1, '1002'),
('SPU007', 'Adidas Ultraboost 23', 'Premium running shoes with Boost technology', 7, 3, 16, 161, '["/images/goods/spu007-1.jpg", "/images/goods/spu007-2.jpg"]', '30-day return, free shipping over $100', '<h2>Ultraboost 23</h2><p>Energy return with every step, superior comfort.</p>', '[{"color": "Core Black", "size": "US 10"}]', 1, 0, 1, '1002'),
('SPU008', 'Zara Oversized Hoodie', 'Trendy streetwear hoodie for everyday style', 8, 3, 17, 171, '["/images/goods/spu008-1.jpg", "/images/goods/spu008-2.jpg"]', '30-day return, easy size exchange', '<h2>Oversized Hoodie</h2><p>Comfortable cotton blend with trendy oversized fit.</p>', '[{"color": "Black", "size": "M"}]', 1, 0, 1, '1002'),
('SPU009', 'Huawei Mate 60 Pro', 'Huawei flagship with Kirin 9000S chip', 10, 1, 11, 111, '["/images/goods/iphone-huawei-1.jpg", "/images/goods/iphone-huawei-2.jpg"]', '7-day no-reason return, 2-year warranty', '<h1>Huawei Mate 60 Pro</h1><p>Satellite communication capability.</p>', '[{"color": "Black", "storage": "512GB"}]', 1, 0, 1, '1001'),
('SPU010', 'Xiaomi 14 Pro', 'Xiaomi flagship with Snapdragon 8 Gen 3', 11, 1, 11, 111, '["/images/goods/iphone-xiaomi-1.jpg", "/images/goods/iphone-xiaomi-2.jpg"]', '7-day no-reason return, 1-year warranty', '<h1>Xiaomi 14 Pro</h1><p>Leica optics system.</p>', '[{"color": "White", "storage": "256GB"}]', 1, 0, 1, '1001');

-- -------------------- SKU Data --------------------
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
-- iPhone SKUs
('SKU001', 'iPhone 15 Pro Max - Natural Titanium 256GB', 999900, 100, '/images/goods/sku-iphone-256.jpg', '["/images/goods/spu001-1.jpg"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color": "Natural Titanium", "storage": "256GB"}', 1, '1001'),
('SKU002', 'iPhone 15 Pro Max - Blue Titanium 512GB', 1199900, 80, '/images/goods/sku-iphone-512.jpg', '["/images/goods/spu001-2.jpg"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color": "Blue Titanium", "storage": "512GB"}', 1, '1001'),
('SKU003', 'iPhone 15 Pro Max - Black Titanium 1TB', 1399900, 50, '/images/goods/pic1.jpg', '["/images/goods/spu001-1.jpg"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color": "Black Titanium", "storage": "1TB"}', 1, '1001'),
-- MacBook SKUs
('SKU004', 'MacBook Pro 16 - Space Black 36GB', 2499900, 30, '/images/goods/spu002-1.jpg', '["/images/goods/spu002-1.jpg"]', 'SPU002', 131, 'Laptop', 1, 'Apple', '{"color": "Space Black", "memory": "36GB"}', 1, '1001'),
('SKU005', 'MacBook Pro 16 - Silver 48GB', 2999900, 20, '/images/goods/spu002-2.jpg', '["/images/goods/spu002-2.jpg"]', 'SPU002', 131, 'Laptop', 1, 'Apple', '{"color": "Silver", "memory": "48GB"}', 1, '1001'),
-- Sony Headphones SKUs
('SKU006', 'Sony WH-1000XM5 - Black', 349900, 150, '/images/goods/spu003-1.jpg', '["/images/goods/spu003-1.jpg"]', 'SPU003', 121, 'Headphones', 2, 'Sony', '{"color": "Black"}', 1, '1001'),
('SKU007', 'Sony WH-1000XM5 - Silver', 349900, 120, '/images/goods/spu003-2.jpg', '["/images/goods/spu003-2.jpg"]', 'SPU003', 121, 'Headphones', 2, 'Sony', '{"color": "Silver"}', 1, '1001'),
-- Keyboard SKUs
('SKU008', 'MX Mechanical - Tactile Full Size', 129900, 80, '/images/goods/spu004-1.jpg', '["/images/goods/spu004-1.jpg"]', 'SPU004', 141, 'Keyboard', 3, 'Logitech', '{"switch": "Tactile", "layout": "Full Size"}', 1, '1001'),
('SKU009', 'MX Mechanical - Linear Mini', 119900, 60, '/images/goods/spu004-2.jpg', '["/images/goods/spu004-2.jpg"]', 'SPU004', 141, 'Keyboard', 3, 'Logitech', '{"switch": "Linear", "layout": "Mini"}', 1, '1001'),
-- Beauty SKUs
('SKU010', 'Advanced Night Repair 50ml', 85000, 200, '/images/goods/spu005-1.jpg', '["/images/goods/spu005-1.jpg"]', 'SPU005', 151, 'Skincare', 5, 'Estée Lauder', '{"size": "50ml"}', 1, '1002'),
('SKU011', 'Advanced Night Repair 100ml', 120000, 150, '/images/goods/spu005-2.jpg', '["/images/goods/spu005-2.jpg"]', 'SPU005', 151, 'Skincare', 5, 'Estée Lauder', '{"size": "100ml"}', 1, '1002'),
-- Shoes SKUs
('SKU012', 'Air Jordan 1 Retro - Chicago US 9', 159900, 50, '/images/goods/spu006-1.jpg', '["/images/goods/spu006-1.jpg"]', 'SPU006', 161, 'Shoes', 6, 'Nike', '{"colorway": "Chicago", "size": "US 9"}', 1, '1002'),
('SKU013', 'Air Jordan 1 Retro - Bred US 10', 169900, 40, '/images/goods/spu006-2.jpg', '["/images/goods/spu006-2.jpg"]', 'SPU006', 161, 'Shoes', 6, 'Nike', '{"colorway": "Bred", "size": "US 10"}', 1, '1002'),
('SKU014', 'Ultraboost 23 - Core Black US 10', 139900, 100, '/images/goods/spu007-1.jpg', '["/images/goods/spu007-1.jpg"]', 'SPU007', 161, 'Shoes', 7, 'Adidas', '{"color": "Core Black", "size": "US 10"}', 1, '1002'),
('SKU015', 'Ultraboost 23 - White US 9', 139900, 90, '/images/goods/spu007-2.jpg', '["/images/goods/spu007-2.jpg"]', 'SPU007', 161, 'Shoes', 7, 'Adidas', '{"color": "White", "size": "US 9"}', 1, '1002'),
-- Clothing SKUs
('SKU016', 'Oversized Hoodie - Black M', 69900, 120, '/images/goods/spu008-1.jpg', '["/images/goods/spu008-1.jpg"]', 'SPU008', 171, 'Clothing', 8, 'Zara', '{"color": "Black", "size": "M"}', 1, '1002'),
('SKU017', 'Oversized Hoodie - Grey L', 69900, 100, '/images/goods/spu008-2.jpg', '["/images/goods/spu008-2.jpg"]', 'SPU008', 171, 'Clothing', 8, 'Zara', '{"color": "Grey", "size": "L"}', 1, '1002'),
-- Huawei SKUs
('SKU018', 'Huawei Mate 60 Pro - Black 512GB', 699900, 80, '/images/goods/sku-huawei-512.jpg', '["/images/goods/iphone-huawei-1.jpg"]', 'SPU009', 111, 'Smartphone', 10, 'Huawei', '{"color": "Black", "storage": "512GB"}', 1, '1001'),
-- Xiaomi SKUs
('SKU019', 'Xiaomi 14 Pro - White 256GB', 499900, 100, '/images/goods/sku-xiaomi-256.jpg', '["/images/goods/iphone-xiaomi-1.jpg"]', 'SPU010', 111, 'Smartphone', 11, 'Xiaomi', '{"color": "White", "storage": "256GB"}', 1, '1001'),
('SKU020', 'Xiaomi 14 Pro - Black 512GB', 549900, 60, '/images/goods/pic2.jpg', '["/images/goods/iphone-xiaomi-2.jpg"]', 'SPU010', 111, 'Smartphone', 11, 'Xiaomi', '{"color": "Black", "storage": "512GB"}', 1, '1001');

-- -------------------- Ad Items Data --------------------
INSERT INTO ad_items (id, name, type, sku_id, sort, tenant_id) VALUES
(1, 'iPhone 15 Pro Max - Featured', 1, 'SKU001', 1, '1001'),
(2, 'Sony WH-1000XM5 - Hot Sale', 1, 'SKU006', 2, '1001'),
(3, 'Air Jordan 1 Retro - Trending', 2, 'SKU012', 1, '1002'),
(4, 'Advanced Night Repair - Best Seller', 2, 'SKU010', 2, '1002'),
(5, 'Huawei Mate 60 Pro - New Arrival', 1, 'SKU018', 3, '1001'),
(6, 'MacBook Pro 16 - Professional', 1, 'SKU004', 4, '1001');

-- -------------------- Category-Brand Data --------------------
INSERT INTO category_brand (category_id, brand_id) VALUES
(111, 1), -- Smartphone - Apple
(111, 2), -- Smartphone - Sony
(111, 10), -- Smartphone - Huawei
(111, 11), -- Smartphone - Xiaomi
(121, 2), -- Headphones - Sony
(131, 1), -- Laptop - Apple
(141, 3), -- Keyboard - Logitech
(151, 5), -- Skincare - Estée Lauder
(161, 6), -- Shoes - Nike
(161, 7), -- Shoes - Adidas
(171, 8); -- Clothing - Zara

-- -------------------- User Info Data --------------------
INSERT INTO user_info (id, username, password, phone, name, points, roles, tenant_id) VALUES
(1, 'zhangsan', '123456', '13800138001', '张三', 100, 'USER', '1001'),
(2, 'lisi', '123456', '13800138002', '李四', 200, 'USER,VIP', '1001'),
(3, 'wangwu', '123456', '13900139001', '王五', 150, 'USER', '1002'),
(4, 'zhaoliu', '123456', '13900139002', '赵六', 300, 'USER,VIP', '1002');

-- -------------------- Sys Admin Data --------------------
INSERT INTO sys_admin (id, username, password, tenant_id) VALUES
(1, 'admin', '123456', '1001'),
(2, 'zhangsan', '123456', '1001'),
(3, 'lisi', '123456', '1002');

-- -------------------- Role Info Data --------------------
INSERT INTO role_info (id, role_name, description) VALUES
(1, 'SUPER_ADMIN', 'Super administrator with all permissions'),
(2, 'ADMIN', 'System administrator'),
(3, 'OPERATOR', 'Business operator'),
(4, 'GUEST', 'Guest user with limited permissions');

-- -------------------- Permission Data --------------------
INSERT INTO permission (id, source_name, url, url_match, service_name, method) VALUES
(1, 'Brand Query', '/goods/brand/**', 1, 'mall-goods-service', 'GET'),
(2, 'Brand Manage', '/goods/brand', 0, 'mall-goods-service', 'POST'),
(3, 'Brand Update', '/goods/brand', 0, 'mall-goods-service', 'PUT'),
(4, 'Brand Delete', '/goods/brand/**', 1, 'mall-goods-service', 'DELETE'),
(5, 'Category Query', '/goods/category/**', 1, 'mall-goods-service', 'GET'),
(6, 'SKU Query', '/goods/sku/**', 1, 'mall-goods-service', 'GET'),
(7, 'SPU Query', '/goods/spu/**', 1, 'mall-goods-service', 'GET'),
(11, 'Order Query', '/order/**', 1, 'mall-order-service', 'GET'),
(12, 'Order Create', '/order', 0, 'mall-order-service', 'POST'),
(13, 'Order Update', '/order', 0, 'mall-order-service', 'PUT'),
(14, 'Order Delete', '/order/**', 1, 'mall-order-service', 'DELETE'),
(21, 'User Query', '/user/**', 1, 'mall-user-service', 'GET'),
(22, 'User Manage', '/user', 0, 'mall-user-service', 'POST'),
(31, 'Cart Query', '/cart/**', 1, 'mall-cart-service', 'GET'),
(32, 'Cart Add', '/cart', 0, 'mall-cart-service', 'POST'),
(33, 'Cart Update', '/cart', 0, 'mall-cart-service', 'PUT'),
(34, 'Cart Delete', '/cart/**', 1, 'mall-cart-service', 'DELETE'),
(41, 'Pay Query', '/pay/**', 1, 'mall-pay-service', 'GET'),
(42, 'Pay Create', '/pay', 0, 'mall-pay-service', 'POST'),
(51, 'Admin Login', '/api/permission/admin/login', 0, 'mall-permission-service', 'POST'),
(52, 'User Login', '/api/user/login', 0, 'mall-user-service', 'POST');

-- -------------------- Role-Permission Data --------------------
INSERT INTO role_permission (rid, pid) VALUES
-- SUPER_ADMIN 所有权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 11), (1, 12), (1, 13), (1, 14),
(1, 21), (1, 22),
(1, 31), (1, 32), (1, 33), (1, 34),
(1, 41), (1, 42),
(1, 51), (1, 52);

-- ========================================================-- 5. Insert Test Data (mall_order)-- ========================================================USE mall_order;

-- -------------------- Clean Old Data --------------------
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM order_info;
DELETE FROM order_sku;
DELETE FROM order_refund;
SET FOREIGN_KEY_CHECKS = 1;

-- -------------------- Order Info Data --------------------
INSERT INTO order_info (id, pay_type, username, recipients, recipients_mobile, recipients_address, 
                        weixin_transaction_id, total_num, moneys, order_status, pay_status, tenant_id) VALUES
('ORDER001', 'weixin', 'zhangsan', '张三', '13800138001', '北京市朝阳区科技园区A座101室', 
 'WX2024010112345678', 3, 299900, 1, 1, '1001'),
('ORDER002', 'alipay', 'lisi', '李四', '13900139002', '上海市浦东新区陆家嘴金融中心88层', 
 'ZFB2024010223456789', 2, 159900, 2, 1, '1001'),
('ORDER003', 'weixin', 'wangwu', '王五', '13700137003', '广州市天河区珠江新城花城大道1号', 
 'WX2024010334567890', 1, 89900, 0, 0, '1001'),
('ORDER004', 'weixin', 'zhangsan', '张三', '13800138001', '深圳市南山区科技园南区B座', 
 'WX2024010445678901', 2, 219900, 1, 1, '1001'),
('ORDER005', 'alipay', 'lisi', '李四', '13900139002', '杭州市西湖区文三路168号', 
 'ZFB2024010556789012', 1, 699900, 3, 1, '1001');

-- -------------------- Order SKU Data --------------------
INSERT INTO order_sku (id, image, sku_id, order_id, name, price, num, money, tenant_id) VALUES
('ORDERSKU001', '/images/goods/order-iphone15.jpg', 'SKU001', 'ORDER001', 'iPhone 15 Pro Max 256GB', 999900, 1, 999900, '1001'),
('ORDERSKU002', '/images/goods/order-airpods.jpg', 'SKU006', 'ORDER001', 'Sony WH-1000XM5', 349900, 2, 699800, '1001'),
('ORDERSKU003', '/images/goods/order-mate60.jpg', 'SKU018', 'ORDER002', 'Huawei Mate 60 Pro 512GB', 699900, 1, 699900, '1001'),
('ORDERSKU004', '/images/goods/order-xiaomi14.jpg', 'SKU019', 'ORDER002', 'Xiaomi 14 Pro 256GB', 499900, 1, 499900, '1001'),
('ORDERSKU005', '/images/goods/order-ipad.jpg', 'SKU004', 'ORDER003', 'MacBook Pro 16', 89900, 1, 89900, '1001'),
('ORDERSKU006', '/images/goods/order-iphone15.jpg', 'SKU001', 'ORDER004', 'iPhone 15 Pro Max 256GB', 999900, 1, 999900, '1001'),
('ORDERSKU007', '/images/goods/order-airpods.jpg', 'SKU008', 'ORDER004', 'MX Mechanical Keyboard', 129900, 1, 129900, '1001'),
('ORDERSKU008', '/images/goods/order-xiaomi14.jpg', 'SKU002', 'ORDER005', 'iPhone 15 Pro Max 512GB', 1199900, 1, 699900, '1001');

-- -------------------- Order Refund Data --------------------
INSERT INTO order_refund (id, order_no, refund_type, order_sku_id, username, status, money, tenant_id) VALUES
('REFUND001', 'ORDER001', 2, 'ORDERSKU002', 'zhangsan', 0, 349900, '1001'),
('REFUND002', 'ORDER002', 1, NULL, 'lisi', 1, 159900, '1001'),
('REFUND003', 'ORDER003', 1, NULL, 'wangwu', 3, 89900, '1001');

-- ========================================================-- Initialization Complete-- ========================================================
USE mall_goods;
SELECT 'mall_goods Database' as Database_Name, COUNT(*) as Table_Count FROM information_schema.tables WHERE table_schema = 'mall_goods';
SELECT 'mall_order Database' as Database_Name, COUNT(*) as Table_Count FROM information_schema.tables WHERE table_schema = 'mall_order';

SELECT '=== Database Initialization Complete ===' as Status;
