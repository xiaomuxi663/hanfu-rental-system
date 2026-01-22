/*
  汉服租赁管理系统数据库脚本
  包含: RBAC权限, SPU/SKU库存, 地址快照, 逾期扣费, 图片相册
  共13张表
*/

-- 1. 创建数据库 (强制指定 utf8mb4 避免中文乱码)
CREATE DATABASE IF NOT EXISTS hanfu_rental_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE hanfu_rental_db;

-- ==========================================
-- 第一部分：权限与用户模块 (RBAC)
-- ==========================================

-- 1. 用户基础表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `username` varchar(50) NOT NULL UNIQUE COMMENT '账号',
  `password` varchar(100) NOT NULL COMMENT '密码(加密)',
  `nickname` varchar(50) COMMENT '昵称',
  
  -- 实名认证 (国标长度)
  `real_name` varchar(50) COMMENT '真实姓名',
  `id_card` char(18) COMMENT '身份证号(18位)',
  `phone` char(11) COMMENT '手机号(11位)',
  
  -- 默认地址模板 (用于下单自动填充，可为空)
  `default_receiver_name` varchar(50) COMMENT '默认收货人',
  `default_receiver_phone` char(11) COMMENT '默认收货电话',
  `default_address` varchar(255) COMMENT '默认收货地址',

  `avatar` varchar(255) COMMENT '头像URL',
  `credit_score` int(11) DEFAULT 100 COMMENT '信用分',
  `balance` decimal(10,2) DEFAULT 0.00 COMMENT '钱包余额',
  `status` tinyint(1) DEFAULT 1 COMMENT '1正常, 0禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 2. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `role_name` varchar(20) NOT NULL COMMENT '角色名',
  `role_key` varchar(20) NOT NULL COMMENT '标识(admin/staff/renter)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 用户-角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限关联表';

-- ==========================================
-- 第二部分：汉服商品模块 (SPU/SKU + 图片)
-- ==========================================

-- 4. 汉服分类表
DROP TABLE IF EXISTS `hanfu_category`;
CREATE TABLE `hanfu_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `category_name` varchar(50) NOT NULL COMMENT '形制(如:明制)',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类字典表';

-- 5. 汉服款式表 (SPU) - 已添加 sub_images
DROP TABLE IF EXISTS `hanfu_spu`;
CREATE TABLE `hanfu_spu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `category_id` int(11) NOT NULL COMMENT '分类ID',
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `daily_rent` decimal(10,2) NOT NULL COMMENT '日租金',
  `deposit` decimal(10,2) NOT NULL COMMENT '押金',
  
  -- 图片展示核心字段
  `main_image` varchar(255) COMMENT '主图(列表页/搜索页展示)',
  `sub_images` varchar(1000) COMMENT '详情页顶部相册(逗号分隔多个URL)',
  `detail_content` text COMMENT '底部详情(富文本HTML)',
  
  `is_publish` tinyint(1) DEFAULT 1 COMMENT '1上架, 0下架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='款式信息表';

-- 6. 汉服实物库存表 (SKU)
DROP TABLE IF EXISTS `hanfu_sku`;
CREATE TABLE `hanfu_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `spu_id` bigint(20) NOT NULL COMMENT '款式ID',
  `sku_code` varchar(50) NOT NULL UNIQUE COMMENT '实物唯一编号',
  `size` varchar(10) NOT NULL COMMENT '尺码(S/M/L)',
  `status` tinyint(4) DEFAULT 0 COMMENT '0:在库, 1:已租, 2:清洗中, 3:维修中, 4:报废',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实物库存表';

-- ==========================================
-- 第三部分：交易与物流模块 (快照逻辑)
-- ==========================================

-- 7. 租赁订单表
DROP TABLE IF EXISTS `rental_order`;
CREATE TABLE `rental_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `order_sn` varchar(64) NOT NULL UNIQUE COMMENT '订单号',
  `user_id` bigint(20) NOT NULL,
  `sku_id` bigint(20) NOT NULL,
  
  -- 租期管理 (逾期计算依据)
  `start_date` date NOT NULL COMMENT '起租日',
  `end_date` date NOT NULL COMMENT '应还日',
  
  -- 费用
  `total_rent` decimal(10,2) NOT NULL COMMENT '总租金',
  `total_deposit` decimal(10,2) NOT NULL COMMENT '总押金',
  `status` tinyint(4) DEFAULT 1 COMMENT '1待支付, 2待发货, 3租赁中, 4待归还, 5已完成, 6已取消, 7已逾期',
  
  -- 地址快照 (下单时刻的数据，不可变)
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人快照',
  `receiver_phone` char(11) NOT NULL COMMENT '电话快照',
  `receiver_address` varchar(255) NOT NULL COMMENT '地址快照',
  
  -- 物流
  `express_no` varchar(100) COMMENT '商家发货单号',
  `return_express_no` varchar(100) COMMENT '用户归还单号',
  
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁订单表';

-- 8. 归还验收表 (逾期与定损)
DROP TABLE IF EXISTS `order_inspection`;
CREATE TABLE `order_inspection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `order_id` bigint(20) NOT NULL,
  `staff_id` bigint(20) NOT NULL COMMENT '操作库管员ID',
  `is_damaged` tinyint(1) DEFAULT 0 COMMENT '0完好, 1破损',
  `damage_desc` varchar(255) COMMENT '破损说明',
  
  -- 费用核算
  `penalty_amount` decimal(10,2) DEFAULT 0.00 COMMENT '总扣费(逾期费+赔偿费)',
  
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '实际归还时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='归还验收表';

-- 9. 订单评价表
DROP TABLE IF EXISTS `order_comment`;
CREATE TABLE `order_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `order_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `rating` tinyint(1) DEFAULT 5 COMMENT '评分(1-5)',
  `content` varchar(500) COMMENT '评价内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';

-- ==========================================
-- 第四部分：审计与配置模块
-- ==========================================

-- 10. 信用日志表 (风控)
DROP TABLE IF EXISTS `sys_credit_log`;
CREATE TABLE `sys_credit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` bigint(20) NOT NULL,
  `change_value` int(11) NOT NULL COMMENT '变动值(如 -10)',
  `reason` varchar(100) COMMENT '原因(如:逾期扣分)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信用变更日志表';

-- 11. 财务流水表 (对账)
DROP TABLE IF EXISTS `sys_wallet_log`;
CREATE TABLE `sys_wallet_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` bigint(20) NOT NULL,
  `type` tinyint(4) COMMENT '1租金, 2押金, 3退还, 4扣款, 5充值',
  `amount` decimal(10,2) NOT NULL COMMENT '变动金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包流水表';

-- 12. 系统公告表 (含轮播图)
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `type` tinyint(1) DEFAULT 1 COMMENT '1公告, 2轮播图',
  `img_url` varchar(255) COMMENT '图片地址(仅轮播图用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 13. 系统参数配置表 (商家地址)
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `param_key` varchar(50) NOT NULL UNIQUE COMMENT '参数键',
  `param_value` varchar(500) NOT NULL COMMENT '参数值',
  `param_desc` varchar(200) COMMENT '参数说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表';

-- ==========================================
-- 数据初始化
-- ==========================================

-- 1. 初始化角色
INSERT INTO `sys_role` (id, role_name, role_key) VALUES (1, '管理员', 'admin'), (2, '库管员', 'staff'), (3, '租客', 'renter');

-- 2. 初始化商家配置
INSERT INTO `sys_config` (param_key, param_value, param_desc) VALUES 
('warehouse_address', '浙江省杭州市西湖区汉服文化园3号楼', '用户归还时的收货地址'),
('warehouse_contact', '库管员 (13800138000)', '仓库联系人及电话'),
('cleaning_days', '2', '每单结束后的清洗缓冲期天数');

-- 3. 初始化汉服分类
INSERT INTO `hanfu_category` (category_name) VALUES ('明制'), ('唐制'), ('宋制'), ('晋制');

-- 4. 初始化管理员账号 (密码: admin123)
INSERT INTO `sys_user` (username, password, nickname, status) VALUES 
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Ff53.5z8Q1vN7qM9qxKby', '超级管理员', 1);

INSERT INTO `sys_user_role` (user_id, role_id) VALUES (1, 1);
