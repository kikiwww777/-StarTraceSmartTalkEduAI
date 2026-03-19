-- 如果你之前已经创建了 learning_templates 表，但缺少 download_count 字段，
-- 执行下面的 ALTER 语句补齐（建议先在本地数据库确认字段是否存在）。
--
-- 注意：MySQL 版本不同对 IF NOT EXISTS 支持不一致，
-- 如果你的 MySQL 不支持 ADD COLUMN IF NOT EXISTS，请手动删除该关键字再执行，或先检查字段存在性。

-- 方案 A：MySQL 8.0.29+（部分版本支持）
-- ALTER TABLE learning_templates
--   ADD COLUMN IF NOT EXISTS download_count INT NOT NULL DEFAULT 0 COMMENT '学生下载量';

-- 方案 B：通用写法（如已存在会报错，需要你确认后再执行）
ALTER TABLE learning_templates
  ADD COLUMN download_count INT NOT NULL DEFAULT 0 COMMENT '学生下载量';


