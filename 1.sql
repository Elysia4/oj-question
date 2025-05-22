-- 创建库
create database if not exists elyoj;

-- 切换库
use elyoj;

-- 用户表
create table if not exists user
(
    id          bigint auto_increment comment 'id' primary key,
    username    varchar(256)                       not null comment '用户名',
    password    varchar(512)                       not null comment '密码',
    email       varchar(256)                       null comment '邮箱',
    role        varchar(256) default 'user'        not null comment '角色（user/admin）',
    status      int default 0                      not null comment '状态（0 - 正常、1 - 禁用）',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete    tinyint default 0                  not null comment '是否删除',
    unique key uk_username (username)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 题目表
create table if not exists question
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    answer      text                               null comment '题目答案',
    submitNum   int      default 0                 not null comment '题目提交数',
    acceptedNum int      default 0                 not null comment '题目通过数',
    judgeCase   text                               null comment '判题用例（json 数组）',
    judgeConfig text                               null comment '判题配置（json 对象）',
    difficulty  int      default 0                 not null comment '难度（0 - 简单、1 - 中等、2 - 困难）',
    userId      bigint                             null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete    tinyint default 0                  not null comment '是否删除',
    index idx_userId (userId),
    index idx_title (title(255))
) comment '题目' collate = utf8mb4_unicode_ci;

-- 题目提交表
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    language   varchar(128)                       not null comment '编程语言',
    userId     bigint                             not null comment '提交用户 id',
    questionId bigint                             not null comment '题目 id',
    code       text                               not null comment '用户代码',
    judgeInfo  text                               null comment '判题信息（json 对象）',
    status     int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete   tinyint default 0                  not null comment '是否删除',
    index idx_userId (userId),
    index idx_questionId (questionId)
) comment '题目提交';

-- 插入初始管理员用户 (密码: admin123)
insert into user (username, password, role) values ('admin', 'e64b78fc3bc91bcbc7dc232ba8ec59e0', 'admin');
