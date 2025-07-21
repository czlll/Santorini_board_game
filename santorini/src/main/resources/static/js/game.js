document.addEventListener('DOMContentLoaded', () => {
    // Game state
    let gameState = {
        state: 'SETUP',
        currentPlayer: 'Player A',
        board: [],
        selectedWorker: null,
        isGameOver: false
    };
    
    let cardSelectionComplete = false;

    // DOM elements
    const gameBoard = document.getElementById('game-board');
    const stateElement = document.getElementById('state');
    const playerElement = document.getElementById('player');
    const messageBox = document.getElementById('message-box');
    const resetButton = document.getElementById('reset-btn');
    const workerSelection = document.getElementById('worker-selection');
    const worker0Button = document.getElementById('worker-0');
    const worker1Button = document.getElementById('worker-1');
    const additionalBuildControls = document.getElementById('additional-build-controls');
    const skipBuildButton = document.getElementById('skip-build-btn');
    const cardSelectionModal = document.getElementById('card-selection-modal');
    const cardSelectionPrompt = document.getElementById('card-selection-prompt');
    const cardList = document.getElementById('card-list');

    // God Card selection state
    let availableCards = [];
    let currentPlayerSelectingCard = 0;

    // Initialize the game
    initGame();

    // Event listeners
    resetButton.addEventListener('click', resetGame);
    worker0Button.addEventListener('click', () => selectWorker(0));
    worker1Button.addEventListener('click', () => selectWorker(1));
    skipBuildButton.addEventListener('click', skipAdditionalBuild);

    // Initialize the game
    function initGame() {
        fetchAvailableCards();
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
        
        // Show/hide controls based on game state
        if (gameState.state === 'MOVE') {
            workerSelection.classList.remove('hidden');
            additionalBuildControls.classList.add('hidden');
        } else if (gameState.state === 'ADDITIONAL_BUILD') {
            workerSelection.classList.add('hidden');
            additionalBuildControls.classList.remove('hidden');
        } else {
            workerSelection.classList.add('hidden');
            additionalBuildControls.classList.add('hidden');
        }
        
        // Show card selection modal if needed
        if (gameState.state === 'CARD_SELECTION' && !cardSelectionComplete) {
            // Check if we need to show the modal based on which players have cards
            const player0HasCard = gameState.godCards && gameState.godCards.player0;
            const player1HasCard = gameState.godCards && gameState.godCards.player1;
            
            if (!player0HasCard) {
                currentPlayerSelectingCard = 0;
                showCardSelectionModal();
            } else if (!player1HasCard) {
                currentPlayerSelectingCard = 1;
                showCardSelectionModal();
            } else {
                // Both players have cards, hide modal and mark as complete
                cardSelectionComplete = true;
                cardSelectionModal.style.display = 'none';
            }
        } else {
            // Not in card selection state or selection is complete, hide modal
            cardSelectionModal.style.display = 'none';
        }
        
        // Show game over message
        if (gameState.isGameOver) {
            showMessage(`Game Over! ${gameState.winner} wins!`, 'success');
        }
        
        // Display God Card information
        displayGodCardInfo();
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
            case 'ADDITIONAL_BUILD':
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
        cardSelectionComplete = false;
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

    // Fetch available God Cards
    function fetchAvailableCards() {
        fetch('/api/game/cards')
            .then(response => response.json())
            .then(data => {
                availableCards = Array.from(data.cards);
                renderCardList();
            })
            .catch(error => {
                showMessage(`Error fetching cards: ${error}`, 'error');
            });
    }

    // Render the card list in the modal
    function renderCardList() {
        cardList.innerHTML = '';
        
        // Create card descriptions
        const cardDescriptions = {
            'Demeter': 'Your Worker may build one additional time, but not on the same space.',
            'Hephaestus': 'Your Worker may build one additional block (not dome) on top of your first block.',
            'Minotaur': 'Your Worker may move into an opponent Worker\'s space, if their Worker can be forced one space straight backwards.',
            'Pan': 'You also win if your Worker moves down two or more levels.'
        };
        
        availableCards.forEach(cardName => {
            const cardItem = document.createElement('div');
            cardItem.className = 'card-item';
            cardItem.dataset.cardName = cardName;
            
            const cardNameElement = document.createElement('div');
            cardNameElement.className = 'card-name';
            cardNameElement.textContent = cardName;
            
            const cardDescElement = document.createElement('div');
            cardDescElement.className = 'card-description';
            cardDescElement.textContent = cardDescriptions[cardName] || 'No description available.';
            
            cardItem.appendChild(cardNameElement);
            cardItem.appendChild(cardDescElement);
            
            cardItem.addEventListener('click', () => selectCard(cardName));
            
            cardList.appendChild(cardItem);
        });
    }

    // Show the card selection modal
    function showCardSelectionModal() {
        const playerName = currentPlayerSelectingCard === 0 ? 'Player A' : 'Player B';
        cardSelectionPrompt.textContent = `${playerName}, choose your God Card:`;
        cardSelectionModal.style.display = 'flex';
    }

    // Select a God Card
    function selectCard(cardName) {
        fetch(`/api/game/assign-card?playerId=${currentPlayerSelectingCard}&cardName=${cardName}`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateGameState(data);
                updateUI();
                
                // Check if we need to switch to the next player for card selection
                if (data.state === 'CARD_SELECTION') {
                    currentPlayerSelectingCard = 1;
                    showCardSelectionModal();
                } else {
                    currentPlayerSelectingCard = 0;
                    cardSelectionComplete = true;
                    cardSelectionModal.style.display = 'none';
                }
            } else {
                showMessage('Failed to assign God Card', 'error');
            }
        })
        .catch(error => {
            showMessage(`Error assigning card: ${error}`, 'error');
        });
    }

    // Skip additional build
    function skipAdditionalBuild() {
        fetch('/api/game/skip-build', {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateGameState(data);
                renderBoard();
                updateUI();
                showMessage('Additional build skipped', 'success');
            } else {
                showMessage('Failed to skip build', 'error');
            }
        })
        .catch(error => {
            showMessage(`Error skipping build: ${error}`, 'error');
        });
    }

    // Display God Card information
    function displayGodCardInfo() {
        // This could be expanded to show God Card info in the UI
        // For now, it's handled by the game state display
    }
});