-- 模板分类表
CREATE TABLE IF NOT EXISTS `templates_category` (
  `id` VARCHAR(50) NOT NULL COMMENT '分类ID',
  `label` VARCHAR(100) NOT NULL COMMENT '分类标签',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_label` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板分类表';

-- 模板表
CREATE TABLE IF NOT EXISTS `templates` (
  `id` VARCHAR(50) NOT NULL COMMENT '模板ID',
  `title` VARCHAR(200) NOT NULL COMMENT '模板标题',
  `category` VARCHAR(50) NOT NULL COMMENT '分类ID',
  `description` TEXT COMMENT '模板描述',
  `prompt` TEXT COMMENT '模板预设提示词',
  `usage_count` INT UNSIGNED DEFAULT 0 COMMENT '使用次数',
  `rating` DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分（0-5分）',
  `color` VARCHAR(20) DEFAULT '#2563eb' COMMENT '主题颜色',
  `icon` TEXT COMMENT '图标SVG内容',
  `workflow_id` VARCHAR(100) DEFAULT NULL COMMENT '绑定的 SIM 工作流 ID',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_category` (`category`),
  INDEX `idx_rating` (`rating`),
  INDEX `idx_usage_count` (`usage_count`),
  CONSTRAINT `fk_templates_category` FOREIGN KEY (`category`) REFERENCES `templates_category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学模板表';

-- 插入初始分类数据
INSERT INTO `templates_category` (`id`, `label`) VALUES
('all', '全部模板'),
('writing', '学术写作'),
('logic', '逻辑与数理'),
('teaching', '互动教学'),
('coding', '代码实验')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`);

-- 插入初始模板数据
INSERT INTO `templates` (`id`, `title`, `category`, `description`, `prompt`, `usage_count`, `rating`, `color`, `icon`, `workflow_id`) VALUES
('t1', '苏格拉底提问启发器', 'teaching', '通过连环追问引导学生自主思考，而非直接给出答案。适用于深度概念探索。', '你是一名擅长苏格拉底式提问的教师，通过追问帮助学生澄清概念、发现矛盾，而不是直接给出结论。请控制每次回答的长度，并在关键处抛出问题。', 1240, 4.9, '#2563eb', '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>', NULL),
('t2', '学术论文润色大师', 'writing', '针对国际期刊标准，对摘要、引言进行逻辑与用词的专业级润色。', '你是一名熟悉国际学术期刊写作规范的写作教练，重点从逻辑性、清晰度和学术表达三方面给出润色建议。优先给出修改后的版本，并解释关键修改理由。', 5600, 4.8, '#059669', '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>', NULL),
('t3', '数学公式推导纠错', 'logic', '自动检测 LaTeX 渲染的公式推导逻辑，标注潜在的计算与恒等变换错误。', '你是一名数学助教，请逐步检查给出的推导是否严谨。尽量指出哪一步可能有问题，并给出更规范的推导方式。', 890, 4.7, '#7c3aed', '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><line x1="9" y1="9" x2="15" y2="15"/><line x1="15" y1="9" x2="9" y2="15"/></svg>', NULL),
('t4', 'Python 科研脚本优化', 'coding', '分析数据处理脚本的性能瓶颈，提供更具学术复现性的代码重构建议。', '你是一名科研编程专家，请从可读性、可复现性和性能三个维度优化用户提供的 Python 代码，并说明修改思路。', 2100, 4.9, '#0891b2', '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>', NULL),
('t5', '批判性思维辩论模型', 'logic', '模拟正反两方观点，辅助学生训练论据支撑能力与反驳逻辑。', '你是辩论训练教练，请分别从正方和反方给出简要立场与论据，并引导用户提出反驳观点。', 1560, 4.6, '#dc2626', '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>', NULL),
('t6', '文献综述自动化助手', 'writing', '输入多篇文献摘要，自动提取核心观点并对比分析，生成综述框架。', '你是一名科研训练导师，请帮助整理多篇文献的研究问题、方法和结论，并输出一个结构化的综述大纲。', 3400, 4.8, '#ea580c', '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 4.5A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5v-15z"/></svg>', NULL)
ON DUPLICATE KEY UPDATE 
  `title` = VALUES(`title`),
  `category` = VALUES(`category`),
  `description` = VALUES(`description`),
  `prompt` = VALUES(`prompt`),
  `usage_count` = VALUES(`usage_count`),
  `rating` = VALUES(`rating`),
  `color` = VALUES(`color`),
  `icon` = VALUES(`icon`);





