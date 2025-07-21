package edu.cmu.cs214.santorini.godcards;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Player;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.Worker;

/**
 * Interface for God Cards in Santorini.
 * Each God Card provides special abilities that modify the standard game rules.
 */
public interface GodCard {
    
    /**
     * Gets the name of this God Card.
     * @return the card name
     */
    String getName();
    
    /**
     * Gets the description of this God Card's ability.
     * @return the card description
     */
    String getDescription();
    
    /**
     * Called before a worker attempts to move.
     * Can modify movement rules or provide alternative movement options.
     * 
     * @param board the game board
     * @param worker the worker attempting to move
     * @param target the target position
     * @return true if the move should be allowed, false otherwise
     */
    default boolean canMove(Board board, Worker worker, Position target) {
        return board.canMove(worker.getPosition(), target);
    }
    
    /**
     * Called after a worker successfully moves.
     * Can trigger special abilities or check for alternative win conditions.
     * 
     * @param board the game board
     * @param worker the worker that moved
     * @param from the previous position
     * @param to the new position
     * @return true if the game should end (player wins), false otherwise
     */
    default boolean onAfterMove(Board board, Worker worker, Position from, Position to) {
        return false;
    }
    
    /**
     * Called before a worker attempts to build.
     * Can modify building rules.
     * 
     * @param board the game board
     * @param worker the worker attempting to build
     * @param target the target position
     * @param isDome whether building a dome
     * @return true if the build should be allowed, false otherwise
     */
    default boolean canBuild(Board board, Worker worker, Position target, boolean isDome) {
        return board.canBuild(worker.getPosition(), target);
    }
    
    /**
     * Called after a worker successfully builds.
     * Can trigger special abilities like additional builds.
     * 
     * @param board the game board
     * @param worker the worker that built
     * @param target the position where building occurred
     * @param isDome whether a dome was built
     * @return a BuildResult indicating if additional actions are available
     */
    default BuildResult onAfterBuild(Board board, Worker worker, Position target, boolean isDome) {
        return BuildResult.NONE;
    }
    
    /**
     * Represents the result of a build action for God Card abilities.
     */
    enum BuildResult {
        NONE,                    // No additional action
        ADDITIONAL_BUILD_SAME,   // Can build again on the same position (Hephaestus)
        ADDITIONAL_BUILD_DIFF    // Can build again on a different position (Demeter)
    }
}