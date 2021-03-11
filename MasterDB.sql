CREATE DATABASE kfdms;

USE kfdms;

CREATE TABLE `filenode` (
  `file_id` int(64) NOT NULL AUTO_INCREMENT,
  `file_encode_name` varchar(255) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_folder_id` int(8) DEFAULT '0',
  `file_owner_id` int(64) NOT NULL,
  `file_owner_name` varchar(255) DEFAULT NULL,
  `file_type` int(8) DEFAULT '1',
  `data_type` varchar(20) DEFAULT NULL,
  `file_description` text,
  `file_size` int(64) DEFAULT NULL,
  `file_md5` varchar(64) DEFAULT NULL,
  `file_permission` int(8) DEFAULT '0',
  `file_create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `file_download_num` int(64) DEFAULT '0',
  `file_visited_num` int(64) DEFAULT '0',
  `file_modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8

CREATE TABLE `fileshare_sharelog` (
  `sharelog_id` int(64) NOT NULL AUTO_INCREMENT,
  `user_id` int(64) DEFAULT NULL,
  `file_id` int(64) DEFAULT '-1',
  `folder_id` int(64) DEFAULT '-1',
  `file_name` varchar(255) DEFAULT NULL,
  `access_code` varchar(4) DEFAULT NULL,
  `share_link` varchar(64) DEFAULT NULL,
  `visit_num` int(8) DEFAULT '0',
  `visit_limit` int(8) DEFAULT '0',
  `valid_period` int(8) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `expired_time` datetime DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  `status` int(8) DEFAULT NULL,
  `version` int(16) DEFAULT '0',
  PRIMARY KEY (`sharelog_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8

CREATE TABLE `folder` (
  `folder_id` int(64) NOT NULL AUTO_INCREMENT,
  `folder_name` varchar(255) NOT NULL,
  `folder_parent_id` int(64) DEFAULT '0',
  `folder_owner_id` int(64) NOT NULL,
  `folder_owner_name` varchar(255) DEFAULT NULL,
  `folder_type` int(8) DEFAULT '1',
  `is_private` int(8) DEFAULT '1',
  `folder_size` int(64) DEFAULT NULL,
  `folder_max_size` int(64) DEFAULT '1024',
  `folder_description` text,
  `folder_create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `folder_modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`folder_id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8

CREATE TABLE `user` (
  `id` int(64) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_type` int(8) DEFAULT '1',
  `verification` varchar(255) DEFAULT NULL,
  `active_status` int(8) DEFAULT '0',
  `base_folder_id` int(64) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sex` int(1) DEFAULT NULL,
  `student_id` varchar(32) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `start_year` int(16) DEFAULT NULL,
  `login_forbidden` int(8) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8

CREATE TABLE `verification_log` (
  `verification_id` int(64) NOT NULL AUTO_INCREMENT,
  `verification_owner` int(64) NOT NULL,
  `verification_code` varchar(255) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_used` int(8) DEFAULT '0',
  `drop_time` datetime DEFAULT NULL,
  `used_user_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`verification_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8

insert into verification_log(verification_owner,verification_code) values(-1,substr(md5(rand()),1,6))