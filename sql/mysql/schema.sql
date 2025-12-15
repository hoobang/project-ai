CREATE DATABASE IF NOT EXISTS `wechat` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `wechat`;

CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `phone` VARCHAR(11) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(100),
  `avatar` VARCHAR(255),
  `signature` VARCHAR(500),
  `gender` VARCHAR(20),
  `location` VARCHAR(255),
  `birth_date` DATETIME,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME,
  `last_login` DATETIME,
  `active` TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `messages` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `sender_id` BIGINT NOT NULL,
  `receiver_id` BIGINT NOT NULL,
  `content` TEXT,
  `type` VARCHAR(20) NOT NULL,
  `file_path` VARCHAR(255),
  `file_name` VARCHAR(100),
  `file_size` BIGINT,
  `is_read` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME,
  INDEX `idx_messages_sender` (`sender_id`),
  INDEX `idx_messages_receiver` (`receiver_id`),
  CONSTRAINT `fk_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_messages_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `chat_groups` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `group_name` VARCHAR(100) NOT NULL,
  `group_avatar` VARCHAR(255),
  `group_description` TEXT,
  `creator_id` BIGINT NOT NULL,
  `member_count` INT NOT NULL DEFAULT 0,
  `max_members` INT NOT NULL DEFAULT 500,
  `is_public` TINYINT(1) NOT NULL DEFAULT 1,
  `invite_code` VARCHAR(20) UNIQUE,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME,
  INDEX `idx_groups_creator` (`creator_id`),
  CONSTRAINT `fk_groups_creator` FOREIGN KEY (`creator_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `group_members` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `group_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `group_nickname` VARCHAR(50),
  `role` VARCHAR(20) NOT NULL,
  `is_muted` TINYINT(1) NOT NULL DEFAULT 0,
  `mute_end_time` DATETIME,
  `joined_at` DATETIME NOT NULL,
  `updated_at` DATETIME,
  UNIQUE KEY `uniq_group_user` (`group_id`, `user_id`),
  INDEX `idx_group_members_group` (`group_id`),
  INDEX `idx_group_members_user` (`user_id`),
  CONSTRAINT `fk_group_members_group` FOREIGN KEY (`group_id`) REFERENCES `chat_groups`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_group_members_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `group_messages` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `group_id` BIGINT NOT NULL,
  `sender_id` BIGINT NOT NULL,
  `content` TEXT,
  `type` VARCHAR(20) NOT NULL,
  `file_path` VARCHAR(255),
  `file_name` VARCHAR(100),
  `file_size` BIGINT,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME,
  INDEX `idx_group_messages_group` (`group_id`),
  INDEX `idx_group_messages_sender` (`sender_id`),
  CONSTRAINT `fk_group_messages_group` FOREIGN KEY (`group_id`) REFERENCES `chat_groups`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_group_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `friendships` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `friend_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME,
  `accepted_at` DATETIME,
  UNIQUE KEY `uniq_user_friend` (`user_id`, `friend_id`),
  INDEX `idx_friendships_user` (`user_id`),
  INDEX `idx_friendships_friend` (`friend_id`),
  CONSTRAINT `fk_friendships_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_friendships_friend` FOREIGN KEY (`friend_id`) REFERENCES `users`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

