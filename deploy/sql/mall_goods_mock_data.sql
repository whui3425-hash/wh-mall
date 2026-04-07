-- ========================================================-- mall_goods Mock Data with Real Image URLs-- ========================================================
USE mall_goods;

-- ========================================================-- 1. Brand Data - Tenant 1001 (3C Digital Tech)-- ========================================================
INSERT INTO brand (id, name, image, initial, sort, tenant_id) VALUES
(1, 'Apple', 'https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=500&q=80', 'A', 1, '1001'),
(2, 'Sony', 'https://images.unsplash.com/photo-1606567595334-069f7c8d47a2?w=500&q=80', 'S', 2, '1001'),
(3, 'Logitech', 'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=500&q=80', 'L', 3, '1001'),
(4, 'DJI', 'https://images.unsplash.com/photo-1569263979104-f2c500e151e1?w=500&q=80', 'D', 4, '1001');

-- ========================================================-- 2. Brand Data - Tenant 1002 (Beauty & Fashion)-- ========================================================
INSERT INTO brand (id, name, image, initial, sort, tenant_id) VALUES
(5, 'Estée Lauder', 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500&q=80', 'E', 1, '1002'),
(6, 'Nike', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&q=80', 'N', 2, '1002'),
(7, 'Adidas', 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=500&q=80', 'A', 3, '1002'),
(8, 'Zara', 'https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=500&q=80', 'Z', 4, '1002');

-- ========================================================-- 3. SPU Data - Tenant 1001 (3C Digital Products)-- ========================================================
INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU001', 'iPhone 15 Pro Max', 'Apple flagship with A17 Pro chip, titanium design', 1, 1, 11, 111,
    '["https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80"]',
    '1 year official warranty, 7-day no-reason return',
    '<h2>iPhone 15 Pro Max</h2><p>The most powerful iPhone ever with A17 Pro chip.</p><img src="https://images.unsplash.com/photo-1696446702183-cbd13c57e3d7?w=800&q=80">',
    '[{"name":"Color","options":["Natural Titanium","Blue Titanium","Black Titanium"]},{"name":"Storage","options":["256GB","512GB","1TB"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU002', 'MacBook Pro 16 M3', 'Professional laptop with M3 Max chip', 1, 2, 21, 211,
    '["https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80","https://images.unsplash.com/photo-1496181133206-80ce9f88c853?w=800&q=80"]',
    '1 year official warranty, AppleCare+ available',
    '<h2>MacBook Pro 16</h2><p>Supercharged by M3 Max chip for professionals.</p><img src="https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80">',
    '[{"name":"Color","options":["Space Black","Silver"]},{"name":"Memory","options":["36GB","48GB","64GB"]},{"name":"Storage","options":["512GB","1TB","2TB"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU003', 'Sony WH-1000XM5', 'Industry-leading noise canceling headphones', 2, 1, 12, 121,
    '["https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80","https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&q=80"]',
    '1 year warranty, 30-day return policy',
    '<h2>Sony WH-1000XM5</h2><p>Best noise canceling with 30-hour battery life.</p><img src="https://images.unsplash.com/photo-1618366712010-b4bee968d599?w=800&q=80">',
    '[{"name":"Color","options":["Black","Silver"]},{"name":"Feature","options":["Active Noise Canceling","Ambient Sound"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU004', 'Logitech MX Mechanical', 'Wireless mechanical keyboard for professionals', 3, 2, 22, 221,
    '["https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80","https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&q=80"]',
    '2 year warranty, business day support',
    '<h2>MX Mechanical</h2><p>Smart illumination and multi-device connectivity.</p><img src="https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80">',
    '[{"name":"Switch Type","options":["Tactile","Linear","Clicky"]},{"name":"Layout","options":["Full Size","Mini"]}]',
    1, 0, 1);

-- ========================================================-- 4. SPU Data - Tenant 1002 (Beauty & Fashion)-- ========================================================
INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU005', 'Advanced Night Repair', 'Estée Lauder iconic serum for radiant skin', 5, 3, 31, 311,
    '["https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80","https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=800&q=80"]',
    'Authentic guarantee, 30-day return if unopened',
    '<h2>Advanced Night Repair</h2><p>Reduces signs of aging, hydrates deeply.</p><img src="https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80">',
    '[{"name":"Size","options":["30ml","50ml","75ml"]},{"name":"Skin Type","options":["All Skin Types","Dry","Oily"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU006', 'Air Jordan 1 Retro', 'Classic basketball shoe, streetwear icon', 6, 3, 32, 321,
    '["https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80","https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&q=80"]',
    'Authentic guarantee, size exchange within 7 days',
    '<h2>Air Jordan 1 Retro</h2><p>Legendary design that changed basketball forever.</p><img src="https://images.unsplash.com/photo-1549298916-b41d2f5d7f0f?w=800&q=80">',
    '[{"name":"Colorway","options":["Chicago","Bred Toe","Royal Blue"]},{"name":"Size","options":["US 7-13"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU007', 'Adidas Ultraboost 23', 'Premium running shoes with Boost technology', 7, 3, 32, 321,
    '["https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80","https://images.unsplash.com/photo-1608231387042-66d1773070de?w=800&q=80"]',
    '30-day return, free shipping over $100',
    '<h2>Ultraboost 23</h2><p>Energy return with every step, superior comfort.</p><img src="https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&q=80">',
    '[{"name":"Color","options":["Core Black","Triple White","Solar Red"]},{"name":"Size","options":["US 6-14"]}]',
    1, 0, 1);

INSERT INTO spu (
    id, name, intro, brand_id, category_one_id, category_two_id, category_three_id,
    images, after_sales_service, content, attribute_list, is_marketable, is_delete, status
) VALUES
('SPU008', 'Zara Oversized Hoodie', 'Trendy streetwear hoodie for everyday style', 8, 3, 33, 331,
    '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80","https://images.unsplash.com/photo-1578768079052-aa76e52ff62e?w=800&q=80"]',
    '30-day return, easy size exchange',
    '<h2>Oversized Hoodie</h2><p>Comfortable cotton blend with trendy oversized fit.</p><img src="https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800&q=80">',
    '[{"name":"Color","options":["Black","Grey","Beige","Navy"]},{"name":"Size","options":["XS","S","M","L","XL"]}]',
    1, 0, 1);

-- ========================================================-- Mock Data Inserted Successfully-- ========================================================
