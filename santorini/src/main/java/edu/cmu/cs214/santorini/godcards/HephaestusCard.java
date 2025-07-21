package edu.cmu.cs214.santorini.godcards;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.Worker;

/**
 * Hephaestus God Card implementation.
 * 
 * Ability: After building, the worker may build one additional block (not dome) 
 * on the same space.
 */
public class HephaestusCard implements GodCard {
    
    @Override
    public String getName() {
        return "Hephaestus";
    }
    
    @Override
    public String getDescription() {
        return "Your Worker may build one additional block (not dome) on top of your first block.";
    }
    
    @Override
    public BuildResult onAfterBuild(Board board, Worker worker, Position target, boolean isDome) {
        // Hephaestus allows an additional build on the same position
        // but only if a block (not dome) was built and the height allows it
        if (!isDome && board.getHeight(target) < 3) {
            return BuildResult.ADDITIONAL_BUILD_SAME;
        }
        return BuildResult.NONE;
    }
}