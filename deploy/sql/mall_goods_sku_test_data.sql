-- ========================================================
-- mall_goods SKU Test Data for High-Concurrency Stock Deduction
-- Supports: Order Flow + FinTech-Grade Anti-Duplicate Stock Deduction
-- Tenants: 1001 (3C Digital), 1002 (Beauty & Fashion)
-- ========================================================
USE mall_goods;

-- ========================================================
-- 1. SKU Table DDL (If Not Exists)
-- ========================================================
CREATE TABLE IF NOT EXISTS sku (
    id VARCHAR(64) PRIMARY KEY COMMENT 'SKU ID (Snowflake ID)',
    name VARCHAR(200) NOT NULL COMMENT 'SKU name (with specific specs)',
    price INT NOT NULL COMMENT 'Price (in cents)',
    num INT DEFAULT 1000 COMMENT 'Stock quantity - baseline for concurrency testing',
    image VARCHAR(500) COMMENT 'Main image URL',
    images TEXT COMMENT 'Image gallery (JSON array)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    spu_id VARCHAR(64) COMMENT 'SPU ID - references parent product',
    category_id INT COMMENT 'Category ID',
    category_name VARCHAR(100) COMMENT 'Category name',
    brand_id INT COMMENT 'Brand ID',
    brand_name VARCHAR(100) COMMENT 'Brand name',
    sku_attribute VARCHAR(500) COMMENT 'SKU attributes (JSON format)',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-normal, 2-offline, 3-deleted',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID - data isolation key',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_spu_id (spu_id),
    INDEX idx_category_id (category_id),
    INDEX idx_brand_id (brand_id),
    INDEX idx_status (status),
    INDEX idx_num (num) COMMENT 'Index for stock queries'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU table (Stock Keeping Unit) for high-concurrency testing';

-- ========================================================
-- 2. SKU Test Data - Tenant 1001 (3C Digital Tech)
-- Baseline Stock: 1000 units per SKU for millisecond-level concurrency testing
-- ========================================================

-- SPU001: iPhone 15 Pro Max (Apple) - 9 SKU variants
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
('SKU1001001', 'iPhone 15 Pro Max - Natural Titanium 256GB', 999900, 1000, 'https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80', '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color":"Natural Titanium","storage":"256GB","network":"5G"}', 1, '1001'),
('SKU1001002', 'iPhone 15 Pro Max - Natural Titanium 512GB', 1199900, 1000, 'https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80', '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color":"Natural Titanium","storage":"512GB","network":"5G"}', 1, '1001'),
('SKU1001003', 'iPhone 15 Pro Max - Natural Titanium 1TB', 1399900, 1000, 'https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80', '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color":"Natural Titanium","storage":"1TB","network":"5G"}', 1, '1001'),
('SKU1001004', 'iPhone 15 Pro Max - Blue Titanium 256GB', 999900, 1000, 'https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80', '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color":"Blue Titanium","storage":"256GB","network":"5G"}', 1, '1001'),
('SKU1001005', 'iPhone 15 Pro Max - Blue Titanium 512GB', 1199900, 1000, 'https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80', '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color":"Blue Titanium","storage":"512GB","network":"5G"}', 1, '1001'),
('SKU1001006', 'iPhone 15 Pro Max - Black Titanium 256GB', 999900, 1000, 'https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80', '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color":"Black Titanium","storage":"256GB","network":"5G"}', 1, '1001'),
('SKU1001007', 'iPhone 15 Pro Max - Black Titanium 1TB', 1399900, 1000, 'https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80', '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color":"Black Titanium","storage":"1TB","network":"5G"}', 1, '1001'),
('SKU1001008', 'iPhone 15 Pro Max - White Titanium 512GB', 1199900, 1000, 'https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80', '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color":"White Titanium","storage":"512GB","network":"5G"}', 1, '1001'),
('SKU1001009', 'iPhone 15 Pro Max - Desert Titanium 1TB', 1499900, 1000, 'https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80', '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80"]', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{"color":"Desert Titanium","storage":"1TB","network":"5G"}', 1, '1001');

-- SPU002: MacBook Pro 16 M3 (Apple) - 8 SKU variants
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
('SKU1002001', 'MacBook Pro 16 M3 Max - Space Black 36GB/512GB', 2499900, 1000, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80', '["https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80","https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80"]', 'SPU002', 211, 'Laptop', 1, 'Apple', '{"color":"Space Black","memory":"36GB","storage":"512GB","chip":"M3 Max"}', 1, '1001'),
('SKU1002002', 'MacBook Pro 16 M3 Max - Space Black 48GB/1TB', 2999900, 1000, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80', '["https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80"]', 'SPU002', 211, 'Laptop', 1, 'Apple', '{"color":"Space Black","memory":"48GB","storage":"1TB","chip":"M3 Max"}', 1, '1001'),
('SKU1002003', 'MacBook Pro 16 M3 Max - Space Black 64GB/2TB', 3499900, 1000, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80', '["https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80"]', 'SPU002', 211, 'Laptop', 1, 'Apple', '{"color":"Space Black","memory":"64GB","storage":"2TB","chip":"M3 Max"}', 1, '1001'),
('SKU1002004', 'MacBook Pro 16 M3 Max - Silver 36GB/512GB', 2499900, 1000, 'https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80', '["https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80"]', 'SPU002', 211, 'Laptop', 1, 'Apple', '{"color":"Silver","memory":"36GB","storage":"512GB","chip":"M3 Max"}', 1, '1001'),
('SKU1002005', 'MacBook Pro 16 M3 Max - Silver 48GB/1TB', 2999900, 1000, 'https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80', '["https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80"]', 'SPU002', 211, 'Laptop', 1, 'Apple', '{"color":"Silver","memory":"48GB","storage":"1TB","chip":"M3 Max"}', 1, '1001'),
('SKU1002006', 'MacBook Pro 16 M3 Max - Silver 64GB/2TB', 3499900, 1000, 'https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80', '["https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80"]', 'SPU002', 211, 'Laptop', 1, 'Apple', '{"color":"Silver","memory":"64GB","storage":"2TB","chip":"M3 Max"}', 1, '1001'),
('SKU1002007', 'MacBook Pro 16 M3 Pro - Space Black 18GB/512GB', 1999900, 1000, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80', '["https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80"]', 'SPU002', 211, 'Laptop', 1, 'Apple', '{"color":"Space Black","memory":"18GB","storage":"512GB","chip":"M3 Pro"}', 1, '1001'),
('SKU1002008', 'MacBook Pro 16 M3 Pro - Silver 18GB/512GB', 1999900, 1000, 'https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80', '["https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80"]', 'SPU002', 211, 'Laptop', 1, 'Apple', '{"color":"Silver","memory":"18GB","storage":"512GB","chip":"M3 Pro"}', 1, '1001');

-- SPU003: Sony WH-1000XM5 (Sony) - 6 SKU variants
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
('SKU1003001', 'Sony WH-1000XM5 - Black (Standard)', 349900, 1000, 'https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80', '["https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80","https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&q=80"]', 'SPU003', 121, 'Headphone', 2, 'Sony', '{"color":"Black","feature":"Active Noise Canceling","battery":"30 hours"}', 1, '1001'),
('SKU1003002', 'Sony WH-1000XM5 - Black (Ambient Sound Edition)', 369900, 1000, 'https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80', '["https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80"]', 'SPU003', 121, 'Headphone', 2, 'Sony', '{"color":"Black","feature":"Ambient Sound","battery":"30 hours"}', 1, '1001'),
('SKU1003003', 'Sony WH-1000XM5 - Silver (Standard)', 349900, 1000, 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&q=80', '["https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&q=80"]', 'SPU003', 121, 'Headphone', 2, 'Sony', '{"color":"Silver","feature":"Active Noise Canceling","battery":"30 hours"}', 1, '1001'),
('SKU1003004', 'Sony WH-1000XM5 - Silver (Ambient Sound Edition)', 369900, 1000, 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&q=80', '["https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&q=80"]', 'SPU003', 121, 'Headphone', 2, 'Sony', '{"color":"Silver","feature":"Ambient Sound","battery":"30 hours"}', 1, '1001'),
('SKU1003005', 'Sony WH-1000XM5 - Midnight Blue Limited', 379900, 1000, 'https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80', '["https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80"]', 'SPU003', 121, 'Headphone', 2, 'Sony', '{"color":"Midnight Blue","feature":"Active Noise Canceling","battery":"30 hours","edition":"Limited"}', 1, '1001'),
('SKU1003006', 'Sony WH-1000XM5 - Noise Canceling Pro Bundle', 399900, 1000, 'https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80', '["https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80"]', 'SPU003', 121, 'Headphone', 2, 'Sony', '{"color":"Black","feature":"ANC Pro","battery":"40 hours","bundle":"Premium Case"}', 1, '1001');

-- SPU004: Logitech MX Mechanical (Logitech) - 6 SKU variants
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
('SKU1004001', 'Logitech MX Mechanical - Full Size Tactile', 169900, 1000, 'https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80', '["https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80","https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&q=80"]', 'SPU004', 221, 'Keyboard', 3, 'Logitech', '{"layout":"Full Size","switch":"Tactile","backlight":"Smart Illumination"}', 1, '1001'),
('SKU1004002', 'Logitech MX Mechanical - Full Size Linear', 169900, 1000, 'https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80', '["https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80"]', 'SPU004', 221, 'Keyboard', 3, 'Logitech', '{"layout":"Full Size","switch":"Linear","backlight":"Smart Illumination"}', 1, '1001'),
('SKU1004003', 'Logitech MX Mechanical - Full Size Clicky', 169900, 1000, 'https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80', '["https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80"]', 'SPU004', 221, 'Keyboard', 3, 'Logitech', '{"layout":"Full Size","switch":"Clicky","backlight":"Smart Illumination"}', 1, '1001'),
('SKU1004004', 'Logitech MX Mechanical - Mini Tactile', 149900, 1000, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&q=80', '["https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&q=80"]', 'SPU004', 221, 'Keyboard', 3, 'Logitech', '{"layout":"Mini","switch":"Tactile","backlight":"Smart Illumination"}', 1, '1001'),
('SKU1004005', 'Logitech MX Mechanical - Mini Linear', 149900, 1000, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&q=80', '["https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&q=80"]', 'SPU004', 221, 'Keyboard', 3, 'Logitech', '{"layout":"Mini","switch":"Linear","backlight":"Smart Illumination"}', 1, '1001'),
('SKU1004006', 'Logitech MX Mechanical - Full Size MX Speed', 189900, 1000, 'https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80', '["https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80"]', 'SPU004', 221, 'Keyboard', 3, 'Logitech', '{"layout":"Full Size","switch":"MX Speed","backlight":"Smart Illumination","features":"Multi-Device"}', 1, '1001');

-- ========================================================
-- 3. SKU Test Data - Tenant 1002 (Beauty & Fashion)
-- Baseline Stock: 1000 units per SKU for millisecond-level concurrency testing
-- ========================================================

-- SPU005: Estée Lauder Advanced Night Repair - 9 SKU variants
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
('SKU2005001', 'Advanced Night Repair Serum - 30ml All Skin Types', 85000, 1000, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80', '["https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80","https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=800&q=80"]', 'SPU005', 311, 'Skincare', 5, 'Estée Lauder', '{"size":"30ml","skinType":"All Skin Types","benefit":"Anti-aging"}', 1, '1002'),
('SKU2005002', 'Advanced Night Repair Serum - 30ml Dry Skin', 85000, 1000, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80', '["https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80"]', 'SPU005', 311, 'Skincare', 5, 'Estée Lauder', '{"size":"30ml","skinType":"Dry","benefit":"Deep Hydration"}', 1, '1002'),
('SKU2005003', 'Advanced Night Repair Serum - 30ml Oily Skin', 85000, 1000, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80', '["https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80"]', 'SPU005', 311, 'Skincare', 5, 'Estée Lauder', '{"size":"30ml","skinType":"Oily","benefit":"Oil Control"}', 1, '1002'),
('SKU2005004', 'Advanced Night Repair Serum - 50ml All Skin Types', 120000, 1000, 'https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=800&q=80', '["https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=800&q=80"]', 'SPU005', 311, 'Skincare', 5, 'Estée Lauder', '{"size":"50ml","skinType":"All Skin Types","benefit":"Anti-aging"}', 1, '1002'),
('SKU2005005', 'Advanced Night Repair Serum - 50ml Dry Skin', 120000, 1000, 'https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=800&q=80', '["https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=800&q=80"]', 'SPU005', 311, 'Skincare', 5, 'Estée Lauder', '{"size":"50ml","skinType":"Dry","benefit":"Deep Hydration"}', 1, '1002'),
('SKU2005006', 'Advanced Night Repair Serum - 75ml All Skin Types', 165000, 1000, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80', '["https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80"]', 'SPU005', 311, 'Skincare', 5, 'Estée Lauder', '{"size":"75ml","skinType":"All Skin Types","benefit":"Anti-aging"}', 1, '1002'),
('SKU2005007', 'Advanced Night Repair - 50ml Holiday Set 2024', 145000, 1000, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80', '["https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80"]', 'SPU005', 311, 'Skincare', 5, 'Estée Lauder', '{"size":"50ml","set":"Holiday 2024","includes":"Eye Cream Sample"}', 1, '1002'),
('SKU2005008', 'Advanced Night Repair - 75ml Luxury Set', 195000, 1000, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80', '["https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80"]', 'SPU005', 311, 'Skincare', 5, 'Estée Lauder', '{"size":"75ml","set":"Luxury","includes":"Micro Essence + Eye Cream"}', 1, '1002'),
('SKU2005009', 'Advanced Night Repair - 100ml Travel Exclusive', 220000, 1000, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80', '["https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80"]', 'SPU005', 311, 'Skincare', 5, 'Estée Lauder', '{"size":"100ml","type":"Travel Exclusive","benefit":"Value Size"}', 1, '1002');

-- SPU006: Nike Air Jordan 1 Retro - 9 SKU variants
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
('SKU2006001', 'Air Jordan 1 Retro - Chicago US 8', 180000, 1000, 'https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80', '["https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80","https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&q=80"]', 'SPU006', 321, 'Sneakers', 6, 'Nike', '{"colorway":"Chicago","size":"US 8","style":"High Top"}', 1, '1002'),
('SKU2006002', 'Air Jordan 1 Retro - Chicago US 9', 180000, 1000, 'https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80', '["https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80"]', 'SPU006', 321, 'Sneakers', 6, 'Nike', '{"colorway":"Chicago","size":"US 9","style":"High Top"}', 1, '1002'),
('SKU2006003', 'Air Jordan 1 Retro - Chicago US 10', 180000, 1000, 'https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80', '["https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80"]', 'SPU006', 321, 'Sneakers', 6, 'Nike', '{"colorway":"Chicago","size":"US 10","style":"High Top"}', 1, '1002'),
('SKU2006004', 'Air Jordan 1 Retro - Chicago US 11', 185000, 1000, 'https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80', '["https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80"]', 'SPU006', 321, 'Sneakers', 6, 'Nike', '{"colorway":"Chicago","size":"US 11","style":"High Top"}', 1, '1002'),
('SKU2006005', 'Air Jordan 1 Retro - Bred Toe US 8.5', 190000, 1000, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&q=80', '["https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&q=80"]', 'SPU006', 321, 'Sneakers', 6, 'Nike', '{"colorway":"Bred Toe","size":"US 8.5","style":"High Top"}', 1, '1002'),
('SKU2006006', 'Air Jordan 1 Retro - Bred Toe US 9.5', 190000, 1000, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&q=80', '["https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&q=80"]', 'SPU006', 321, 'Sneakers', 6, 'Nike', '{"colorway":"Bred Toe","size":"US 9.5","style":"High Top"}', 1, '1002'),
('SKU2006007', 'Air Jordan 1 Retro - Royal Blue US 9', 175000, 1000, 'https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80', '["https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80"]', 'SPU006', 321, 'Sneakers', 6, 'Nike', '{"colorway":"Royal Blue","size":"US 9","style":"High Top"}', 1, '1002'),
('SKU2006008', 'Air Jordan 1 Retro - Trophy Room US 10', 350000, 1000, 'https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80', '["https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80"]', 'SPU006', 321, 'Sneakers', 6, 'Nike', '{"colorway":"Trophy Room","size":"US 10","style":"High Top","edition":"Limited"}', 1, '1002'),
('SKU2006009', 'Air Jordan 1 Retro - Dior Collab US 9', 850000, 1000, 'https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80', '["https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80"]', 'SPU006', 321, 'Sneakers', 6, 'Nike', '{"colorway":"Dior Collab","size":"US 9","style":"High Top","edition":"Ultra Limited"}', 1, '1002');

-- SPU007: Adidas Ultraboost 23 - 9 SKU variants
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
('SKU2007001', 'Adidas Ultraboost 23 - Core Black US 7', 180000, 1000, 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80', '["https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80","https://images.unsplash.com/photo-1608231387042-66d1773070de?w=800&q=80"]', 'SPU007', 321, 'Running Shoes', 7, 'Adidas', '{"color":"Core Black","size":"US 7","technology":"Boost"}', 1, '1002'),
('SKU2007002', 'Adidas Ultraboost 23 - Core Black US 8', 180000, 1000, 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80', '["https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80"]', 'SPU007', 321, 'Running Shoes', 7, 'Adidas', '{"color":"Core Black","size":"US 8","technology":"Boost"}', 1, '1002'),
('SKU2007003', 'Adidas Ultraboost 23 - Core Black US 9', 180000, 1000, 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80', '["https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80"]', 'SPU007', 321, 'Running Shoes', 7, 'Adidas', '{"color":"Core Black","size":"US 9","technology":"Boost"}', 1, '1002'),
('SKU2007004', 'Adidas Ultraboost 23 - Core Black US 10', 180000, 1000, 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80', '["https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80"]', 'SPU007', 321, 'Running Shoes', 7, 'Adidas', '{"color":"Core Black","size":"US 10","technology":"Boost"}', 1, '1002'),
('SKU2007005', 'Adidas Ultraboost 23 - Triple White US 8', 180000, 1000, 'https://images.unsplash.com/photo-1608231387042-66d1773070de?w=800&q=80', '["https://images.unsplash.com/photo-1608231387042-66d1773070de?w=800&q=80"]', 'SPU007', 321, 'Running Shoes', 7, 'Adidas', '{"color":"Triple White","size":"US 8","technology":"Boost"}', 1, '1002'),
('SKU2007006', 'Adidas Ultraboost 23 - Triple White US 9', 180000, 1000, 'https://images.unsplash.com/photo-1608231387042-66d1773070de?w=800&q=80', '["https://images.unsplash.com/photo-1608231387042-66d1773070de?w=800&q=80"]', 'SPU007', 321, 'Running Shoes', 7, 'Adidas', '{"color":"Triple White","size":"US 9","technology":"Boost"}', 1, '1002'),
('SKU2007007', 'Adidas Ultraboost 23 - Solar Red US 9', 190000, 1000, 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80', '["https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80"]', 'SPU007', 321, 'Running Shoes', 7, 'Adidas', '{"color":"Solar Red","size":"US 9","technology":"Boost"}', 1, '1002'),
('SKU2007008', 'Adidas Ultraboost 23 - Solar Red US 10', 190000, 1000, 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80', '["https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80"]', 'SPU007', 321, 'Running Shoes', 7, 'Adidas', '{"color":"Solar Red","size":"US 10","technology":"Boost"}', 1, '1002'),
('SKU2007009', 'Adidas Ultraboost 23 - DNA Premium US 9.5', 220000, 1000, 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80', '["https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80"]', 'SPU007', 321, 'Running Shoes', 7, 'Adidas', '{"color":"Multi Color","size":"US 9.5","technology":"Boost 5.0","edition":"Premium"}', 1, '1002');

-- SPU008: Zara Oversized Hoodie - 12 SKU variants
INSERT INTO sku (id, name, price, num, image, images, spu_id, category_id, category_name, brand_id, brand_name, sku_attribute, status, tenant_id) VALUES
('SKU2008001', 'Zara Oversized Hoodie - Black XS', 49900, 1000, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80","https://images.unsplash.com/photo-1578768079052-aa76e52ff62e?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Black","size":"XS","material":"Cotton Blend"}', 1, '1002'),
('SKU2008002', 'Zara Oversized Hoodie - Black S', 49900, 1000, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Black","size":"S","material":"Cotton Blend"}', 1, '1002'),
('SKU2008003', 'Zara Oversized Hoodie - Black M', 49900, 1000, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Black","size":"M","material":"Cotton Blend"}', 1, '1002'),
('SKU2008004', 'Zara Oversized Hoodie - Black L', 49900, 1000, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Black","size":"L","material":"Cotton Blend"}', 1, '1002'),
('SKU2008005', 'Zara Oversized Hoodie - Black XL', 49900, 1000, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Black","size":"XL","material":"Cotton Blend"}', 1, '1002'),
('SKU2008006', 'Zara Oversized Hoodie - Grey M', 49900, 1000, 'https://images.unsplash.com/photo-1578768079052-aa76e52ff62e?w=800&q=80', '["https://images.unsplash.com/photo-1578768079052-aa76e52ff62e?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Grey","size":"M","material":"Cotton Blend"}', 1, '1002'),
('SKU2008007', 'Zara Oversized Hoodie - Grey L', 49900, 1000, 'https://images.unsplash.com/photo-1578768079052-aa76e52ff62e?w=800&q=80', '["https://images.unsplash.com/photo-1578768079052-aa76e52ff62e?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Grey","size":"L","material":"Cotton Blend"}', 1, '1002'),
('SKU2008008', 'Zara Oversized Hoodie - Beige S', 52900, 1000, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Beige","size":"S","material":"Cotton Blend"}', 1, '1002'),
('SKU2008009', 'Zara Oversized Hoodie - Beige M', 52900, 1000, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Beige","size":"M","material":"Cotton Blend"}', 1, '1002'),
('SKU2008010', 'Zara Oversized Hoodie - Beige L', 52900, 1000, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Beige","size":"L","material":"Cotton Blend"}', 1, '1002'),
('SKU2008011', 'Zara Oversized Hoodie - Navy M', 54900, 1000, 'https://images.unsplash.com/photo-1578768079052-aa76e52ff62e?w=800&q=80', '["https://images.unsplash.com/photo-1578768079052-aa76e52ff62e?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Navy","size":"M","material":"Cotton Blend"}', 1, '1002'),
('SKU2008012', 'Zara Oversized Hoodie - Limited Edition Black Embroidered XL', 79900, 1000, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80"]', 'SPU008', 331, 'Streetwear', 8, 'Zara', '{"color":"Black","size":"XL","material":"Premium Cotton","feature":"Embroidered Logo"}', 1, '1002');

-- ========================================================
-- 4. Data Summary
-- ========================================================
-- Tenant 1001 (3C Digital): 29 SKUs total
--   - SPU001 (iPhone 15 Pro Max): 9 SKUs
--   - SPU002 (MacBook Pro 16 M3): 8 SKUs
--   - SPU003 (Sony WH-1000XM5): 6 SKUs
--   - SPU004 (Logitech MX Mechanical): 6 SKUs
--
-- Tenant 1002 (Beauty & Fashion): 39 SKUs total
--   - SPU005 (Estée Lauder ANR): 9 SKUs
--   - SPU006 (Air Jordan 1 Retro): 9 SKUs
--   - SPU007 (Adidas Ultraboost 23): 9 SKUs
--   - SPU008 (Zara Oversized Hoodie): 12 SKUs
--
-- Total: 68 SKUs with 1000 units stock each (68,000 total baseline inventory)
-- All SKUs ready for millisecond-level concurrent stock deduction testing
-- ========================================================
-- SKU Test Data Insertion Complete
-- ========================================================
