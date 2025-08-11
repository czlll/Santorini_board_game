# Santorini游戏测试框架

本测试框架实现了棋谱数据与测试代码的解耦设计，提供了可维护、可扩展的E2E测试解决方案。

## 架构概览

```
tests/
├── data/                    # 棋谱数据层
│   ├── game-notation.ts     # 棋谱数据格式定义
│   └── scenarios/           # 预定义测试场景
├── engine/                  # 测试引擎层
│   ├── game-engine.ts       # 游戏测试执行引擎
│   └── validators.ts        # 数据验证器
├── pages/                   # 页面对象层
│   ├── game-pages.ts        # 页面对象模式实现
│   └── components/          # 可重用组件
├── refactored/             # 重构后的测试用例
│   ├── game-flow-decoupled.spec.ts
│   ├── ui-components-decoupled.spec.ts
│   └── god-cards-decoupled.spec.ts
├── utils/                   # 工具函数
│   └── test-helpers.ts      # 测试辅助工具
└── README.md               # 本文档
```

## 核心概念

### 1. 棋谱数据格式 (Game Notation)

使用JSON格式描述游戏场景，包含：

- **GameSetup**: 游戏初始设置（玩家、神卡等）
- **GameMove**: 单个游戏动作（放置、移动、建造等）
- **GameScenario**: 完整的测试场景

```typescript
interface GameScenario {
  id: string;
  name: string;
  description: string;
  setup: GameSetup;
  moves: GameMove[];
  expectedOutcome?: GameOutcome;
}
```

### 2. 测试引擎 (Test Engine)

`SantoriniTestEngine` 类负责：

- 解释棋谱数据
- 执行游戏操作
- 验证游戏状态
- 提供统一的API接口

```typescript
const engine = new SantoriniTestEngine(page);
await engine.executeScenario(scenario);
```

### 3. 页面对象模式 (Page Object Model)

封装UI操作，提供稳定的接口：

- **HomePage**: 主页操作
- **PlayerFormPage**: 玩家表单页面
- **GameBoardPage**: 游戏板页面

```typescript
const pageManager = new PageObjectManager(page);
await pageManager.setupCompleteGame('Alice', 'Bob');
```

## 使用指南

### 创建新的测试场景

1. **定义棋谱数据**：

```typescript
const newScenario: GameScenario = {
  id: 'my_test_scenario',
  name: '我的测试场景',
  description: '场景描述',
  setup: {
    players: {
      player1: { name: 'Alice', godCard: 'Apollo' },
      player2: { name: 'Bob', godCard: 'Demeter' }
    },
    boardSize: { width: 5, height: 5 }
  },
  moves: [
    { type: 'place_worker', player: 'player1', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } }
    // ... 更多移动
  ]
};
```

2. **执行测试**：

```typescript
test('My custom scenario', async ({ page }) => {
  const engine = new SantoriniTestEngine(page);
  await engine.executeScenario(newScenario);
});
```

### 使用测试助手

```typescript
import { TestHelpers, TestDataGenerator } from './utils/test-helpers';

// 创建基础场景
const scenario = TestHelpers.createBasicWorkerPlacementScenario('Alice', 'Bob');

// 生成随机测试数据
const [god1, god2] = TestDataGenerator.generateGodCardPair();

// 创建神卡特定场景
const apolloScenario = TestHelpers.createGodCardScenario('Apollo');
```

## 棋谱数据示例

### 基础工人放置

```json
{
  "id": "basic_worker_placement",
  "name": "基础工人放置",
  "setup": {
    "players": {
      "player1": { "name": "Alice", "godCard": "Demeter" },
      "player2": { "name": "Bob", "godCard": "Pan" }
    }
  },
  "moves": [
    { "type": "place_worker", "player": "player1", "to": { "x": 0, "y": 0 } },
    { "type": "place_worker", "player": "player1", "to": { "x": 1, "y": 1 } },
    { "type": "place_worker", "player": "player2", "to": { "x": 2, "y": 2 } },
    { "type": "place_worker", "player": "player2", "to": { "x": 3, "y": 3 } }
  ]
}
```

### Apollo换位能力

```json
{
  "id": "apollo_swap_move",
  "name": "Apollo换位能力测试",
  "moves": [
    // 工人放置...
    { 
      "type": "special_action", 
      "player": "player1", 
      "from": { "x": 1, "y": 0 }, 
      "to": { "x": 1, "y": 1 } 
    }
  ]
}
```

## 优势特点

### 1. 数据与代码解耦

- 棋谱数据独立存储，易于维护和版本控制
- 测试逻辑与具体UI操作分离
- 支持数据驱动测试

### 2. 可重用性

- 页面对象可在多个测试中复用
- 测试引擎提供统一接口
- 辅助工具简化常见操作

### 3. 可维护性

- 清晰的分层架构
- 类型安全的TypeScript实现
- 标准化的错误处理

### 4. 可扩展性

- 易于添加新的神卡测试场景
- 支持复杂的游戏状态验证
- 模块化设计便于扩展功能

## 运行测试

```bash
# 运行所有解耦测试
npx playwright test tests/refactored/

# 运行特定测试文件
npx playwright test tests/refactored/game-flow-decoupled.spec.ts

# 以调试模式运行
npx playwright test --debug tests/refactored/

# 生成测试报告
npx playwright test --reporter=html
```

## 最佳实践

### 1. 棋谱设计原则

- 保持场景简洁明确
- 使用描述性的ID和名称
- 包含足够的元数据信息
- 验证边界条件和错误情况

### 2. 测试编写指南

- 使用页面对象而不是直接操作元素
- 通过测试引擎执行复杂场景
- 添加适当的等待和验证
- 使用测试助手简化常见操作

### 3. 维护建议

- 定期验证所有预定义场景
- 保持棋谱数据格式一致性
- 及时更新页面对象以适应UI变化
- 使用版本控制追踪场景变更

## 扩展功能

### 添加新神卡支持

1. 在 `game-notation.ts` 中添加神卡特定的数据格式
2. 在 `game-engine.ts` 中实现神卡特殊能力的执行逻辑
3. 在 `test-helpers.ts` 中添加神卡场景生成器
4. 创建对应的测试用例

### 添加新的验证器

```typescript
export function validateCustomRule(scenario: GameScenario): string[] {
  const errors: string[] = [];
  // 自定义验证逻辑
  return errors;
}
```

### 扩展页面对象

```typescript
export class CustomGamePage {
  constructor(private page: Page) {}
  
  async customAction() {
    // 自定义页面操作
  }
}
```

这个测试框架为Santorini游戏提供了一个完整的、可维护的测试解决方案，实现了棋谱数据与测试代码的完全解耦。