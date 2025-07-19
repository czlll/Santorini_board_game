# State Justification

## State Storage Decisions

### Players
**Where stored:** In the `Game` class as a list of `Player` objects.

**Justification:** The `Game` class is responsible for managing the overall game state, including which players are participating. Storing players in the `Game` class follows the Information Expert principle as the game needs to know about all players to manage turns and determine the winner.

**Alternatives considered:** 
1. Storing players in a separate `PlayerManager` class - This would add an unnecessary layer of indirection and violate the principle of low representational gap.
2. Storing only player IDs in the game and players elsewhere - This would require additional lookups and complicate the code without clear benefits.

### Current Player
**Where stored:** In the `Game` class as a reference to the current `Player` object.

**Justification:** The `Game` class is responsible for turn management, so it needs to track which player is currently active. This follows the Information Expert principle as the game has the knowledge needed to manage turns.

**Alternatives considered:**
1. Using an index into the player list - This would be less direct and require additional lookups.
2. Having a boolean flag in each Player object - This would distribute state that should be centralized and make it harder to ensure only one player is active.

### Worker Locations
**Where stored:** Each `Worker` object stores its own location (position on the board), and each `Player` object has references to its workers.

**Justification:** This follows the principle of information hiding and encapsulation. Workers naturally "know" their own position, and players naturally "own" their workers. This design also supports the principle of low coupling as the board doesn't need to track worker positions separately.

**Alternatives considered:**
1. Storing worker positions in the `Board` class - This would create bidirectional dependencies between Board and Worker classes.
2. Storing worker positions in the `Game` class - This would violate encapsulation and make the Game class too complex.

### Towers
**Where stored:** The `Board` class stores the tower heights for each cell in a 2D grid structure.

**Justification:** The board is the natural owner of information about the physical state of the playing field. This follows the Information Expert principle as the board has the knowledge needed to manage the physical state of the game.

**Alternatives considered:**
1. Storing tower information in separate `Tower` objects - This would add unnecessary complexity for what is essentially just a height value.
2. Storing tower information in the `Game` class - This would violate separation of concerns and make the Game class too complex.

### Winner
**Where stored:** In the `Game` class as a reference to the winning `Player` (null if no winner yet).

**Justification:** The game is responsible for determining when the game ends and who wins. This follows the Information Expert principle as the game has the knowledge needed to determine the winner based on the rules.

**Alternatives considered:**
1. Using a boolean flag in each Player object - This would distribute state that should be centralized and make it harder to ensure only one player is marked as winner.
2. Creating a separate GameResult class - This would be an unnecessary abstraction for the simple win condition in Santorini.

## Design Principles Applied

1. **Information Expert**: State is assigned to the class with the information needed to maintain it.
2. **Low Coupling**: The design minimizes dependencies between classes.
3. **High Cohesion**: Each class has a clear, focused set of responsibilities.
4. **Encapsulation**: State is hidden within appropriate classes and accessed through methods.
5. **Single Responsibility Principle**: Each class has a single reason to change.