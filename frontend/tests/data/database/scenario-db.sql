-- Santorini 棋谱数据库设计
-- 适用于大规模场景管理和复杂查询

-- 场景基本信息表
CREATE TABLE scenarios (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    version VARCHAR(20) DEFAULT '1.0',
    difficulty ENUM('basic', 'intermediate', 'advanced', 'expert') DEFAULT 'basic',
    author VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    estimated_duration INTEGER, -- 预计执行时间（秒）
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_difficulty (difficulty),
    INDEX idx_author (author),
    INDEX idx_created (created_at)
);

-- 游戏设置表
CREATE TABLE scenario_setups (
    scenario_id VARCHAR(100) PRIMARY KEY,
    player1_name VARCHAR(100) NOT NULL,
    player1_god VARCHAR(50) NOT NULL,
    player2_name VARCHAR(100) NOT NULL, 
    player2_god VARCHAR(50) NOT NULL,
    board_width INTEGER DEFAULT 5,
    board_height INTEGER DEFAULT 5,
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE
);

-- 游戏移动表
CREATE TABLE scenario_moves (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    scenario_id VARCHAR(100) NOT NULL,
    step_number INTEGER NOT NULL,
    move_type ENUM('place_worker', 'move', 'build', 'special_action') NOT NULL,
    player ENUM('player1', 'player2') NOT NULL,
    from_x INTEGER,
    from_y INTEGER,
    to_x INTEGER NOT NULL,
    to_y INTEGER NOT NULL,
    build_at_x INTEGER,
    build_at_y INTEGER,
    metadata JSON, -- 存储额外的元数据
    description TEXT,
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    INDEX idx_scenario_step (scenario_id, step_number),
    INDEX idx_move_type (move_type),
    INDEX idx_player (player)
);

-- 预期结果表
CREATE TABLE scenario_outcomes (
    scenario_id VARCHAR(100) PRIMARY KEY,
    expected_winner ENUM('player1', 'player2', 'draw', 'none'),
    expected_game_state ENUM('in_progress', 'ended') DEFAULT 'in_progress',
    board_state JSON, -- 存储预期的棋盘状态
    validations JSON, -- 存储验证规则
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE
);

-- 标签表
CREATE TABLE tags (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    color VARCHAR(7), -- 十六进制颜色代码
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 场景标签关联表
CREATE TABLE scenario_tags (
    scenario_id VARCHAR(100) NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (scenario_id, tag_id),
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- 测试执行记录表
CREATE TABLE test_executions (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    scenario_id VARCHAR(100) NOT NULL,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('passed', 'failed', 'skipped', 'error') NOT NULL,
    duration INTEGER, -- 执行时间（毫秒）
    error_message TEXT,
    browser VARCHAR(50),
    viewport VARCHAR(20),
    test_environment VARCHAR(50),
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id),
    INDEX idx_scenario_status (scenario_id, status),
    INDEX idx_executed_at (executed_at)
);

-- 场景关系表（依赖关系、变体等）
CREATE TABLE scenario_relationships (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    parent_scenario_id VARCHAR(100) NOT NULL,
    related_scenario_id VARCHAR(100) NOT NULL,
    relationship_type ENUM('depends_on', 'variant_of', 'sequel_to', 'related') NOT NULL,
    description TEXT,
    FOREIGN KEY (parent_scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    FOREIGN KEY (related_scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    INDEX idx_parent (parent_scenario_id),
    INDEX idx_related (related_scenario_id),
    INDEX idx_relationship_type (relationship_type)
);

-- 插入基础标签数据
INSERT INTO tags (name, description, color) VALUES
('apollo', 'Apollo神卡相关测试', '#FF6B6B'),
('artemis', 'Artemis神卡相关测试', '#4ECDC4'),  
('athena', 'Athena神卡相关测试', '#45B7D1'),
('atlas', 'Atlas神卡相关测试', '#96CEB4'),
('demeter', 'Demeter神卡相关测试', '#FFEAA7'),
('hephaestus', 'Hephaestus神卡相关测试', '#DDA0DD'),
('hermes', 'Hermes神卡相关测试', '#98D8C8'),
('minotaur', 'Minotaur神卡相关测试', '#F7DC6F'),
('pan', 'Pan神卡相关测试', '#BB8FCE'),
('prometheus', 'Prometheus神卡相关测试', '#85C1E9'),
('basic', '基础功能测试', '#A8E6CF'),
('advanced', '高级功能测试', '#FFD3A5'),
('god-card', '神卡能力测试', '#FD79A8'),
('ui', '用户界面测试', '#FDCB6E'),
('performance', '性能测试', '#E17055'),
('regression', '回归测试', '#74B9FF');

-- 示例数据插入
INSERT INTO scenarios (id, name, description, difficulty, author, estimated_duration) VALUES
('apollo_swap_basic', 'Apollo基础换位测试', '测试Apollo神卡与敌方工人交换位置的基本能力', 'basic', 'Test Team', 30),
('demeter_double_build', 'Demeter双重建造测试', '测试Demeter神卡的可选第二次建造能力', 'intermediate', 'Test Team', 45);

-- 插入Apollo换位场景的设置
INSERT INTO scenario_setups (scenario_id, player1_name, player1_god, player2_name, player2_god) VALUES
('apollo_swap_basic', 'Apollo_Alice', 'Apollo', 'Normal_Bob', 'Demeter');

-- 插入Apollo换位场景的移动
INSERT INTO scenario_moves (scenario_id, step_number, move_type, player, to_x, to_y, metadata, description) VALUES
('apollo_swap_basic', 1, 'place_worker', 'player1', 1, 0, '{"workerIndex": 0}', 'Alice放置第一个工人'),
('apollo_swap_basic', 2, 'place_worker', 'player1', 2, 1, '{"workerIndex": 1}', 'Alice放置第二个工人'),
('apollo_swap_basic', 3, 'place_worker', 'player2', 1, 1, '{"workerIndex": 0}', 'Bob放置第一个工人'),
('apollo_swap_basic', 4, 'place_worker', 'player2', 3, 3, '{"workerIndex": 1}', 'Bob放置第二个工人'),
('apollo_swap_basic', 5, 'special_action', 'player1', 1, 1, '{"specialAbility": "swap_positions", "targetPlayer": "player2"}', 'Apollo使用换位能力');

-- 更新移动表中的from位置（用于移动操作）
UPDATE scenario_moves SET from_x = 1, from_y = 0 WHERE scenario_id = 'apollo_swap_basic' AND step_number = 5;

-- 插入预期结果
INSERT INTO scenario_outcomes (scenario_id, expected_game_state, board_state) VALUES
('apollo_swap_basic', 'in_progress', '{"1,0": {"worker": {"player": "player2", "index": 0}}, "1,1": {"worker": {"player": "player1", "index": 0}}}');

-- 关联标签
INSERT INTO scenario_tags (scenario_id, tag_id) VALUES
('apollo_swap_basic', (SELECT id FROM tags WHERE name = 'apollo')),
('apollo_swap_basic', (SELECT id FROM tags WHERE name = 'basic')),
('apollo_swap_basic', (SELECT id FROM tags WHERE name = 'god-card'));

-- 常用查询视图
CREATE VIEW scenario_summary AS
SELECT 
    s.id,
    s.name,
    s.description,
    s.difficulty,
    s.author,
    s.created_at,
    ss.player1_god,
    ss.player2_god,
    COUNT(sm.id) as move_count,
    GROUP_CONCAT(t.name) as tags
FROM scenarios s
LEFT JOIN scenario_setups ss ON s.id = ss.scenario_id
LEFT JOIN scenario_moves sm ON s.id = sm.scenario_id  
LEFT JOIN scenario_tags st ON s.id = st.scenario_id
LEFT JOIN tags t ON st.tag_id = t.id
WHERE s.is_active = TRUE
GROUP BY s.id;

-- 测试统计视图
CREATE VIEW test_execution_stats AS
SELECT 
    s.id as scenario_id,
    s.name,
    COUNT(te.id) as total_executions,
    SUM(CASE WHEN te.status = 'passed' THEN 1 ELSE 0 END) as passed_count,
    SUM(CASE WHEN te.status = 'failed' THEN 1 ELSE 0 END) as failed_count,
    AVG(te.duration) as avg_duration,
    MAX(te.executed_at) as last_execution
FROM scenarios s
LEFT JOIN test_executions te ON s.id = te.scenario_id
GROUP BY s.id, s.name;

-- 常用查询示例

-- 1. 查找所有Apollo相关的测试场景
-- SELECT * FROM scenario_summary WHERE tags LIKE '%apollo%';

-- 2. 查找失败率高的场景
-- SELECT * FROM test_execution_stats WHERE failed_count > passed_count;

-- 3. 查找特定难度的场景
-- SELECT * FROM scenarios WHERE difficulty = 'advanced' AND is_active = TRUE;

-- 4. 查找执行时间最长的场景
-- SELECT * FROM test_execution_stats ORDER BY avg_duration DESC LIMIT 10;

-- 5. 查找场景依赖关系
-- SELECT 
--   sr.parent_scenario_id,
--   s1.name as parent_name,
--   sr.related_scenario_id, 
--   s2.name as related_name,
--   sr.relationship_type
-- FROM scenario_relationships sr
-- JOIN scenarios s1 ON sr.parent_scenario_id = s1.id
-- JOIN scenarios s2 ON sr.related_scenario_id = s2.id
-- WHERE sr.relationship_type = 'depends_on';