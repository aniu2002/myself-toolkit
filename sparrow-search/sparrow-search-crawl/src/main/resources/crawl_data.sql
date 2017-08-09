/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : sparrow_is

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2016-12-08 18:23:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for crawl_data
-- ----------------------------
DROP TABLE IF EXISTS `crawl_data`;
CREATE TABLE `crawl_data` (
  `uuid` char(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '唯一id',
  `site_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '站点id',
  `site_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '站点名称',
  `site_url` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '站点url',
  `page_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '分页入口',
  `detail_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '详情页',
  `title` varchar(64) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '标题',
  `subject` varchar(128) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '主题',
  `content` text COLLATE utf8_unicode_ci COMMENT '内容',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `time` datetime DEFAULT NULL COMMENT '抓取时间',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
