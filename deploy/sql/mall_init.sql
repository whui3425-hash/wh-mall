/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.31.102
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : 192.168.31.102:3306
 Source Schema         : mall_goods

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 12/04/2026 09:28:45
*/

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS mall_goods CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE mall_goods;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ad_items
-- ----------------------------
DROP TABLE IF EXISTS `ad_items`;
CREATE TABLE `ad_items`  (
                             `id` int NOT NULL AUTO_INCREMENT COMMENT 'Ad ID',
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Ad name',
                             `type` int NULL DEFAULT 1 COMMENT 'Type: 1-index banner, 2-category banner',
                             `sku_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SKU ID',
                             `sort` int NULL DEFAULT 0 COMMENT 'Sort order',
                             `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT 'Tenant ID',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
                             INDEX `idx_sku_id`(`sku_id` ASC) USING BTREE,
                             INDEX `idx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Ad items table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ad_items
-- ----------------------------
INSERT INTO `ad_items` VALUES (1, 'iPhone 15 Pro Max - Featured', 1, 'SKU001', 1, '1001');
INSERT INTO `ad_items` VALUES (2, 'Sony WH-1000XM5 - Hot Sale', 1, 'SKU006', 2, '1001');
INSERT INTO `ad_items` VALUES (3, 'Air Jordan 1 Retro - Trending', 2, 'SKU012', 1, '1002');
INSERT INTO `ad_items` VALUES (4, 'Advanced Night Repair - Best Seller', 2, 'SKU010', 2, '1002');
INSERT INTO `ad_items` VALUES (5, 'Huawei Mate 60 Pro - New Arrival', 1, 'SKU018', 3, '1001');
INSERT INTO `ad_items` VALUES (6, 'MacBook Pro 16 - Professional', 1, 'SKU004', 4, '1001');

-- ----------------------------
-- Table structure for brand
-- ----------------------------
DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand`  (
                          `id` int NOT NULL AUTO_INCREMENT COMMENT 'Brand ID',
                          `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Brand name',
                          `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Brand logo URL',
                          `initial` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Brand initial letter',
                          `sort` int NULL DEFAULT 0 COMMENT 'Sort order',
                          `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT 'Tenant ID',
                          PRIMARY KEY (`id`) USING BTREE,
                          INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Brand table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of brand
-- ----------------------------
INSERT INTO `brand` VALUES (1, 'Apple', '/images/goods/brand-apple.jpg', 'A', 1, '1001');
INSERT INTO `brand` VALUES (2, 'Sony', '/images/goods/brand-sony.jpg', 'S', 2, '1001');
INSERT INTO `brand` VALUES (3, 'Logitech', '/images/goods/brand-logitech.jpg', 'L', 3, '1001');
INSERT INTO `brand` VALUES (4, 'DJI', '/images/goods/brand-dji.jpg', 'D', 4, '1001');
INSERT INTO `brand` VALUES (5, 'Estée Lauder', '/images/goods/brand-estee-lauder.jpg', 'E', 1, '1002');
INSERT INTO `brand` VALUES (6, 'Nike', '/images/goods/brand-nike.jpg', 'N', 2, '1002');
INSERT INTO `brand` VALUES (7, 'Adidas', '/images/goods/brand-adidas.jpg', 'A', 3, '1002');
INSERT INTO `brand` VALUES (8, 'Zara', '/images/goods/brand-zara.jpg', 'Z', 4, '1002');
INSERT INTO `brand` VALUES (9, 'Samsung', '/images/goods/brand-samsung.jpg', 'S', 5, '1001');
INSERT INTO `brand` VALUES (10, 'Huawei', '/images/goods/brand-huawei.jpg', 'H', 6, '1001');
INSERT INTO `brand` VALUES (11, 'Xiaomi', '/images/goods/brand-xiaomi.jpg', 'X', 7, '1001');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
                             `id` int NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Category name',
                             `sort` int NULL DEFAULT 0 COMMENT 'Sort order',
                             `parent_id` int NULL DEFAULT 0 COMMENT 'Parent category ID (0 for root)',
                             `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT 'Tenant ID',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
                             INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 172 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Product category table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 'Phone & Accessories', 1, 0, '1001');
INSERT INTO `category` VALUES (2, 'Computer & Office', 2, 0, '1001');
INSERT INTO `category` VALUES (3, 'Beauty & Fashion', 3, 0, '1001');
INSERT INTO `category` VALUES (11, 'Mobile Phone', 1, 1, '1001');
INSERT INTO `category` VALUES (12, 'Phone Accessories', 2, 1, '1001');
INSERT INTO `category` VALUES (13, 'Laptop', 1, 2, '1001');
INSERT INTO `category` VALUES (14, 'Keyboard & Mouse', 2, 2, '1001');
INSERT INTO `category` VALUES (15, 'Skincare', 1, 3, '1001');
INSERT INTO `category` VALUES (16, 'Shoes', 2, 3, '1001');
INSERT INTO `category` VALUES (17, 'Clothing', 3, 3, '1001');
INSERT INTO `category` VALUES (111, 'Smartphone', 1, 11, '1001');
INSERT INTO `category` VALUES (112, 'Feature Phone', 2, 11, '1001');
INSERT INTO `category` VALUES (121, 'Headphones', 1, 12, '1001');
INSERT INTO `category` VALUES (131, 'Business Laptop', 1, 13, '1001');
INSERT INTO `category` VALUES (141, 'Mechanical Keyboard', 1, 14, '1001');
INSERT INTO `category` VALUES (151, 'Face Care', 1, 15, '1001');
INSERT INTO `category` VALUES (161, 'Sports Shoes', 1, 16, '1001');
INSERT INTO `category` VALUES (171, 'Casual Wear', 1, 17, '1001');

-- ----------------------------
-- Table structure for category_attr
-- ----------------------------
DROP TABLE IF EXISTS `category_attr`;
CREATE TABLE `category_attr`  (
                                  `category_id` int NOT NULL COMMENT 'Category ID',
                                  `attr_id` int NOT NULL COMMENT 'Attribute ID',
                                  PRIMARY KEY (`category_id`, `attr_id`) USING BTREE,
                                  INDEX `idx_attr_id`(`attr_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Category-Attribute relationship' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category_attr
-- ----------------------------
INSERT INTO `category_attr` VALUES (111, 1);
INSERT INTO `category_attr` VALUES (111, 2);
INSERT INTO `category_attr` VALUES (111, 3);

-- ----------------------------
-- Table structure for category_brand
-- ----------------------------
DROP TABLE IF EXISTS `category_brand`;
CREATE TABLE `category_brand`  (
                                   `category_id` int NOT NULL COMMENT 'Category ID',
                                   `brand_id` int NOT NULL COMMENT 'Brand ID',
                                   PRIMARY KEY (`category_id`, `brand_id`) USING BTREE,
                                   INDEX `idx_brand_id`(`brand_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Category-Brand relationship' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category_brand
-- ----------------------------
INSERT INTO `category_brand` VALUES (111, 1);
INSERT INTO `category_brand` VALUES (131, 1);
INSERT INTO `category_brand` VALUES (111, 2);
INSERT INTO `category_brand` VALUES (121, 2);
INSERT INTO `category_brand` VALUES (141, 3);
INSERT INTO `category_brand` VALUES (151, 5);
INSERT INTO `category_brand` VALUES (161, 6);
INSERT INTO `category_brand` VALUES (161, 7);
INSERT INTO `category_brand` VALUES (171, 8);
INSERT INTO `category_brand` VALUES (111, 10);
INSERT INTO `category_brand` VALUES (111, 11);

-- ----------------------------
-- Table structure for mall_cart
-- ----------------------------
DROP TABLE IF EXISTS `mall_cart`;
CREATE TABLE `mall_cart`  (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车记录ID（主键）',
                              `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '用户ID（从JWT Header X-User-Id获取）',
                              `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
                              `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称（通过Feign从SKU同步）',
                              `price` int NOT NULL COMMENT '商品价格（单位：分，通过Feign从SKU同步）',
                              `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片URL（通过Feign从SKU同步）',
                              `sku_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SKU ID（关联商品）',
                              `num` int NOT NULL DEFAULT 1 COMMENT '购买数量（累加模式）',
                              `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT '租户ID（多租户隔离）',
                              `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `uk_user_sku`(`user_id` ASC, `sku_id` ASC, `tenant_id` ASC) USING BTREE COMMENT '同一用户同一租户下同一SKU只能有一条记录',
                              INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                              INDEX `idx_sku_id`(`sku_id` ASC) USING BTREE,
                              INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表（支持多租户、用户隔离）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_cart
-- ----------------------------

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info`  (
                               `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单ID',
                               `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '买家用户ID，C端查询订单时使用',
                               `pay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'weixin' COMMENT '支付方式',
                               `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                               `consign_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
                               `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
                               `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
                               `recipients` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人',
                               `recipients_mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人手机号',
                               `recipients_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货地址',
                               `weixin_transaction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信支付流水号',
                               `total_num` int NULL DEFAULT 0 COMMENT '商品总数量',
                               `moneys` int NULL DEFAULT 0 COMMENT '订单总金额（分）',
                               `order_status` int NULL DEFAULT 0 COMMENT '订单状态：0-未支付，1-已支付待发货，2-已发货，3-已完成，4-退款中，5-已退款',
                               `pay_status` int NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付',
                               `is_delete` int NULL DEFAULT 0 COMMENT '是否删除：0-正常，1-已删除',
                               `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT '租户ID',
                               `out_trade_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外部交易流水号，用于支付',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `idx_username`(`username` ASC) USING BTREE,
                               INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
                               INDEX `idx_out_trade_no`(`out_trade_no` ASC) USING BTREE,
                               INDEX `idx_order_status`(`order_status` ASC) USING BTREE,
                               INDEX `idx_user_tenant`(`user_id` ASC, `tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('2042258717205950466', NULL, 'weixin', '2026-04-09 23:09:56', '2026-04-09 23:09:56', NULL, NULL, NULL, 'zhangsan', NULL, NULL, NULL, NULL, 2, 2199800, 0, 0, 0, '1001', NULL);
INSERT INTO `order_info` VALUES ('2042378804600512513', NULL, 'weixin', '2026-04-10 07:07:07', '2026-04-10 07:07:07', NULL, NULL, NULL, 'zhangsan', NULL, NULL, NULL, NULL, 1, 999900, 0, 0, 0, '1001', NULL);
INSERT INTO `order_info` VALUES ('2042380399371366402', NULL, 'weixin', '2026-04-10 07:13:28', '2026-04-09 23:13:32', NULL, NULL, NULL, 'zhangsan', NULL, NULL, NULL, NULL, 1, 1199900, 1, 1, 0, '1001', 'ORDC9F9FA9E302A4B10');
INSERT INTO `order_info` VALUES ('2042382769576841218', '1', 'weixin', '2026-04-10 07:22:53', '2026-04-09 23:22:53', NULL, NULL, NULL, 'zhangsan', NULL, NULL, NULL, NULL, 1, 1399900, 1, 1, 0, '1001', 'ORD5BFEB59228414F25');
INSERT INTO `order_info` VALUES ('2042384730581651457', '1', 'weixin', '2026-04-10 07:30:40', '2026-04-09 23:30:41', NULL, NULL, NULL, 'zhangsan', NULL, NULL, NULL, NULL, 1, 129900, 1, 1, 0, '1001', 'ORD396A22CE5D704D8B');

-- ----------------------------
-- Table structure for order_refund
-- ----------------------------
DROP TABLE IF EXISTS `order_refund`;
CREATE TABLE `order_refund`  (
                                 `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Refund ID (Snowflake ID)',
                                 `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Order number',
                                 `refund_type` tinyint NULL DEFAULT 1 COMMENT 'Refund type: 1-full, 2-partial',
                                 `order_sku_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Order SKU ID (for partial refund)',
                                 `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Username',
                                 `status` tinyint NULL DEFAULT 0 COMMENT 'Refund status: 0-pending, 1-approved, 2-rejected, 3-completed',
                                 `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
                                 `money` int NULL DEFAULT 0 COMMENT 'Refund amount (in cents)',
                                 `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT 'Tenant ID',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
                                 INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
                                 INDEX `idx_username`(`username` ASC) USING BTREE,
                                 INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Order refund table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_refund
-- ----------------------------
INSERT INTO `order_refund` VALUES ('REFUND001', 'ORDER001', 2, 'ORDERSKU002', 'zhangsan', 0, '2026-04-09 13:54:01', 349900, '1001');
INSERT INTO `order_refund` VALUES ('REFUND002', 'ORDER002', 1, NULL, 'lisi', 1, '2026-04-09 13:54:01', 159900, '1001');
INSERT INTO `order_refund` VALUES ('REFUND003', 'ORDER003', 1, NULL, 'wangwu', 3, '2026-04-09 13:54:01', 89900, '1001');

-- ----------------------------
-- Table structure for order_sku
-- ----------------------------
DROP TABLE IF EXISTS `order_sku`;
CREATE TABLE `order_sku`  (
                              `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单SKU记录ID',
                              `order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联订单ID',
                              `sku_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SKU ID',
                              `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
                              `price` int NOT NULL COMMENT '单价（分）',
                              `num` int NOT NULL COMMENT '数量',
                              `money` int NOT NULL COMMENT '小计金额（分）= price * num',
                              `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片URL',
                              `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT '租户ID',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
                              INDEX `idx_sku_id`(`sku_id` ASC) USING BTREE,
                              INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单SKU明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_sku
-- ----------------------------
INSERT INTO `order_sku` VALUES ('2042258717205950467', '2042258717205950466', 'SKU002', 'iPhone 15 Pro Max - Blue Titanium 512GB', 1199900, 1, 1199900, '/images/goods/sku-iphone-512.jpg', '1001');
INSERT INTO `order_sku` VALUES ('2042258717646352386', '2042258717205950466', 'SKU001', 'iPhone 15 Pro Max - Natural Titanium 256GB', 999900, 1, 999900, '/images/goods/sku-iphone-256.jpg', '1001');
INSERT INTO `order_sku` VALUES ('2042378804600512514', '2042378804600512513', 'SKU001', 'iPhone 15 Pro Max - Natural Titanium 256GB', 999900, 1, 999900, '/images/goods/sku-iphone-256.jpg', '1001');
INSERT INTO `order_sku` VALUES ('2042380399371366403', '2042380399371366402', 'SKU002', 'iPhone 15 Pro Max - Blue Titanium 512GB', 1199900, 1, 1199900, '/images/goods/sku-iphone-512.jpg', '1001');
INSERT INTO `order_sku` VALUES ('2042382769576841219', '2042382769576841218', 'SKU003', 'iPhone 15 Pro Max - Black Titanium 1TB', 1399900, 1, 1399900, '/images/goods/pic1.jpg', '1001');
INSERT INTO `order_sku` VALUES ('2042384730581651458', '2042384730581651457', 'SKU008', 'MX Mechanical - Tactile Full Size', 129900, 1, 129900, '/images/goods/spu004-1.jpg', '1001');

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
                               `id` int NOT NULL AUTO_INCREMENT COMMENT 'Permission ID',
                               `source_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Permission source name',
                               `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API URL pattern',
                               `url_match` int NULL DEFAULT 0 COMMENT 'URL match mode: 0-exact match, 1-wildcard match',
                               `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Service name',
                               `method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'HTTP method',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API permission table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1, 'Brand Query', '/goods/brand/**', 1, 'mall-goods-service', 'GET');
INSERT INTO `permission` VALUES (2, 'Brand Manage', '/goods/brand', 0, 'mall-goods-service', 'POST');
INSERT INTO `permission` VALUES (3, 'Brand Update', '/goods/brand', 0, 'mall-goods-service', 'PUT');
INSERT INTO `permission` VALUES (4, 'Brand Delete', '/goods/brand/**', 1, 'mall-goods-service', 'DELETE');
INSERT INTO `permission` VALUES (5, 'Category Query', '/goods/category/**', 1, 'mall-goods-service', 'GET');
INSERT INTO `permission` VALUES (6, 'SKU Query', '/goods/sku/**', 1, 'mall-goods-service', 'GET');
INSERT INTO `permission` VALUES (7, 'SPU Query', '/goods/spu/**', 1, 'mall-goods-service', 'GET');
INSERT INTO `permission` VALUES (11, 'Order Query', '/order/**', 1, 'mall-order-service', 'GET');
INSERT INTO `permission` VALUES (12, 'Order Create', '/order', 0, 'mall-order-service', 'POST');
INSERT INTO `permission` VALUES (13, 'Order Update', '/order', 0, 'mall-order-service', 'PUT');
INSERT INTO `permission` VALUES (14, 'Order Delete', '/order/**', 1, 'mall-order-service', 'DELETE');
INSERT INTO `permission` VALUES (21, 'User Query', '/user/**', 1, 'mall-user-service', 'GET');
INSERT INTO `permission` VALUES (22, 'User Manage', '/user', 0, 'mall-user-service', 'POST');
INSERT INTO `permission` VALUES (31, 'Cart Query', '/cart/**', 1, 'mall-cart-service', 'GET');
INSERT INTO `permission` VALUES (32, 'Cart Add', '/cart', 0, 'mall-cart-service', 'POST');
INSERT INTO `permission` VALUES (33, 'Cart Update', '/cart', 0, 'mall-cart-service', 'PUT');
INSERT INTO `permission` VALUES (34, 'Cart Delete', '/cart/**', 1, 'mall-cart-service', 'DELETE');
INSERT INTO `permission` VALUES (41, 'Pay Query', '/pay/**', 1, 'mall-pay-service', 'GET');
INSERT INTO `permission` VALUES (42, 'Pay Create', '/pay', 0, 'mall-pay-service', 'POST');
INSERT INTO `permission` VALUES (51, 'Admin Login', '/api/permission/admin/login', 0, 'mall-permission-service', 'POST');
INSERT INTO `permission` VALUES (52, 'User Login', '/api/user/login', 0, 'mall-user-service', 'POST');

-- ----------------------------
-- Table structure for role_info
-- ----------------------------
DROP TABLE IF EXISTS `role_info`;
CREATE TABLE `role_info`  (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT 'Role ID',
                              `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Role name',
                              `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Role description',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Role information table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_info
-- ----------------------------
INSERT INTO `role_info` VALUES (1, 'SUPER_ADMIN', 'Super administrator with all permissions');
INSERT INTO `role_info` VALUES (2, 'ADMIN', 'System administrator');
INSERT INTO `role_info` VALUES (3, 'OPERATOR', 'Business operator');
INSERT INTO `role_info` VALUES (4, 'GUEST', 'Guest user with limited permissions');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
                                    `rid` int NOT NULL COMMENT 'Role ID',
                                    `pid` int NOT NULL COMMENT 'Permission ID',
                                    PRIMARY KEY (`rid`, `pid`) USING BTREE,
                                    INDEX `idx_pid`(`pid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Role-Permission mapping table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1, 1);
INSERT INTO `role_permission` VALUES (1, 2);
INSERT INTO `role_permission` VALUES (1, 3);
INSERT INTO `role_permission` VALUES (1, 4);
INSERT INTO `role_permission` VALUES (1, 5);
INSERT INTO `role_permission` VALUES (1, 6);
INSERT INTO `role_permission` VALUES (1, 7);
INSERT INTO `role_permission` VALUES (1, 11);
INSERT INTO `role_permission` VALUES (1, 12);
INSERT INTO `role_permission` VALUES (1, 13);
INSERT INTO `role_permission` VALUES (1, 14);
INSERT INTO `role_permission` VALUES (1, 21);
INSERT INTO `role_permission` VALUES (1, 22);
INSERT INTO `role_permission` VALUES (1, 31);
INSERT INTO `role_permission` VALUES (1, 32);
INSERT INTO `role_permission` VALUES (1, 33);
INSERT INTO `role_permission` VALUES (1, 34);
INSERT INTO `role_permission` VALUES (1, 41);
INSERT INTO `role_permission` VALUES (1, 42);
INSERT INTO `role_permission` VALUES (1, 51);
INSERT INTO `role_permission` VALUES (1, 52);

-- ----------------------------
-- Table structure for sku
-- ----------------------------
DROP TABLE IF EXISTS `sku`;
CREATE TABLE `sku`  (
                        `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SKU ID (Snowflake ID)',
                        `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SKU name',
                        `price` int NOT NULL COMMENT 'Price (in cents)',
                        `num` int NULL DEFAULT 0 COMMENT 'Stock quantity',
                        `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Main image URL',
                        `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Image gallery (JSON array)',
                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
                        `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
                        `spu_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SPU ID',
                        `category_id` int NULL DEFAULT NULL COMMENT 'Category ID',
                        `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Category name',
                        `brand_id` int NULL DEFAULT NULL COMMENT 'Brand ID',
                        `brand_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Brand name',
                        `sku_attribute` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SKU attributes (JSON)',
                        `status` tinyint NULL DEFAULT 1 COMMENT 'Status: 1-normal, 2-offline, 3-deleted',
                        `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT 'Tenant ID',
                        PRIMARY KEY (`id`) USING BTREE,
                        INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
                        INDEX `idx_spu_id`(`spu_id` ASC) USING BTREE,
                        INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
                        INDEX `idx_brand_id`(`brand_id` ASC) USING BTREE,
                        INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'SKU table (Stock Keeping Unit)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sku
-- ----------------------------
INSERT INTO `sku` VALUES ('SKU001', 'iPhone 15 Pro Max - Natural Titanium 256GB', 999891, 95, '/images/goods/sku-iphone-256.jpg', '[\"/images/goods/spu001-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 23:07:06', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{\"color\": \"Natural Titanium\", \"storage\": \"256GB\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU002', 'iPhone 15 Pro Max - Blue Titanium 512GB', 1199900, 69, '/images/goods/sku-iphone-512.jpg', '[\"/images/goods/spu001-2.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 23:13:26', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{\"color\": \"Blue Titanium\", \"storage\": \"512GB\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU003', 'iPhone 15 Pro Max - Black Titanium 1TB', 1399900, 41, '/images/goods/pic1.jpg', '[\"/images/goods/spu001-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 23:22:51', 'SPU001', 111, 'Smartphone', 1, 'Apple', '{\"color\": \"Black Titanium\", \"storage\": \"1TB\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU004', 'MacBook Pro 16 - Space Black 36GB', 2499900, 29, '/images/goods/spu002-1.jpg', '[\"/images/goods/spu002-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 15:01:26', 'SPU002', 131, 'Laptop', 1, 'Apple', '{\"color\": \"Space Black\", \"memory\": \"36GB\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU005', 'MacBook Pro 16 - Silver 48GB', 2999900, 20, '/images/goods/spu002-2.jpg', '[\"/images/goods/spu002-2.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU002', 131, 'Laptop', 1, 'Apple', '{\"color\": \"Silver\", \"memory\": \"48GB\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU006', 'Sony WH-1000XM5 - Black', 349900, 150, '/images/goods/spu003-1.jpg', '[\"/images/goods/spu003-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU003', 121, 'Headphones', 2, 'Sony', '{\"color\": \"Black\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU007', 'Sony WH-1000XM5 - Silver', 349900, 120, '/images/goods/spu003-2.jpg', '[\"/images/goods/spu003-2.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU003', 121, 'Headphones', 2, 'Sony', '{\"color\": \"Silver\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU008', 'MX Mechanical - Tactile Full Size', 129900, 79, '/images/goods/spu004-1.jpg', '[\"/images/goods/spu004-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 23:30:39', 'SPU004', 141, 'Keyboard', 3, 'Logitech', '{\"switch\": \"Tactile\", \"layout\": \"Full Size\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU009', 'MX Mechanical - Linear Mini', 119900, 60, '/images/goods/spu004-2.jpg', '[\"/images/goods/spu004-2.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU004', 141, 'Keyboard', 3, 'Logitech', '{\"switch\": \"Linear\", \"layout\": \"Mini\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU010', 'Advanced Night Repair 50ml', 85000, 200, '/images/goods/spu005-1.jpg', '[\"/images/goods/spu005-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU005', 151, 'Skincare', 5, 'Estée Lauder', '{\"size\": \"50ml\"}', 1, '1002');
INSERT INTO `sku` VALUES ('SKU011', 'Advanced Night Repair 100ml', 120000, 150, '/images/goods/spu005-2.jpg', '[\"/images/goods/spu005-2.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU005', 151, 'Skincare', 5, 'Estée Lauder', '{\"size\": \"100ml\"}', 1, '1002');
INSERT INTO `sku` VALUES ('SKU012', 'Air Jordan 1 Retro - Chicago US 9', 159900, 50, '/images/goods/spu006-1.jpg', '[\"/images/goods/spu006-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU006', 161, 'Shoes', 6, 'Nike', '{\"colorway\": \"Chicago\", \"size\": \"US 9\"}', 1, '1002');
INSERT INTO `sku` VALUES ('SKU013', 'Air Jordan 1 Retro - Bred US 10', 169900, 40, '/images/goods/spu006-2.jpg', '[\"/images/goods/spu006-2.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU006', 161, 'Shoes', 6, 'Nike', '{\"colorway\": \"Bred\", \"size\": \"US 10\"}', 1, '1002');
INSERT INTO `sku` VALUES ('SKU014', 'Ultraboost 23 - Core Black US 10', 139900, 100, '/images/goods/spu007-1.jpg', '[\"/images/goods/spu007-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU007', 161, 'Shoes', 7, 'Adidas', '{\"color\": \"Core Black\", \"size\": \"US 10\"}', 1, '1002');
INSERT INTO `sku` VALUES ('SKU015', 'Ultraboost 23 - White US 9', 139900, 90, '/images/goods/spu007-2.jpg', '[\"/images/goods/spu007-2.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU007', 161, 'Shoes', 7, 'Adidas', '{\"color\": \"White\", \"size\": \"US 9\"}', 1, '1002');
INSERT INTO `sku` VALUES ('SKU016', 'Oversized Hoodie - Black M', 69900, 120, '/images/goods/spu008-1.jpg', '[\"/images/goods/spu008-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU008', 171, 'Clothing', 8, 'Zara', '{\"color\": \"Black\", \"size\": \"M\"}', 1, '1002');
INSERT INTO `sku` VALUES ('SKU017', 'Oversized Hoodie - Grey L', 69900, 100, '/images/goods/spu008-2.jpg', '[\"/images/goods/spu008-2.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU008', 171, 'Clothing', 8, 'Zara', '{\"color\": \"Grey\", \"size\": \"L\"}', 1, '1002');
INSERT INTO `sku` VALUES ('SKU018', 'Huawei Mate 60 Pro - Black 512GB', 699900, 80, '/images/goods/sku-huawei-512.jpg', '[\"/images/goods/iphone-huawei-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU009', 111, 'Smartphone', 10, 'Huawei', '{\"color\": \"Black\", \"storage\": \"512GB\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU019', 'Xiaomi 14 Pro - White 256GB', 499900, 100, '/images/goods/sku-xiaomi-256.jpg', '[\"/images/goods/iphone-xiaomi-1.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU010', 111, 'Smartphone', 11, 'Xiaomi', '{\"color\": \"White\", \"storage\": \"256GB\"}', 1, '1001');
INSERT INTO `sku` VALUES ('SKU020', 'Xiaomi 14 Pro - Black 512GB', 549900, 60, '/images/goods/pic2.jpg', '[\"/images/goods/iphone-xiaomi-2.jpg\"]', '2026-04-09 13:54:01', '2026-04-09 13:54:01', 'SPU010', 111, 'Smartphone', 11, 'Xiaomi', '{\"color\": \"Black\", \"storage\": \"512GB\"}', 1, '1001');

-- ----------------------------
-- Table structure for spu
-- ----------------------------
DROP TABLE IF EXISTS `spu`;
CREATE TABLE `spu`  (
                        `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SPU ID (Snowflake ID)',
                        `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Product name',
                        `intro` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Product introduction',
                        `brand_id` int NULL DEFAULT NULL COMMENT 'Brand ID',
                        `category_one_id` int NULL DEFAULT NULL COMMENT 'First-level category ID',
                        `category_two_id` int NULL DEFAULT NULL COMMENT 'Second-level category ID',
                        `category_three_id` int NULL DEFAULT NULL COMMENT 'Third-level category ID',
                        `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Product images (JSON array)',
                        `after_sales_service` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'After-sales service description',
                        `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Product detail content (HTML)',
                        `attribute_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Product attributes list (JSON)',
                        `is_marketable` tinyint NULL DEFAULT 1 COMMENT 'Is marketable: 1-yes, 0-no',
                        `is_delete` tinyint NULL DEFAULT 0 COMMENT 'Is deleted: 0-no, 1-yes',
                        `status` tinyint NULL DEFAULT 1 COMMENT 'Status: 1-approved, 0-pending',
                        `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT 'Tenant ID',
                        PRIMARY KEY (`id`) USING BTREE,
                        INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
                        INDEX `idx_brand_id`(`brand_id` ASC) USING BTREE,
                        INDEX `idx_category_three_id`(`category_three_id` ASC) USING BTREE,
                        INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'SPU table (Standard Product Unit)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of spu
-- ----------------------------
INSERT INTO `spu` VALUES ('SPU001', 'iPhone 15 Pro Max', 'Apple flagship smartphone with A17 Pro chip', 1, 1, 11, 111, '/images/goods/sku-iphone-256.jpg, \"/images/goods/spu001-2.jpg\"]', '7-day no-reason return, 1-year warranty', '<h1>iPhone 15 Pro Max</h1><p>The most advanced iPhone ever.</p>', '[{\"color\": \"Natural Titanium\", \"storage\": \"256GB\"}]', 1, 0, 1, '1001');
INSERT INTO `spu` VALUES ('SPU002', 'MacBook Pro 16 M3', 'Professional laptop with M3 Max chip', 1, 2, 13, 131, '[\"/images/goods/spu002-1.jpg\", \"/images/goods/spu002-2.jpg\"]', '1 year official warranty, AppleCare+ available', '<h2>MacBook Pro 16</h2><p>Supercharged by M3 Max chip for professionals.</p>', '[{\"color\": \"Space Black\", \"memory\": \"36GB\"}]', 1, 0, 1, '1001');
INSERT INTO `spu` VALUES ('SPU003', 'Sony WH-1000XM5', 'Industry-leading noise canceling headphones', 2, 1, 12, 121, '[\"/images/goods/spu003-1.jpg\", \"/images/goods/spu003-2.jpg\"]', '1 year warranty, 30-day return policy', '<h2>Sony WH-1000XM5</h2><p>Best noise canceling with 30-hour battery life.</p>', '[{\"color\": \"Black\", \"feature\": \"Active Noise Canceling\"}]', 1, 0, 1, '1001');
INSERT INTO `spu` VALUES ('SPU004', 'Logitech MX Mechanical', 'Wireless mechanical keyboard for professionals', 3, 2, 14, 141, '[\"/images/goods/spu004-1.jpg\", \"/images/goods/spu004-2.jpg\"]', '2 year warranty, business day support', '<h2>MX Mechanical</h2><p>Smart illumination and multi-device connectivity.</p>', '[{\"switch\": \"Tactile\", \"layout\": \"Full Size\"}]', 1, 0, 1, '1001');
INSERT INTO `spu` VALUES ('SPU005', 'Advanced Night Repair', 'Estée Lauder iconic serum for radiant skin', 5, 3, 15, 151, '[\"/images/goods/spu005-1.jpg\", \"/images/goods/spu005-2.jpg\"]', 'Authentic guarantee, 30-day return if unopened', '<h2>Advanced Night Repair</h2><p>Reduces signs of aging, hydrates deeply.</p>', '[{\"size\": \"50ml\", \"skinType\": \"All Skin Types\"}]', 1, 0, 1, '1002');
INSERT INTO `spu` VALUES ('SPU006', 'Air Jordan 1 Retro', 'Classic basketball shoe, streetwear icon', 6, 3, 16, 161, '[\"/images/goods/spu006-1.jpg\", \"/images/goods/spu006-2.jpg\"]', 'Authentic guarantee, size exchange within 7 days', '<h2>Air Jordan 1 Retro</h2><p>Legendary design that changed basketball forever.</p>', '[{\"colorway\": \"Chicago\", \"size\": \"US 9\"}]', 1, 0, 1, '1002');
INSERT INTO `spu` VALUES ('SPU007', 'Adidas Ultraboost 23', 'Premium running shoes with Boost technology', 7, 3, 16, 161, '[\"/images/goods/spu007-1.jpg\", \"/images/goods/spu007-2.jpg\"]', '30-day return, free shipping over $100', '<h2>Ultraboost 23</h2><p>Energy return with every step, superior comfort.</p>', '[{\"color\": \"Core Black\", \"size\": \"US 10\"}]', 1, 0, 1, '1002');
INSERT INTO `spu` VALUES ('SPU008', 'Zara Oversized Hoodie', 'Trendy streetwear hoodie for everyday style', 8, 3, 17, 171, '[\"/images/goods/spu008-1.jpg\", \"/images/goods/spu008-2.jpg\"]', '30-day return, easy size exchange', '<h2>Oversized Hoodie</h2><p>Comfortable cotton blend with trendy oversized fit.</p>', '[{\"color\": \"Black\", \"size\": \"M\"}]', 1, 0, 1, '1002');
INSERT INTO `spu` VALUES ('SPU009', 'Huawei Mate 60 Pro', 'Huawei flagship with Kirin 9000S chip', 10, 1, 11, 111, '[\"/images/goods/iphone-huawei-1.jpg\", \"/images/goods/iphone-huawei-2.jpg\"]', '7-day no-reason return, 2-year warranty', '<h1>Huawei Mate 60 Pro</h1><p>Satellite communication capability.</p>', '[{\"color\": \"Black\", \"storage\": \"512GB\"}]', 1, 0, 1, '1001');
INSERT INTO `spu` VALUES ('SPU010', 'Xiaomi 14 Pro', 'Xiaomi flagship with Snapdragon 8 Gen 3', 11, 1, 11, 111, '[\"/images/goods/iphone-xiaomi-1.jpg\", \"/images/goods/iphone-xiaomi-2.jpg\"]', '7-day no-reason return, 1-year warranty', '<h1>Xiaomi 14 Pro</h1><p>Leica optics system.</p>', '[{\"color\": \"White\", \"storage\": \"256GB\"}]', 1, 0, 1, '1001');

-- ----------------------------
-- Table structure for sys_admin
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin`  (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
                              `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码(明文演示)',
                              `tenant_id` bigint NOT NULL COMMENT '所属租户ID',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `uk_tenant_username`(`tenant_id` ASC, `username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'B端租户管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_admin
-- ----------------------------
INSERT INTO `sys_admin` VALUES (1, 'admin', '123456', 1001);
INSERT INTO `sys_admin` VALUES (2, 'zhangsan', '123456', 1001);
INSERT INTO `sys_admin` VALUES (3, 'lisi', '123456', 1002);

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
                              `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名（登录账号）',
                              `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（明文存储，仅测试用）',
                              `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
                              `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
                              `points` int NULL DEFAULT 0 COMMENT '积分',
                              `roles` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'USER' COMMENT '角色（多个用逗号分隔）',
                              `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1001' COMMENT '租户ID（多租户隔离）',
                              `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `uk_tenant_username`(`tenant_id` ASC, `username` ASC) USING BTREE,
                              INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'C端买家用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, 'zhangsan', '123456', '13800138001', '张三', 100, 'USER', '1001', '2026-04-09 13:54:01', '2026-04-09 13:54:01');
INSERT INTO `user_info` VALUES (2, 'lisi', '123456', '13800138002', '李四', 200, 'USER,VIP', '1001', '2026-04-09 13:54:01', '2026-04-09 13:54:01');
INSERT INTO `user_info` VALUES (3, 'wangwu', '123456', '13900139001', '王五', 150, 'USER', '1002', '2026-04-09 13:54:01', '2026-04-09 13:54:01');
INSERT INTO `user_info` VALUES (4, 'zhaoliu', '123456', '13900139002', '赵六', 300, 'USER,VIP', '1002', '2026-04-09 13:54:01', '2026-04-09 13:54:01');

SET FOREIGN_KEY_CHECKS = 1;
