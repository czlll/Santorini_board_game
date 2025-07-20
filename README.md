# Santorini Game Implementation

A complete implementation of the Santorini board game with a web interface.

## Features
- Core game logic implementation in Java
- Spring Boot web application
- RESTful API for game interactions
- Web interface with HTML, CSS, and JavaScript
- Game board visualization
- Player turns and game state management
- Win condition detection

## Technical Details
- Java 17
- Spring Boot 2.7.3
- JUnit tests for core game logic
- GitHub Actions workflow for CI/CD

## Project Structure
- `src/main/java/edu/cmu/cs214/santorini/` - Core game logic
- `src/main/java/edu/cmu/cs214/santorini/controller/` - REST API controllers
- `src/main/resources/templates/` - HTML templates
- `src/main/resources/static/` - Static resources (CSS, JavaScript)
- `src/test/java/edu/cmu/cs214/santorini/` - Unit tests

## How to Run
1. Clone the repository
2. Run `mvn clean package` to build the project
3. Run `java -jar target/santorini-1.0-SNAPSHOT.jar` to start the application
4. Open a web browser and navigate to `http://localhost:12000`

## Game Rules
Santorini is a strategy board game where players move workers around a 5x5 grid, building towers and trying to climb to the third level to win.

### Setup
- Each player places two workers on the board
- Players take turns moving and building

### Gameplay
1. Select a worker to move
2. Move the selected worker to an adjacent space (including diagonals)
3. Build a level on an adjacent space to the moved worker
4. The first player to move a worker to the third level wins
5. If a player cannot move any of their workers, they lose

## Screenshots
The game interface shows a 5x5 grid representing the Santorini board. Each cell displays:
- The current height (0-3)
- Workers (shown with player color)
- Domes (shown in blue)

## Implementation Details
- The game uses a Model-View-Controller architecture
- The core game logic is implemented in Java
- The web interface uses Spring Boot with Thymeleaf templates
- The frontend uses vanilla JavaScript for game interactions
- RESTful API endpoints handle game state and player actions