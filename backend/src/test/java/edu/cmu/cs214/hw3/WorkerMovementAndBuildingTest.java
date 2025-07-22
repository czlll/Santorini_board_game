package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 专门测试工人移动和建造功能的测试类
 * 对应文档中的测试用例 7-12
 * 重点测试移动规则、建造规则和各种边界条件
 */
public class WorkerMovementAndBuildingTest {
    
    private Game game;
    private Player playerA;
    private Player playerB;
    private Board board;

    @Before
    public void setUp() throws Exception {
        playerA = new Player("PlayerA");
        playerB = new Player("PlayerB");
        board = new Board();
        game = new Game(board, playerA, playerB);
        setupGameToPlayPhase();
    }

    // ========== 工人移动规则测试 ==========
    
    /**
     * 测试相邻移动的基本功能
     */
    @Test
    public void testAdjacentMovementAllDirections() throws Exception {
        // 简单测试：工人从 (0,0) 移动到相邻的 (1,0)
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // 验证移动成功
        assertTrue("工人应该移动到 (1,0)", board.hasWorker(1, 0));
        assertEquals("(1,0) 应该有玩家 A 的工人", "PlayerA", 
                    board.getCell(1, 0).getWorker().getPlayerName());
        assertFalse("工人应该已从 (0,0) 移除", board.hasWorker(0, 0));
        
        // 完成建造以结束回合
        game.buildAuto(1, 0, 0, 0, false);
        
        // 验证游戏状态：轮到玩家 B
        assertEquals("应该轮到玩家 B", 1, game.getCurPlayer());
        
        // 测试玩家 B 的移动
        game.moveWorkerAuto(4, 4, 3, 4);
        
        // 验证玩家 B 的移动成功
        assertTrue("玩家 B 的工人应该移动到 (3,4)", board.hasWorker(3, 4));
        assertEquals("(3,4) 应该有玩家 B 的工人", "PlayerB", 
                    board.getCell(3, 4).getWorker().getPlayerName());
        assertFalse("玩家 B 的工人应该已从 (4,4) 移除", board.hasWorker(4, 4));
    }
    
    /**
     * 测试高度限制：只能爬升一层
     */
    @Test
    public void testHeightRestrictionClimbOneLevel() throws Exception {
        // 创建不同高度的塔
        board.getCell(1, 1).setTower(new Tower(0)); // 地面
        board.getCell(1, 2).setTower(new Tower(1)); // 1层
        board.getCell(1, 3).setTower(new Tower(2)); // 2层
        board.getCell(1, 4).setTower(new Tower(3)); // 3层
        
        // 将工人移动到地面 (1,1)
        game.moveWorkerAuto(0, 0, 1, 1);
        game.buildAuto(1, 1, 0, 2, false);
        
        // 玩家 B 回合
        game.moveWorkerAuto(4, 4, 3, 4);
        game.buildAuto(3, 4, 3, 3, false);
        
        // 测试从地面爬到1层（应该成功）
        game.moveWorkerAuto(1, 1, 1, 2);
        
        // 验证成功爬升
        assertTrue("工人应该能从地面爬到1层", board.hasWorker(1, 2));
        assertEquals("(1,2) 应该有玩家 A 的工人", "PlayerA", 
                    board.getCell(1, 2).getWorker().getPlayerName());
        
        // 完成建造
        game.buildAuto(1, 2, 0, 2, false);
        
        // 玩家 B 回合
        game.moveWorkerAuto(3, 4, 4, 4);
        game.buildAuto(4, 4, 4, 3, false);
        
        // 测试从1层爬到2层（应该成功）
        game.moveWorkerAuto(1, 2, 1, 3);
        
        // 验证成功爬升
        assertTrue("工人应该能从1层爬到2层", board.hasWorker(1, 3));
        assertEquals("(1,3) 应该有玩家 A 的工人", "PlayerA", 
                    board.getCell(1, 3).getWorker().getPlayerName());
    }
    
    /**
     * 测试高度限制：不能爬升超过一层
     */
    @Test
    public void testHeightRestrictionCannotClimbTwoLevels() throws Exception {
        // 创建高度差为2的相邻格子
        board.getCell(2, 2).setTower(new Tower(0)); // 地面
        board.getCell(2, 3).setTower(new Tower(2)); // 2层
        
        // 将工人移动到 (2,2)
        game.moveWorkerAuto(0, 0, 1, 0);
        game.buildAuto(1, 0, 1, 1, false);
        
        // 玩家 B 回合
        game.moveWorkerAuto(4, 4, 3, 4);
        game.buildAuto(3, 4, 3, 3, false);
        
        // 移动到测试位置
        game.moveWorkerAuto(1, 0, 2, 2);
        
        // 尝试从地面直接爬到2层（应该失败）
        try {
            game.moveWorkerAuto(2, 2, 2, 3);
            fail("不应该能从地面直接爬到2层");
        } catch (Exception e) {
            assertEquals("应该提示不能爬升超过一层", 
                        "Cannot move more one level higher", e.getMessage());
        }
        
        // 验证工人位置未变
        assertTrue("工人应该仍在 (2,2)", board.hasWorker(2, 2));
        assertFalse("工人不应该在 (2,3)", board.hasWorker(2, 3));
    }
    
    /**
     * 测试可以下降任意高度
     */
    @Test
    public void testCanDescendAnyHeight() throws Exception {
        // 创建高塔和地面
        board.getCell(2, 2).setTower(new Tower(3)); // 3层高塔
        board.getCell(2, 3).setTower(new Tower(0)); // 地面
        
        // 将工人放置在高塔上
        game.moveWorkerAuto(0, 0, 2, 2);
        game.buildAuto(2, 2, 2, 1, false);
        
        // 玩家 B 回合
        game.moveWorkerAuto(4, 4, 3, 4);
        game.buildAuto(3, 4, 3, 3, false);
        
        // 测试从3层直接下降到地面（应该成功）
        game.moveWorkerAuto(2, 2, 2, 3);
        
        // 验证下降成功
        assertTrue("工人应该能从3层下降到地面", board.hasWorker(2, 3));
        assertEquals("(2,3) 应该有玩家 A 的工人", "PlayerA", 
                    board.getCell(2, 3).getWorker().getPlayerName());
        assertFalse("工人应该已从 (2,2) 移除", board.hasWorker(2, 2));
    }

    // ========== 建造规则测试 ==========
    
    /**
     * 测试建造必须在相邻格子
     */
    @Test
    public void testBuildingMustBeAdjacent() throws Exception {
        // 移动工人到 (2,2)
        game.moveWorkerAuto(0, 0, 1, 0);
        game.buildAuto(1, 0, 1, 1, false);
        
        // 玩家 B 回合
        game.moveWorkerAuto(4, 4, 3, 4);
        game.buildAuto(3, 4, 3, 3, false);
        
        // 移动到测试位置
        game.moveWorkerAuto(1, 0, 2, 2);
        
        // 测试在相邻格子建造（应该成功）
        game.buildAuto(2, 2, 2, 3, false);
        assertEquals("相邻格子建造应该成功", 1, board.getCell(2, 3).getTowerLevel());
        
        // 完成回合
        game.moveWorkerAuto(3, 4, 4, 4);
        game.buildAuto(4, 4, 4, 3, false);
        
        // 测试在非相邻格子建造（应该失败）
        game.moveWorkerAuto(2, 2, 2, 1);
        try {
            game.buildAuto(2, 1, 4, 1, false); // 距离太远
            fail("不应该能在非相邻格子建造");
        } catch (Exception e) {
            assertEquals("应该提示只能在相邻格子建造", 
                        "can only build in adjacent cell", e.getMessage());
        }
    }
    
    /**
     * 测试建造层数递增
     */
    @Test
    public void testBuildingLevelProgression() throws Exception {
        // 移动工人到建造位置
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // 在同一格子连续建造，测试层数递增
        Cell targetCell = board.getCell(1, 1);
        
        // 第一次建造：0 -> 1
        assertEquals("初始应该是地面", 0, targetCell.getTowerLevel());
        game.buildAuto(1, 0, 1, 1, false);
        assertEquals("第一次建造后应该是1层", 1, targetCell.getTowerLevel());
        
        // 玩家 B 回合
        game.moveWorkerAuto(4, 4, 3, 4);
        game.buildAuto(3, 4, 1, 1, false); // 继续在同一格子建造
        assertEquals("第二次建造后应该是2层", 2, targetCell.getTowerLevel());
        
        // 玩家 A 回合
        game.moveWorkerAuto(1, 0, 0, 0);
        game.buildAuto(0, 0, 1, 1, false); // 继续建造
        assertEquals("第三次建造后应该是3层", 3, targetCell.getTowerLevel());
        
        // 玩家 B 回合
        game.moveWorkerAuto(3, 4, 4, 4);
        game.buildAuto(4, 4, 1, 1, true); // 建造圆顶
        assertEquals("建造圆顶后应该是4层", 4, targetCell.getTowerLevel());
        assertTrue("应该有圆顶", targetCell.getTower().hasDome());
    }
    
    /**
     * 测试不能在有工人的格子建造
     */
    @Test
    public void testCannotBuildOnOccupiedCell() throws Exception {
        // 移动工人
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // 尝试在有工人的格子建造
        try {
            game.buildAuto(1, 0, 0, 1, false); // (0,1) 有玩家 A 的另一个工人
            fail("不应该能在有工人的格子建造");
        } catch (Exception e) {
            assertEquals("应该提示不能在有工人的位置建造", 
                        "Cannot build because the distinated location alreay has a worker", 
                        e.getMessage());
        }
    }
    
    /**
     * 测试建造圆顶的特殊规则
     */
    @Test
    public void testDomeBuilding() throws Exception {
        // 创建3层塔
        board.getCell(1, 1).setTower(new Tower(3));
        
        // 移动工人到相邻位置
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // 在3层塔上建造圆顶
        game.buildAuto(1, 0, 1, 1, true);
        
        // 验证圆顶建造成功
        assertEquals("应该是4层（包含圆顶）", 4, board.getCell(1, 1).getTowerLevel());
        assertTrue("应该有圆顶", board.getCell(1, 1).getTower().hasDome());
        
        // 完成回合
        game.moveWorkerAuto(4, 4, 3, 4);
        game.buildAuto(3, 4, 3, 3, false);
        
        // 测试不能在圆顶上继续建造
        game.moveWorkerAuto(1, 0, 2, 0);
        try {
            game.buildAuto(2, 0, 1, 1, false);
            fail("不应该能在圆顶上继续建造");
        } catch (Exception e) {
            assertTrue("应该提示不能在圆顶上建造", 
                      e.getMessage().contains("dome") || 
                      e.getMessage().contains("Cannot build"));
        }
    }

    // ========== 边界条件测试 ==========
    
    /**
     * 测试棋盘边界移动
     */
    @Test
    public void testBoardBoundaryMovement() throws Exception {
        // 将工人移动到边界位置 (0,0)
        // 工人已经在 (0,0)，测试边界移动
        
        // 测试向边界外移动（应该失败）
        try {
            game.moveWorkerAuto(0, 0, -1, 0);
            fail("不应该能移动到棋盘外");
        } catch (Exception e) {
            assertEquals("应该提示移动出界", 
                        "make this move will take the worker out of the board", 
                        e.getMessage());
        }
        
        try {
            game.moveWorkerAuto(0, 0, 0, -1);
            fail("不应该能移动到棋盘外");
        } catch (Exception e) {
            assertEquals("应该提示移动出界", 
                        "make this move will take the worker out of the board", 
                        e.getMessage());
        }
        
        // 测试有效的边界移动
        game.moveWorkerAuto(0, 0, 0, 1); // 向右移动（有效）
        assertTrue("应该能在边界内移动", board.hasWorker(0, 1));
    }
    
    /**
     * 测试棋盘边界建造
     */
    @Test
    public void testBoardBoundaryBuilding() throws Exception {
        // 移动工人到边界
        game.moveWorkerAuto(0, 0, 0, 1);
        
        // 测试向边界外建造（应该失败）
        try {
            game.buildAuto(0, 1, -1, 1, false);
            fail("不应该能在棋盘外建造");
        } catch (Exception e) {
            assertEquals("应该提示建造出界", 
                        "can not build when location is out of the board", 
                        e.getMessage());
        }
        
        try {
            game.buildAuto(0, 1, 0, -1, false);
            fail("不应该能在棋盘外建造");
        } catch (Exception e) {
            assertEquals("应该提示建造出界", 
                        "can not build when location is out of the board", 
                        e.getMessage());
        }
        
        // 测试有效的边界建造
        game.buildAuto(0, 1, 1, 1, false); // 在边界内建造（有效）
        assertEquals("应该能在边界内建造", 1, board.getCell(1, 1).getTowerLevel());
    }

    // ========== 辅助方法 ==========
    
    /**
     * 设置游戏到 PLAY 阶段
     */
    private void setupGameToPlayPhase() throws Exception {
        game.placeWorkerAuto(0, 0); // 玩家 A 工人 1
        game.placeWorkerAuto(0, 1); // 玩家 A 工人 2
        game.placeWorkerAuto(4, 4); // 玩家 B 工人 1
        game.placeWorkerAuto(4, 3); // 玩家 B 工人 2
        
        assertEquals("应该进入 PLAY 阶段", 1, game.getGameStatus());
        assertEquals("应该轮到玩家 A", 0, game.getCurPlayer());
    }
}