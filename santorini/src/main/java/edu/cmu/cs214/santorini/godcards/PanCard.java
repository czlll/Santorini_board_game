package edu.cmu.cs214.santorini.godcards;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.Worker;

/**
 * Pan God Card implementation.
 * 
 * Ability: You also win if your Worker moves down two or more levels.
 */
public class PanCard implements GodCard {
    
    @Override
    public String getName() {
        return "Pan";
    }
    
    @Override
    public String getDescription() {
        return "You also win if your Worker moves down two or more levels.";
    }
    
    @Override
    public boolean onAfterMove(Board board, Worker worker, Position from, Position to) {
        // Check if the worker moved down two or more levels
        int heightDifference = board.getHeight(from) - board.getHeight(to);
        return heightDifference >= 2;
    }
}