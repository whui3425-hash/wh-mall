-- ========================================================-- mall_goods Mock Data with Local Image Paths-- ========================================================
USE mall_goods;

-- ========================================================-- 1. Brand Data - Tenant 1001 (3C Digital Tech)-- ========================================================
INSERT INTO brand (id, name, image, initial, sort, tenant_id) VALUES
(1, 'Apple', '/images/goods/brand-apple.jpg', 'A', 1, '1001'),
(2, 'Sony', '/images/goods/brand-sony.jpg', 'S', 2, '1001'),
(3, 'Logitech', '/images/goods/brand-logitech.jpg', 'L', 3, '1001'),
(4, 'DJI', '/images/goods/brand-dji.jpg', 'D', 4, '1001'),
(9, 'Samsung', '/images/goods/brand-samsung.jpg', 'S', 5, '1001');

-- ========================================================-- 2. Brand Data - Tenant 1002 (Beauty & Fashion)-- ========================================================
INSERT INTO brand (id, name, image, initial, sort, tenant_id) VALUES
(5, 'Estée Lauder', '/images/goods/brand-estee-lauder.jpg', 'E', 1, '1002'),
(6, 'Nike', '/images/goods/brand-nike.jpg', 'N', 2, '1002'),
(7, 'Adidas', '/images/goods/brand-adidas.jpg', 'A', 3, '1002'),
(8, 'Zara', '/images/goods/brand-zara.jpg', 'Z', 4, '1002');

-- ========================================================-- 3. SPU Data - Tenant 1001 (3C Digital Products)-- ========================================================
INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU001', 'iPhone 15 Pro Max', 'Apple flagship with A17 Pro chip, titanium design', 1, 1, 11, 111,
    '["/images/goods/spu001-1.jpg","/images/goods/spu001-2.jpg"]',
    '1 year official warranty, 7-day no-reason return',
    '<h2>iPhone 15 Pro Max</h2><p>The most powerful iPhone ever with A17 Pro chip.</p><img src="/images/goods/spu001-1.jpg">',
    '[{"name":"Color","options":["Natural Titanium","Blue Titanium","Black Titanium"]},{"name":"Storage","options":["256GB","512GB","1TB"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU002', 'MacBook Pro 16 M3', 'Professional laptop with M3 Max chip', 1, 2, 21, 211,
    '["/images/goods/spu002-1.jpg","/images/goods/spu002-2.jpg"]',
    '1 year official warranty, AppleCare+ available',
    '<h2>MacBook Pro 16</h2><p>Supercharged by M3 Max chip for professionals.</p><img src="/images/goods/spu002-1.jpg">',
    '[{"name":"Color","options":["Space Black","Silver"]},{"name":"Memory","options":["36GB","48GB","64GB"]},{"name":"Storage","options":["512GB","1TB","2TB"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU003', 'Sony WH-1000XM5', 'Industry-leading noise canceling headphones', 2, 1, 12, 121,
    '["/images/goods/spu003-1.jpg","/images/goods/spu003-2.jpg"]',
    '1 year warranty, 30-day return policy',
    '<h2>Sony WH-1000XM5</h2><p>Best noise canceling with 30-hour battery life.</p><img src="/images/goods/spu003-1.jpg">',
    '[{"name":"Color","options":["Black","Silver"]},{"name":"Feature","options":["Active Noise Canceling","Ambient Sound"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU004', 'Logitech MX Mechanical', 'Wireless mechanical keyboard for professionals', 3, 2, 22, 221,
    '["/images/goods/spu004-1.jpg","/images/goods/spu004-2.jpg"]',
    '2 year warranty, business day support',
    '<h2>MX Mechanical</h2><p>Smart illumination and multi-device connectivity.</p><img src="/images/goods/spu004-1.jpg">',
    '[{"name":"Switch Type","options":["Tactile","Linear","Clicky"]},{"name":"Layout","options":["Full Size","Mini"]}]',
    1, 0, 1);

-- ========================================================-- 4. SPU Data - Tenant 1002 (Beauty & Fashion)-- ========================================================
INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU005', 'Advanced Night Repair', 'Estée Lauder iconic serum for radiant skin', 5, 3, 31, 311,
    '["/images/goods/spu005-1.jpg","/images/goods/spu005-2.jpg"]',
    'Authentic guarantee, 30-day return if unopened',
    '<h2>Advanced Night Repair</h2><p>Reduces signs of aging, hydrates deeply.</p><img src="/images/goods/spu005-1.jpg">',
    '[{"name":"Size","options":["30ml","50ml","75ml"]},{"name":"Skin Type","options":["All Skin Types","Dry","Oily"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU006', 'Air Jordan 1 Retro', 'Classic basketball shoe, streetwear icon', 6, 3, 32, 321,
    '["/images/goods/spu006-1.jpg","/images/goods/spu006-2.jpg"]',
    'Authentic guarantee, size exchange within 7 days',
    '<h2>Air Jordan 1 Retro</h2><p>Legendary design that changed basketball forever.</p><img src="/images/goods/spu006-1.jpg">',
    '[{"name":"Colorway","options":["Chicago","Bred Toe","Royal Blue"]},{"name":"Size","options":["US 7-13"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU007', 'Adidas Ultraboost 23', 'Premium running shoes with Boost technology', 7, 3, 32, 321,
    '["/images/goods/spu007-1.jpg","/images/goods/spu007-2.jpg"]',
    '30-day return, free shipping over $100',
    '<h2>Ultraboost 23</h2><p>Energy return with every step, superior comfort.</p><img src="/images/goods/spu007-1.jpg">',
    '[{"name":"Color","options":["Core Black","Triple White","Solar Red"]},{"name":"Size","options":["US 6-14"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU008', 'Zara Oversized Hoodie', 'Trendy streetwear hoodie for everyday style', 8, 3, 33, 331,
    '["/images/goods/spu008-1.jpg","/images/goods/spu008-2.jpg"]',
    '30-day return, easy size exchange',
    '<h2>Oversized Hoodie</h2><p>Comfortable cotton blend with trendy oversized fit.</p><img src="/images/goods/spu008-1.jpg">',
    '[{"name":"Color","options":["Black","Grey","Beige","Navy"]},{"name":"Size","options":["XS","S","M","L","XL"]}]',
    1, 0, 1);

-- ========================================================-- Mock Data Inserted Successfully-- ========================================================
