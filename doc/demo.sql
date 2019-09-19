/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : demo

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2019-09-19 22:48:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `dispatch_order`
-- ----------------------------
DROP TABLE IF EXISTS `dispatch_order`;
CREATE TABLE `dispatch_order` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1049 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dispatch_order
-- ----------------------------

-- ----------------------------
-- Table structure for `pay_order`
-- ----------------------------
DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `status` varchar(2) DEFAULT NULL,
  `name` varchar(16) DEFAULT NULL,
  `order_id` varchar(32) DEFAULT NULL,
  `money` double DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1091 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of pay_order
-- ----------------------------
