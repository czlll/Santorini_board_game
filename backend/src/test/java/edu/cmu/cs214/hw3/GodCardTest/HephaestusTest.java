package edu.cmu.cs214.hw3.GodCardTest;

import edu.cmu.cs214.hw3.game.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GodCard;
import edu.cmu.cs214.hw3.game.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * 测试Hephaestus神卡功能
 * 能力：在玩家完成一次合法建造后，可在同一格再放置一层方块
 * 限制：不能把圆顶叠两次；加第二层后最高不得超过3（即不能直接放圆顶）
 */
public class HephaestusTest {
    private Game game;

    @Before
    public void setGame() throws Exception {
        Player playerA = new Player("A");
        Player playerB = new Player("B");
        Board board = new Board();

        playerA.setGodCard(GodCard.Hephaestus);
        playerB.setGodCard(GodCard.Pan);

        game = new Game(board, playerA, playerB);

        int aWorkerAR = 0;
        int aWorkerAC = 0;

        int aWorkerBR = 1;
        int aWorkerBC = 1;

        game.placeWorkerAuto(aWorkerAR, aWorkerAC);
        game.placeWorkerAuto(aWorkerBR, aWorkerBC);

        int bWorkerAR = 2;
        int bWorkerAC = 2;

        int bWorkerBR = 3;
        int bWorkerBC = 3;

        game.placeWorkerAuto(bWorkerAR, bWorkerAC);
        game.placeWorkerAuto(bWorkerBR, bWorkerBC);

        assertEquals(playerA.getName(), game.getBoard().getCell(0, 0).getWorker().getPlayerName());
        assertEquals(playerA.getName(), game.getBoard().getCell(1, 1).getWorker().getPlayerName());

        assertEquals(playerB.getName(), game.getBoard().getCell(2, 2).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(3, 3).getWorker().getPlayerName());

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }

    /**
     * 测试Hephaestus的基本建造能力
     */
    @Test
    public void testHephaestusBuildTwice() throws Exception {
        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5, game.getCurPlayerAction(), 0);

        // 第一次建造
        game.buildAuto(0, 0, 0, 1, false);
        assertEquals(1, game.getBoard().getTowerLevel(0, 1));

        // 验证当前状态是Hephaestus的第二次建造阶段
        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5.5, game.getCurPlayerAction(), 0);

        // 在同一格子进行第二次建造
        game.buildAuto(0, 0, 0, 1, false);
        assertEquals(2, game.getBoard().getTowerLevel(0, 1));

        // 验证回合已切换到PlayerB
        assertEquals(1, game.getGameStatus());
        assertEquals(1, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }

    /**
     * 测试Hephaestus不能在不同格子进行第二次建造
     */
    @Test
    public void testHephaestusCannotBuildOnDifferentSpot() throws Exception {
        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5, game.getCurPlayerAction(), 0);

        // 第一次建造
        game.buildAuto(0, 0, 0, 1, false);
        assertEquals(1, game.getBoard().getTowerLevel(0, 1));

        try {
            // 尝试在不同格子进行第二次建造
            game.buildAuto(0, 0, 1, 0, false);
            fail("应该抛出异常，因为Hephaestus必须在同一格子进行第二次建造");
        } catch (Exception e) {
            // 预期会抛出异常
            assertEquals("Hephaestus must build on the same space for the second build", e.getMessage());
        }
    }

    /**
     * 测试Hephaestus不能在第二次建造中放置圆顶
     */
    @Test
    public void testHephaestusCannotBuildDomeOnSecondBuild() throws Exception {
        game.skipActionAdmin();

        // 在(0,1)建造两层
        game.getBoard().getCell(0, 1).getTower().build(false);
        game.getBoard().getCell(0, 1).getTower().build(false);
        assertEquals(2, game.getBoard().getTowerLevel(0, 1));

        // 第一次建造
        game.buildAuto(0, 0, 0, 1, false);
        assertEquals(3, game.getBoard().getTowerLevel(0, 1));

        try {
            // 尝试在第二次建造中放置圆顶
            game.buildAuto(0, 0, 0, 1, true);
            fail("应该抛出异常，因为Hephaestus不能在第二次建造中放置圆顶");
        } catch (Exception e) {
            // 预期会抛出异常
            assertEquals("Cannot build a dome with Hephaestus second build", e.getMessage());
        }
    }

    /**
     * 测试Hephaestus不能在第一次建造圆顶后进行第二次建造
     */
    @Test
    public void testHephaestusCannotBuildAfterDome() throws Exception {
        game.skipActionAdmin();

        // 在(0,1)建造三层
        game.getBoard().getCell(0, 1).getTower().build(false);
        game.getBoard().getCell(0, 1).getTower().build(false);
        game.getBoard().getCell(0, 1).getTower().build(false);
        assertEquals(3, game.getBoard().getTowerLevel(0, 1));

        // 第一次建造放置圆顶
        game.buildAuto(0, 0, 0, 1, true);
        assertEquals(true, game.getBoard().getCell(0, 1).getTower().hasDome());

        try {
            // 尝试在圆顶上进行第二次建造
            game.buildAuto(0, 0, 0, 1, false);
            fail("应该抛出异常，因为不能在圆顶上建造");
        } catch (Exception e) {
            // 预期会抛出异常
            assertEquals("Cannot build on top of a dome", e.getMessage());
        }
    }

    /**
     * 测试Hephaestus不能建造超过3层
     */
    @Test
    public void testHephaestusCannotBuildBeyondLevel3() throws Exception {
        game.skipActionAdmin();

        // 在(0,1)建造两层
        game.getBoard().getCell(0, 1).getTower().build(false);
        game.getBoard().getCell(0, 1).getTower().build(false);
        assertEquals(2, game.getBoard().getTowerLevel(0, 1));

        // 第一次建造
        game.buildAuto(0, 0, 0, 1, false);
        assertEquals(3, game.getBoard().getTowerLevel(0, 1));

        try {
            // 尝试建造超过3层
            game.buildAuto(0, 0, 0, 1, false);
            fail("应该抛出异常，因为Hephaestus不能建造超过3层");
        } catch (Exception e) {
            // 预期会抛出异常
            assertEquals("Cannot build beyond level 3 with Hephaestus power", e.getMessage());
        }
    }

    /**
     * 测试跳过Hephaestus的第二次建造
     */
    @Test
    public void testSkipHephaestusSecondBuild() throws Exception {
        game.skipActionAdmin();

        // 第一次建造
        game.buildAuto(0, 0, 0, 1, false);
        assertEquals(1, game.getBoard().getTowerLevel(0, 1));

        // 跳过第二次建造
        game.skipAction();

        // 验证回合已切换到PlayerB
        assertEquals(1, game.getGameStatus());
        assertEquals(1, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }
}