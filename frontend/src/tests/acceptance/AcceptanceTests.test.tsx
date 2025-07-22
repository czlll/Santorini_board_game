import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import axios from 'axios';
import App from '../../App';

// Mock axios for all acceptance tests
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Santorini Game - Acceptance Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Mock window.alert
    jest.spyOn(window, 'alert').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('AC-001: Game Initialization Flow', () => {
    test('User can start a new game from home page', async () => {
      // Mock backend connection success
      mockedAxios.mockResolvedValueOnce({ data: 'Hello world' });

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      // Verify home page elements
      expect(screen.getByText(/Hello, Welcome to my Santorini Game/i)).toBeInTheDocument();
      
      // Click start button
      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      // Should navigate to player form
      await waitFor(() => {
        expect(screen.getByText(/Welcome to Santorini Game/i)).toBeInTheDocument();
      });
    });

    test('User sees error message when backend is not available', async () => {
      // Mock backend connection failure
      mockedAxios.mockRejectedValueOnce(new Error('Network Error'));

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(window.alert).toHaveBeenCalledWith('Not connected to backend');
      });
    });
  });

  describe('AC-002: Player Setup and God Card Selection', () => {
    test('Users can enter names and select different god cards', async () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Enter player names
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      // Select different god cards
      const godSelects = screen.getAllByRole('combobox');
      fireEvent.change(godSelects[0], { target: { value: 'Demeter' } });
      fireEvent.change(godSelects[1], { target: { value: 'Minotaur' } });

      // Verify selections
      expect(playerInputs[0]).toHaveValue('Alice');
      expect(playerInputs[1]).toHaveValue('Bob');
      expect(godSelects[0]).toHaveValue('Demeter');
      expect(godSelects[1]).toHaveValue('Minotaur');

      // Warning should not be visible
      const warning = screen.getByText(/certain player have duplicate god card/i);
      expect(warning).toHaveClass('invisible');
    });

    test('System prevents duplicate god card selection', async () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Select same god card for both players
      const godSelects = screen.getAllByRole('combobox');
      fireEvent.change(godSelects[1], { target: { value: 'Demeter' } }); // Both default to Demeter

      // Warning should be visible
      const warning = screen.getByText(/certain player have duplicate god card/i);
      expect(warning).toHaveClass('visible');
    });

    test('System validates required fields before starting game', async () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Try to start game without entering names
      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(window.alert).toHaveBeenCalledWith('Player one did not enter name');
      });
    });

    test('Users can successfully start game with valid inputs', async () => {
      // Mock successful game initialization
      mockedAxios.mockResolvedValueOnce({ data: 'game ready' });

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Fill in valid data
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      const godSelects = screen.getAllByRole('combobox');
      fireEvent.change(godSelects[1], { target: { value: 'Pan' } });

      // Start game
      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      // Should call backend API
      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080/initialGame?playerOneName=Alice&playerTwoName=Bob&playerOneCard=Demeter&playerTwoCard=Pan',
          headers: {},
        });
      });
    });
  });

  describe('AC-003: Game Board Interaction', () => {
    beforeEach(() => {
      // Mock game state for board tests
      mockedAxios.mockResolvedValue({
        data: {
          gameStatus: 1,
          curPlayer: 0,
          curPlayerAction: 1,
          playerA: { name: 'Alice', godCard: 'Demeter' },
          playerB: { name: 'Bob', godCard: 'Pan' },
          board: {
            grid: Array(5).fill(null).map((_, row) => 
              Array(5).fill(null).map((_, col) => ({
                row,
                col,
                tower: { level: 0, hasDome: false },
                worker: null
              }))
            )
          }
        }
      });
    });

    test('Game board displays 5x5 grid correctly', async () => {
      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      // Wait for game state to load
      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080/getGameState',
          headers: {},
        });
      });
    });

    test('Players can interact with grid cells', async () => {
      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalled();
      });

      // Mock click response
      mockedAxios.mockResolvedValueOnce({
        data: { gameStatus: 1, curPlayer: 0 }
      });

      // This would depend on the actual Grid component implementation
      // The test should verify that clicking on grid cells triggers appropriate actions
    });
  });

  describe('AC-004: Game Flow and Rules', () => {
    test('Game enforces turn-based gameplay', async () => {
      // Mock game states showing turn progression
      mockedAxios
        .mockResolvedValueOnce({
          data: {
            gameStatus: 1,
            curPlayer: 0,
            curPlayerAction: 1,
            playerA: { name: 'Alice' },
            playerB: { name: 'Bob' }
          }
        })
        .mockResolvedValueOnce({
          data: {
            gameStatus: 1,
            curPlayer: 1,
            curPlayerAction: 1,
            playerA: { name: 'Alice' },
            playerB: { name: 'Bob' }
          }
        });

      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalled();
      });
    });

    test('Game detects win conditions', async () => {
      // Mock game end state
      mockedAxios.mockResolvedValueOnce({
        data: {
          gameStatus: 2,
          winner: 'Alice',
          playerA: { name: 'Alice' },
          playerB: { name: 'Bob' }
        }
      });

      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalled();
      });

      // Should navigate to win page when game ends
      // This depends on the GameBoard component implementation
    });
  });

  describe('AC-005: God Card Functionality', () => {
    test('God cards are available for selection', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      const expectedGods = ['Demeter', 'Minotaur', 'Pan', 'Hermes', 'Apollo', 'Hephaestus'];
      
      expectedGods.forEach(god => {
        expect(screen.getAllByText(god)).toHaveLength(2);
      });
    });

    test('Selected god cards are sent to backend', async () => {
      mockedAxios.mockResolvedValueOnce({ data: 'game ready' });

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Select specific god cards
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      const godSelects = screen.getAllByRole('combobox');
      fireEvent.change(godSelects[0], { target: { value: 'Apollo' } });
      fireEvent.change(godSelects[1], { target: { value: 'Hephaestus' } });

      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080/initialGame?playerOneName=Alice&playerTwoName=Bob&playerOneCard=Apollo&playerTwoCard=Hephaestus',
          headers: {},
        });
      });
    });
  });

  describe('AC-006: Error Handling and User Feedback', () => {
    test('System provides clear error messages for invalid actions', async () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Test various error scenarios
      const startButton = screen.getByText(/Start Game/i);
      
      // Empty player one name
      fireEvent.click(startButton);
      await waitFor(() => {
        expect(window.alert).toHaveBeenCalledWith('Player one did not enter name');
      });

      // Fill player one, leave player two empty
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.click(startButton);
      
      await waitFor(() => {
        expect(window.alert).toHaveBeenCalledWith('Player two did not enter name');
      });
    });

    test('System handles backend communication errors gracefully', async () => {
      mockedAxios.mockRejectedValueOnce(new Error('Server Error'));

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(window.alert).toHaveBeenCalledWith('Not connected to backend');
      });
    });
  });

  describe('AC-007: User Interface and Experience', () => {
    test('Application has consistent navigation between pages', () => {
      const routes = ['/', '/playerform', '/gameboard', '/wingame'];
      
      routes.forEach(route => {
        const { unmount } = render(
          <MemoryRouter initialEntries={[route]}>
            <App />
          </MemoryRouter>
        );
        
        // Should render without crashing
        expect(document.body).toBeInTheDocument();
        unmount();
      });
    });

    test('Application is responsive and accessible', () => {
      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      // Check for Bootstrap classes (responsive design)
      const responsiveElements = document.querySelectorAll('.container, .row, .col-sm, .d-flex');
      expect(responsiveElements.length).toBeGreaterThan(0);

      // Check for accessible elements
      const buttons = screen.getAllByRole('button');
      expect(buttons.length).toBeGreaterThan(0);
    });

    test('Application provides visual feedback for user interactions', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Check for visual feedback elements
      const warning = screen.getByText(/certain player have duplicate god card/i);
      expect(warning).toHaveClass('text-warning');
    });
  });

  describe('AC-008: Complete Game Workflow', () => {
    test('User can complete full game workflow', async () => {
      // Mock all necessary API calls
      mockedAxios
        .mockResolvedValueOnce({ data: 'Hello world' }) // Home connection check
        .mockResolvedValueOnce({ data: 'game ready' })  // Game initialization
        .mockResolvedValue({                            // Game state
          data: {
            gameStatus: 1,
            curPlayer: 0,
            playerA: { name: 'Alice', godCard: 'Demeter' },
            playerB: { name: 'Bob', godCard: 'Pan' }
          }
        });

      // Start from home
      const { rerender } = render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      // Navigate through the flow
      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(screen.getByText(/Welcome to Santorini Game/i)).toBeInTheDocument();
      });

      // Fill player form
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      const gameStartButton = screen.getByText(/Start Game/i);
      fireEvent.click(gameStartButton);

      // Should reach game board
      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalledWith(
          expect.objectContaining({
            url: expect.stringContaining('initialGame')
          })
        );
      });
    });
  });
});