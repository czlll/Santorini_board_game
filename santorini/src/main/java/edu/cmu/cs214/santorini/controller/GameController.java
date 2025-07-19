package edu.cmu.cs214.santorini.controller;

import edu.cmu.cs214.santorini.Game;
import edu.cmu.cs214.santorini.Player;
import edu.cmu.cs214.santorini.Position;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private Game game;

    public GameController() {
        resetGame();
    }

    @GetMapping("/state")
    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("state", game.getState().toString());
        state.put("currentPlayer", game.getCurrentPlayer() != null ? game.getCurrentPlayer().getName() : null);
        state.put("winner", game.getWinner() != null ? game.getWinner().getName() : null);
        state.put("board", getBoardState());
        state.put("isGameOver", game.isGameOver());
        return state;
    }

    @PostMapping("/reset")
    public Map<String, Object> resetGame() {
        game = new Game();
        game.addPlayer(new Player("Player A"));
        game.addPlayer(new Player("Player B"));
        return getGameState();
    }

    @PostMapping("/place")
    public Map<String, Object> placeWorker(@RequestParam int playerId, @RequestParam int x, @RequestParam int y) {
        System.out.println("Placing worker for player " + playerId + " at position (" + x + ", " + y + ")");
        
        // In the game, workerIndex is 0 or 1, not playerId
        // We need to determine which worker to place based on the current state
        int workerIndex = 0;
        
        // Check if player already has a worker placed
        Player player = game.getPlayers().get(playerId);
        if (player.getWorkers().get(0).getPosition() != null) {
            workerIndex = 1;
        }
        
        boolean success = game.placeWorker(workerIndex, new Position(x, y));
        System.out.println("Worker placement success: " + success);
        
        Map<String, Object> response = getGameState();
        response.put("success", success);
        return response;
    }

    @PostMapping("/select")
    public Map<String, Object> selectWorker(@RequestParam int workerId) {
        boolean success = game.selectWorker(workerId);
        Map<String, Object> response = getGameState();
        response.put("success", success);
        return response;
    }

    @PostMapping("/move")
    public Map<String, Object> moveWorker(@RequestParam int x, @RequestParam int y) {
        boolean success = game.moveWorker(new Position(x, y));
        Map<String, Object> response = getGameState();
        response.put("success", success);
        return response;
    }

    @PostMapping("/build")
    public Map<String, Object> build(@RequestParam int x, @RequestParam int y, @RequestParam boolean dome) {
        boolean success = game.build(new Position(x, y), dome);
        Map<String, Object> response = getGameState();
        response.put("success", success);
        return response;
    }

    private Object[][] getBoardState() {
        Object[][] boardState = new Object[5][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                Position pos = new Position(x, y);
                Map<String, Object> cell = new HashMap<>();
                cell.put("height", game.getBoard().getHeight(pos));
                cell.put("hasDome", game.getBoard().hasDome(pos));
                cell.put("isOccupied", game.getBoard().isOccupied(pos));
                
                // Add worker information if the cell is occupied
                if (game.getBoard().isOccupied(pos)) {
                    for (int i = 0; i < 2; i++) {
                        Player player = game.getPlayers().get(i);
                        for (int j = 0; j < 2; j++) {
                            Position workerPos = player.getWorker(j).getPosition();
                            if (workerPos != null && workerPos.equals(pos)) {
                                cell.put("playerId", i);
                                cell.put("workerId", j);
                                break;
                            }
                        }
                    }
                }
                
                boardState[x][y] = cell;
            }
        }
        return boardState;
    }
}