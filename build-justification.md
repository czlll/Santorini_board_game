# Build Action Justification

## Responsibility Assignment for Build Action

### Actions Required for Building
1. **Validate the build request**: Check if the build position is valid (adjacent to the worker, unoccupied, no dome).
2. **Determine the type of build**: Decide whether to build a block or a dome based on the current height and request.
3. **Update the board state**: Increase the height or add a dome at the target position.
4. **Update the game state**: Transition from BUILD to MOVE state and switch to the next player.

### Responsibility Assignment

#### Worker Class
The `Worker` class is responsible for the high-level build action. It:
- Knows its own position
- Delegates validation to the Board
- Calls the appropriate build method on the Board

#### Board Class
The `Board` class is responsible for:
- Validating if a build is possible at a given position
- Maintaining the state of the board (heights, domes, occupation)
- Performing the actual build operation (adding a block or dome)

#### Game Class
The `Game` class is responsible for:
- Managing the game flow
- Ensuring builds only happen during the BUILD phase
- Transitioning game state after a successful build
- Switching to the next player after a build

### Justification

This design follows several key design principles:

1. **Information Expert**: The Board class has the information needed to validate and perform builds (knowledge of heights, domes, and occupation).

2. **Low Coupling**: The Worker doesn't need to know the internal details of how the Board stores heights or domes, it just delegates to the Board's methods.

3. **High Cohesion**: Each class has a clear, focused set of responsibilities:
   - Worker: Represents a player's piece on the board
   - Board: Manages the physical state of the game
   - Game: Manages the overall game flow

4. **Single Responsibility Principle**: The Board is solely responsible for the physical state of the game, while the Game manages the rules and flow.

### Alternatives Considered

1. **Game performs all build logic**: This would violate the Information Expert principle, as the Game would need to know too many details about the Board's internal state.

2. **Worker performs all build logic**: This would require the Worker to have direct access to the Board's internal state, increasing coupling.

3. **Separate BuildManager class**: This would add unnecessary complexity for what is a relatively simple operation.

## Object-Level Interaction Diagram for Build Action

```
User -> Game: build(position, isDome)
  Game -> Game: check if in BUILD state
  Game -> Worker: build(board, position, isDome)
    Worker -> Board: canBuild(workerPosition, buildPosition)
      Board -> Position: isAdjacentTo(other)
      Position --> Board: result
      Board -> Board: check if position is occupied or has dome
    Board --> Worker: result
    Worker -> Board: buildBlock(position) or buildDome(position)
      Board -> Board: update heights or set dome
    Board --> Worker: result
  Worker --> Game: result
  Game -> Game: update game state and switch player
Game --> User: result
```

This interaction diagram shows how the build action flows through the system, with each class handling its specific responsibilities while maintaining clear boundaries between components.