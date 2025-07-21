package edu.cmu.cs214.santorini.godcards;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.Worker;
import edu.cmu.cs214.santorini.Player;

/**
 * Minotaur God Card implementation.
 * 
 * Ability: Your Worker may move into an opponent Worker's space, if their Worker 
 * can be forced one space straight backwards to an unoccupied space at any level.
 */
public class MinotaurCard implements GodCard {
    
    @Override
    public String getName() {
        return "Minotaur";
    }
    
    @Override
    public String getDescription() {
        return "Your Worker may move into an opponent Worker's space, if their Worker can be forced one space straight backwards.";
    }
    
    @Override
    public boolean canMove(Board board, Worker worker, Position target) {
        // Check basic movement constraints
        if (!worker.getPosition().isAdjacentTo(target)) {
            return false;
        }
        
        // Check if there's a dome on the target
        if (board.hasDome(target)) {
            return false;
        }
        
        // Check if we can climb to this position (height restriction still applies)
        int heightDifference = board.getHeight(target) - board.getHeight(worker.getPosition());
        if (heightDifference > 1) {
            return false;
        }
        
        // If target is not occupied, it's a normal move
        if (!board.isOccupied(target)) {
            return true;
        }
        
        // Target is occupied - check if we can push an opponent worker
        // Calculate the push position (one space backwards from target)
        Position pushPosition = calculatePushPosition(worker.getPosition(), target);
        if (pushPosition == null || !pushPosition.isWithinBounds()) {
            return false;
        }
        
        // Check if the push position is available (no worker, no dome)
        return !board.isOccupied(pushPosition) && !board.hasDome(pushPosition);
    }
    
    /**
     * Calculates the position where the opponent worker would be pushed.
     * 
     * @param from the position of the moving worker
     * @param to the position of the opponent worker
     * @return the push position, or null if invalid
     */
    private Position calculatePushPosition(Position from, Position to) {
        int deltaX = to.getX() - from.getX();
        int deltaY = to.getY() - from.getY();
        
        int pushX = to.getX() + deltaX;
        int pushY = to.getY() + deltaY;
        
        return new Position(pushX, pushY);
    }
}