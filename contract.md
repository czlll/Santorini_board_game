# Behavioral Contract: Move Worker

## Operation: moveWorker(position)

### Preconditions:
1. The game is in the MOVE state.
2. A worker has been selected by the current player.
3. The target position is within the bounds of the board (0-4 for both x and y coordinates).
4. The target position is adjacent to the selected worker's current position.
5. The target position is not occupied by another worker.
6. The target position does not have a dome.
7. The height difference between the target position and the worker's current position is at most 1 level (worker can climb at most 1 level).

### Postconditions:
1. The selected worker's position is updated to the target position.
2. The board's occupation state is updated:
   - The worker's previous position is marked as unoccupied.
   - The worker's new position is marked as occupied.
3. If the worker has moved to a level 3 tower:
   - The game state is changed to GAME_OVER.
   - The current player is set as the winner.
4. Otherwise:
   - The game state is changed to BUILD.
   - The selected worker remains selected for the subsequent build action.

### Example:
**Initial state:**
- Game state: MOVE
- Current player: Player A
- Selected worker: Worker A-1 at position (0, 0)
- Board state: 
  - Position (0, 0): Height 0, Occupied by Worker A-1
  - Position (1, 0): Height 0, Unoccupied
  - Position (1, 1): Height 1, Unoccupied

**Operation call:**
```
moveWorker(new Position(1, 0))
```

**Final state:**
- Game state: BUILD
- Current player: Player A
- Selected worker: Worker A-1 at position (1, 0)
- Board state: 
  - Position (0, 0): Height 0, Unoccupied
  - Position (1, 0): Height 0, Occupied by Worker A-1
  - Position (1, 1): Height 1, Unoccupied

**Win condition example:**
If Worker A-1 moves to a position with height 3, the postconditions would be:
- Game state: GAME_OVER
- Current player: Player A
- Winner: Player A
- Selected worker: Worker A-1 at the level 3 position
- Board state updated accordingly