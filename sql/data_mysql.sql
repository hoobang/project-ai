USE `wechat`;

INSERT INTO `users` (`id`, `username`, `email`, `phone`, `password`, `nickname`, `gender`, `active`, `created_at`, `updated_at`)
VALUES
(1, 'alice', 'alice@example.com', '13000000001', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1CR5R.pItcBk.uI3Yyii1K9ofuSGK', 'Alice', 'F', 1, NOW(), NOW()),
(2, 'bob', 'bob@example.com', '13000000002', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1CR5R.pItcBk.uI3Yyii1K9ofuSGK', 'Bob', 'M', 1, NOW(), NOW()),
(3, 'charlie', 'charlie@example.com', '13000000003', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi1CR5R.pItcBk.uI3Yyii1K9ofuSGK', 'Charlie', 'M', 1, NOW(), NOW());

INSERT INTO `friendships` (`id`, `user_id`, `friend_id`, `status`, `created_at`, `updated_at`, `accepted_at`)
VALUES
(1, 1, 2, 'ACCEPTED', NOW(), NOW(), NOW()),
(2, 2, 3, 'PENDING', NOW(), NOW(), NULL);

INSERT INTO `messages` (`id`, `sender_id`, `receiver_id`, `content`, `type`, `is_read`, `created_at`, `updated_at`)
VALUES
(1, 1, 2, 'Hi Bob!', 'TEXT', 0, NOW(), NOW()),
(2, 2, 1, 'Hey Alice!', 'TEXT', 0, NOW(), NOW()),
(3, 1, 3, 'Hi Charlie!', 'TEXT', 0, NOW(), NOW());

INSERT INTO `chat_groups` (`id`, `group_name`, `group_avatar`, `group_description`, `creator_id`, `member_count`, `max_members`, `is_public`, `invite_code`, `created_at`, `updated_at`)
VALUES
(1, 'Demo Group', NULL, 'Demo group for testing', 1, 3, 500, 1, 'ABC123', NOW(), NOW());

INSERT INTO `group_members` (`id`, `group_id`, `user_id`, `group_nickname`, `role`, `is_muted`, `joined_at`, `updated_at`)
VALUES
(1, 1, 1, 'Alice', 'OWNER', 0, NOW(), NOW()),
(2, 1, 2, 'Bob', 'ADMIN', 0, NOW(), NOW()),
(3, 1, 3, 'Charlie', 'MEMBER', 0, NOW(), NOW());

INSERT INTO `group_messages` (`id`, `group_id`, `sender_id`, `content`, `type`, `created_at`, `updated_at`)
VALUES
(1, 1, 1, 'Welcome to the group!', 'TEXT', NOW(), NOW()),
(2, 1, 2, 'Thanks!', 'TEXT', NOW(), NOW()),
(3, 1, 3, 'Hello everyone!', 'TEXT', NOW(), NOW());

