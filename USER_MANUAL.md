# Santorini God Card Game - User Manual

## Overview

This is an enhanced version of the classic Santorini board game with God Card abilities. Each player selects a God Card that grants special powers during the game.

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Game

1. **Clone the repository:**
   ```bash
   git clone https://github.com/czlll/Santorini_board_game.git
   cd Santorini_board_game/santorini
   ```

2. **Start the server:**
   ```bash
   mvn spring-boot:run
   ```

3. **Open your browser:**
   Navigate to `http://localhost:8080`

## How to Play

### 1. God Card Selection
- When the game starts, a modal will appear asking Player A to select a God Card
- Choose from 4 available cards: **Demeter**, **Hephaestus**, **Minotaur**, or **Pan**
- After Player A selects, Player B will choose from the remaining cards
- Each card has unique abilities described below

### 2. Worker Placement (Setup Phase)
- Players take turns placing their 2 workers on the board
- Click on any empty cell to place a worker
- Player A places first, then Player B alternates

### 3. Game Play
Each turn consists of:
1. **Select Worker**: Choose which of your workers to move
2. **Move**: Click an adjacent cell to move your worker
3. **Build**: Click an adjacent cell to build a block

### 4. Winning Conditions
- **Standard Win**: Move any worker from level 2 to level 3
- **Pan Special Win**: Move any worker down 2+ levels in one move

## God Card Abilities

### 🏗️ **Demeter** - Goddess of Harvest
- **Ability**: After building, may build one additional block
- **Restriction**: Second build must be on a different space
- **Usage**: After your normal build, "Skip Additional Build" button appears if you want to build again

### 🔨 **Hephaestus** - God of Blacksmiths  
- **Ability**: May build one additional block on the same space
- **Restriction**: Cannot build two domes; max height is 3 blocks + dome
- **Usage**: After building, can add another block on the same cell

### 🐂 **Minotaur** - The Bull of Crete
- **Ability**: May move into opponent's space by pushing them backwards
- **Restriction**: Pushed opponent must land on valid, empty space
- **Usage**: Move normally - if target has opponent worker, they'll be pushed if possible

### 🐐 **Pan** - God of the Wild
- **Ability**: Also wins by moving down 2+ levels
- **Restriction**: Must be a single move (e.g., level 3 → level 1)
- **Usage**: Passive ability - automatic win detection

## Game Controls

### Main Interface
- **Game Board**: 5×5 grid showing building levels (0-3) and workers (A1, A2, B1, B2)
- **Game State**: Shows current phase (CARD_SELECTION, SETUP, PLAYING, etc.)
- **Current Player**: Indicates whose turn it is
- **Reset Game**: Starts a new game with fresh card selection

### During Play
- **Worker Selection**: Buttons appear to choose which worker to move
- **Additional Build Controls**: "Skip Additional Build" button for Demeter/Hephaestus abilities
- **Error Messages**: Red alerts show invalid moves or rule violations

## Game Rules

### Movement Rules
- Workers can move to any adjacent cell (including diagonally)
- Cannot move up more than 1 level
- Cannot move to occupied cells (except Minotaur's push ability)
- Cannot move to cells with domes

### Building Rules
- Must build on adjacent cell after moving
- Can build blocks (levels 1-3) or domes (on level 3)
- Cannot build on occupied cells
- Cannot build on cells with domes

### God Card Integration
- God abilities trigger automatically when conditions are met
- Additional build options appear as buttons when available
- All standard rules still apply unless specifically overridden by God power

## Troubleshooting

### Common Issues

**Modal not appearing:**
- Refresh the page
- Check browser console for JavaScript errors
- Ensure server is running on correct port

**Game not responding:**
- Check network connection
- Verify server is running (`mvn spring-boot:run`)
- Try resetting the game

**Invalid moves:**
- Read error messages carefully
- Ensure you're following both standard rules and God card restrictions
- Remember workers cannot climb more than 1 level

### Technical Support
- Check server logs for backend errors
- Use browser developer tools to inspect network requests
- Verify all game state transitions in the API responses

## API Endpoints (for developers)

- `GET /api/game/state` - Get current game state
- `GET /api/game/cards` - Get available God Cards
- `POST /api/game/assign-card` - Assign God Card to player
- `POST /api/game/place-worker` - Place worker during setup
- `POST /api/game/move` - Move worker
- `POST /api/game/build` - Build block
- `POST /api/game/skip-build` - Skip additional build
- `POST /api/game/reset` - Reset game

## Development Notes

The God Card system is designed to be extensible. New cards can be added by:
1. Implementing the `GodCard` interface
2. Registering the card in `GodCardRegistry`
3. Adding frontend descriptions in the card list

No modifications to core game logic are required for new cards.