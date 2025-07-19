document.addEventListener('DOMContentLoaded', () => {
    // Game state
    let gameState = {
        state: 'SETUP',
        currentPlayer: 'Player A',
        board: [],
        selectedWorker: null,
        isGameOver: false
    };

    // DOM elements
    const gameBoard = document.getElementById('game-board');
    const stateElement = document.getElementById('state');
    const playerElement = document.getElementById('player');
    const messageBox = document.getElementById('message-box');
    const resetButton = document.getElementById('reset-btn');
    const workerSelection = document.getElementById('worker-selection');
    const worker0Button = document.getElementById('worker-0');
    const worker1Button = document.getElementById('worker-1');

    // Initialize the game
    initGame();

    // Event listeners
    resetButton.addEventListener('click', resetGame);
    worker0Button.addEventListener('click', () => selectWorker(0));
    worker1Button.addEventListener('click', () => selectWorker(1));

    // Initialize the game
    function initGame() {
        fetchGameState();
    }

    // Fetch game state from the server
    function fetchGameState() {
        fetch('/api/game/state')
            .then(response => response.json())
            .then(data => {
                updateGameState(data);
                renderBoard();
                updateUI();
            })
            .catch(error => {
                showMessage(`Error fetching game state: ${error}`, 'error');
            });
    }

    // Update the game state with data from the server
    function updateGameState(data) {
        gameState = data;
    }

    // Render the game board
    function renderBoard() {
        gameBoard.innerHTML = '';
        
        if (!gameState.board || !Array.isArray(gameState.board)) {
            showMessage('Invalid board data', 'error');
            return;
        }

        for (let y = 0; y < 5; y++) {
            for (let x = 0; x < 5; x++) {
                const cell = document.createElement('div');
                cell.className = 'cell';
                cell.dataset.x = x;
                cell.dataset.y = y;
                
                const cellData = gameState.board[x][y];
                
                // Add height indicator
                const heightIndicator = document.createElement('div');
                heightIndicator.className = 'height-indicator';
                heightIndicator.textContent = cellData.height;
                cell.appendChild(heightIndicator);
                
                // Add cell content (worker or dome)
                const cellContent = document.createElement('div');
                cellContent.className = 'cell-content';
                
                if (cellData.hasDome) {
                    const dome = document.createElement('div');
                    dome.className = 'dome';
                    cellContent.appendChild(dome);
                } else if (cellData.isOccupied) {
                    const worker = document.createElement('div');
                    worker.className = `worker player-${cellData.playerId}`;
                    worker.textContent = `W${cellData.workerId + 1}`;
                    cellContent.appendChild(worker);
                }
                
                cell.appendChild(cellContent);
                
                // Add click event
                cell.addEventListener('click', () => handleCellClick(x, y));
                
                gameBoard.appendChild(cell);
            }
        }
    }

    // Update the UI based on the game state
    function updateUI() {
        stateElement.textContent = gameState.state;
        playerElement.textContent = gameState.currentPlayer || '';
        
        // Show/hide worker selection buttons based on game state
        if (gameState.state === 'SELECT_WORKER') {
            workerSelection.classList.remove('hidden');
        } else {
            workerSelection.classList.add('hidden');
        }
        
        // Show game over message
        if (gameState.isGameOver) {
            showMessage(`Game Over! ${gameState.winner} wins!`, 'success');
        }
    }

    // Handle cell click based on the current game state
    function handleCellClick(x, y) {
        if (gameState.isGameOver) {
            showMessage('Game is over. Click Reset to play again.', 'error');
            return;
        }
        
        // Check if there's a worker at this position
        const cell = gameState.board[y][x];
        if (gameState.state === 'MOVE' && cell.playerId !== undefined && cell.workerId !== undefined) {
            // If the cell has a worker and it's the current player's worker, select it
            const currentPlayerId = gameState.currentPlayer === 'Player A' ? 0 : 1;
            if (cell.playerId === currentPlayerId) {
                selectWorker(cell.workerId);
                return;
            }
        }
        
        switch (gameState.state) {
            case 'SETUP':
                placeWorker(x, y);
                break;
            case 'MOVE':
                moveWorker(x, y);
                break;
            case 'BUILD':
                buildBlock(x, y);
                break;
            default:
                showMessage('Invalid action for current game state', 'error');
        }
    }

    // Place a worker on the board
    function placeWorker(x, y) {
        const playerId = gameState.currentPlayer === 'Player A' ? 0 : 1;
        
        fetch(`/api/game/place?playerId=${playerId}&x=${x}&y=${y}`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateGameState(data);
                renderBoard();
                updateUI();
            } else {
                showMessage('Invalid worker placement', 'error');
            }
        })
        .catch(error => {
            showMessage(`Error placing worker: ${error}`, 'error');
        });
    }

    // Select a worker
    function selectWorker(workerId) {
        fetch(`/api/game/select?workerId=${workerId}`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateGameState(data);
                renderBoard();
                updateUI();
                showMessage(`Worker ${workerId + 1} selected`, 'success');
            } else {
                showMessage('Invalid worker selection', 'error');
            }
        })
        .catch(error => {
            showMessage(`Error selecting worker: ${error}`, 'error');
        });
    }

    // Move a worker
    function moveWorker(x, y) {
        fetch(`/api/game/move?x=${x}&y=${y}`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateGameState(data);
                renderBoard();
                updateUI();
                
                if (data.isGameOver) {
                    showMessage(`Game Over! ${data.winner} wins!`, 'success');
                }
            } else {
                showMessage('Invalid move', 'error');
            }
        })
        .catch(error => {
            showMessage(`Error moving worker: ${error}`, 'error');
        });
    }

    // Build a block or dome
    function buildBlock(x, y) {
        // For simplicity, we'll always build a block, not a dome
        fetch(`/api/game/build?x=${x}&y=${y}&dome=false`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateGameState(data);
                renderBoard();
                updateUI();
            } else {
                showMessage('Invalid build', 'error');
            }
        })
        .catch(error => {
            showMessage(`Error building: ${error}`, 'error');
        });
    }

    // Reset the game
    function resetGame() {
        fetch('/api/game/reset', {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            updateGameState(data);
            renderBoard();
            updateUI();
            showMessage('Game reset', 'success');
        })
        .catch(error => {
            showMessage(`Error resetting game: ${error}`, 'error');
        });
    }

    // Show a message in the message box
    function showMessage(message, type = '') {
        messageBox.textContent = message;
        messageBox.className = 'message';
        if (type) {
            messageBox.classList.add(type);
        }
    }
});