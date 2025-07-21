package edu.cmu.cs214.hw3.game.action.build;

import edu.cmu.cs214.hw3.game.Board;
import edu.cmu.cs214.hw3.game.Cell;

/**
 * Hephaestus build action implementation.
 * Allows building twice on the same space.
 */
public class HephaestusBuild extends BuildAction {
    private int lastBuildRow = -1;
    private int lastBuildCol = -1;
    private boolean secondBuild = false;

    @Override
    public boolean build(int workerRow, int workerCol, int buildRow, int buildCol, Board board, boolean isDome) throws Exception {
        // First build - use normal build rules
        if (!secondBuild) {
            boolean result = super.build(workerRow, workerCol, buildRow, buildCol, board, isDome);
            if (result) {
                // Store the location of the first build
                lastBuildRow = buildRow;
                lastBuildCol = buildCol;
                secondBuild = true;
            }
            return result;
        } 
        // Second build - must be on the same space as the first build
        else {
            // Check if building on the same space as the first build
            if (buildRow != lastBuildRow || buildCol != lastBuildCol) {
                throw new Exception("Hephaestus must build the second time on the same space");
            }

            Cell cell = board.getCell(buildRow, buildCol);
            
            // Check if trying to build a dome on a dome
            if (isDome && cell.getTower().hasDome()) {
                throw new Exception("Cannot build a dome on a dome");
            }
            
            // Check if building would exceed level 3 (can't build to level 4)
            if (cell.getTower().getLevel() >= 3 && !isDome) {
                throw new Exception("Cannot build beyond level 3");
            }

            // Perform the build
            cell.getTower().build(isDome);
            secondBuild = false; // Reset for next turn
            return true;
        }
    }

    @Override
    public void reset() {
        secondBuild = false;
        lastBuildRow = -1;
        lastBuildCol = -1;
    }

    @Override
    public boolean isSecondBuild() {
        return secondBuild;
    }

    @Override
    public void skipSecondBuild() {
        secondBuild = false;
    }
}