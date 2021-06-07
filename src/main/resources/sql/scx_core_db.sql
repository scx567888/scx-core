-- For MySql Server Version	8.0.22
-- 核心功能表

-- 添加初始管理员用户 用户名 admin 密码 123456
INSERT INTO scx.auth_user (username, salt, password, is_admin)
VALUES ('admin', 'b1bee9691622e55f', 'qEB8gv1T/ZPaKwU6dN9ZWi4UNdhGXJOXjlZSOryemqWUlHoC5FKRwJOxludkytB2', true);
-- 添加默认 license
INSERT INTO scx.auth_license (id, create_date, update_date, is_deleted, flag, last_time)
VALUES (1, '2020-11-02 11:31:24', '2020-11-02 11:36:05', 0, 1, '2020-11-02');
