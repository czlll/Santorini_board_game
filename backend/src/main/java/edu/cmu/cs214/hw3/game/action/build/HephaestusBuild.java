package edu.cmu.cs214.hw3.game.action.build;

import edu.cmu.cs214.hw3.game.Board;

/**
 * Hephaestus神卡实现
 * 能力：在玩家完成一次合法建造后，可在同一格再放置一层方块
 * 限制：不能把圆顶叠两次；加第二层后最高不得超过3（即不能直接放圆顶）
 */
public class HephaestusBuild extends BuildAction {
    private int prevRow = -1;
    private int prevCol = -1;
    private boolean firstBuildIsDome = false;

    @Override
    public boolean validateBuild(int r, int c, int newR, int newC, String playerName, Board board, boolean isDome) throws Exception {
        boolean result = super.validateBuild(r, c, newR, newC, playerName, board, isDome);
        
        // 如果是第一次建造
        if (prevRow == -1 && prevCol == -1) {
            if (result) {
                prevRow = newR;
                prevCol = newC;
                firstBuildIsDome = isDome;
            }
            return result;
        } 
        // 如果是第二次建造
        else {
            // 检查是否在同一格
            if (newR == prevRow && newC == prevCol) {
                // 检查第一次是否建造了圆顶
                if (firstBuildIsDome) {
                    super.setOkToBuild(false);
                    throw new Exception("Cannot build on top of a dome");
                }
                
                // 检查是否尝试直接放圆顶
                if (isDome) {
                    super.setOkToBuild(false);
                    throw new Exception("Cannot build a dome with Hephaestus second build");
                }
                
                // 检查建造后是否会超过3层
                int currentLevel = board.getTowerLevel(newR, newC);
                if (currentLevel >= 3) {
                    super.setOkToBuild(false);
                    throw new Exception("Cannot build beyond level 3 with Hephaestus power");
                }
                
                return result;
            } else {
                super.setOkToBuild(false);
                throw new Exception("Hephaestus must build on the same space for the second build");
            }
        }
    }

    @Override
    public double nextAction(double curPlayerAction) {
        if (curPlayerAction == 5) {
            return 5.5;
        } else if (curPlayerAction == 5.5) {
            prevRow = -1;
            prevCol = -1;
            firstBuildIsDome = false;
            return 2;
        }
        return curPlayerAction;
    }

    @Override
    public int nextPlayer(double curPlayerAction, int curPlayer) {
        if (curPlayerAction == 2) {
            return (curPlayer + 1) % 2;
        } else {
            return curPlayer;
        }
    }
}