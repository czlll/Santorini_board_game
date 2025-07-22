package edu.cmu.cs214.santorini.godcards;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.Worker;

/**
 * Demeter God Card implementation.
 * 
 * Ability: After building, the worker may build again on a different space.
 * The second build cannot be a dome and must be on a different position.
 */
public class DemeterCard implements GodCard {
    
    @Override
    public String getName() {
        return "Demeter";
    }
    
    @Override
    public String getDescription() {
        return "Your Worker may build one additional time, but not on the same space.";
    }
    
    @Override
    public BuildResult onAfterBuild(Board board, Worker worker, Position target, boolean isDome) {
        // Demeter allows an additional build on a different position
        // but not if a dome was just built (since domes end the turn)
        if (!isDome) {
            return BuildResult.ADDITIONAL_BUILD_DIFF;
        }
        return BuildResult.NONE;
    }
}