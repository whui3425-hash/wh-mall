-- ========================================================-- mall-permission-service Tables for mall_goods Database-- ========================================================

USE mall_goods;

-- ========================================================-- 2. Create Tables (DDL)-- ========================================================

-- Admin table (System Administrator)
CREATE TABLE IF NOT EXISTS sys_admin (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Admin ID',
    username VARCHAR(100) NOT NULL COMMENT 'Admin username',
    password VARCHAR(100) NOT NULL COMMENT 'Admin password',
    tenant_id VARCHAR(32) DEFAULT '1001' COMMENT 'Tenant ID for SaaS isolation',
    UNIQUE KEY uk_username (username),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System administrator table';

-- Role table (Permission Roles)
CREATE TABLE IF NOT EXISTS role_info (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Role ID',
    role_name VARCHAR(100) NOT NULL COMMENT 'Role name',
    description VARCHAR(500) COMMENT 'Role description'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role information table';

-- Permission table (API Permissions)
CREATE TABLE IF NOT EXISTS permission (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Permission ID',
    source_name VARCHAR(100) COMMENT 'Permission source name',
    url VARCHAR(500) NOT NULL COMMENT 'API URL pattern',
    url_match INT DEFAULT 0 COMMENT 'URL match mode: 0-exact match, 1-wildcard match',
    service_name VARCHAR(100) COMMENT 'Service name',
    method VARCHAR(20) COMMENT 'HTTP method'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API permission table';

-- Role-Permission relationship table (Middle table)
CREATE TABLE IF NOT EXISTS role_permission (
    rid INT NOT NULL COMMENT 'Role ID',
    pid INT NOT NULL COMMENT 'Permission ID',
    PRIMARY KEY (rid, pid),
    INDEX idx_pid (pid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role-Permission mapping table';

-- ========================================================-- 3. Insert Test Data (DML)-- ========================================================

-- Admin test data (for login testing)
INSERT INTO sys_admin (id, username, password, tenant_id) VALUES
(1, 'admin', '123456', '1001'),
(2, 'zhangsan', '123456', '1001'),
(3, 'lisi', '123456', '1002');

-- Role test data
INSERT INTO role_info (id, role_name, description) VALUES
(1, 'SUPER_ADMIN', 'Super administrator with all permissions'),
(2, 'ADMIN', 'System administrator'),
(3, 'OPERATOR', 'Business operator'),
(4, 'GUEST', 'Guest user with limited permissions');

-- Permission test data (API endpoints)
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
(51, 'Admin Login', '/api/permission/admin/login', 0, 'mall-permission-service', 'POST');

-- Role-Permission mapping
INSERT INTO role_permission (rid, pid) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 11), (1, 12), (1, 13), (1, 14),
(1, 21), (1, 22),
(1, 31), (1, 32), (1, 33), (1, 34),
(1, 41), (1, 42),
(1, 51);

INSERT INTO role_permission (rid, pid) VALUES
(2, 1), (2, 2), (2, 3), (2, 5), (2, 6), (2, 7),
(2, 11), (2, 12), (2, 13),
(2, 21), (2, 22),
(2, 31), (2, 32), (2, 33),
(2, 41), (2, 42),
(2, 51);

INSERT INTO role_permission (rid, pid) VALUES
(3, 1), (3, 5), (3, 6), (3, 7),
(3, 11), (3, 12),
(3, 31), (3, 32),
(3, 41),
(3, 51);

INSERT INTO role_permission (rid, pid) VALUES
(4, 1), (4, 5), (4, 6), (4, 7),
(4, 11),
(4, 31),
(4, 41);

-- ========================================================-- Initialization Complete-- ========================================================
