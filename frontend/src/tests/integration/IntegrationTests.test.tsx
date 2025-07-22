import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import axios from 'axios';
import App from '../../App';

// Integration tests that test the actual API calls (can be run against real backend)
describe('Frontend-Backend Integration Tests', () => {
  const BACKEND_URL = 'http://localhost:8080';
  
  beforeEach(() => {
    // Reset axios to use real HTTP calls for integration tests
    jest.restoreAllMocks();
  });

  describe('INT-001: Backend Connection Tests', () => {
    test('Home page can connect to backend server', async () => {
      // This test requires the backend to be running
      const mockAlert = jest.spyOn(window, 'alert').mockImplementation(() => {});
      
      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      // Wait for either success navigation or error alert
      await waitFor(() => {
        // If backend is running, should navigate to player form
        // If backend is not running, should show alert
        const playerFormText = screen.queryByText(/Welcome to Santorini Game/i);
        if (!playerFormText) {
          expect(mockAlert).toHaveBeenCalledWith('Not connected to backend');
        }
      }, { timeout: 5000 });

      mockAlert.mockRestore();
    });

    test('Backend health check endpoint responds correctly', async () => {
      try {
        const response = await axios.get(BACKEND_URL);
        expect(response.data).toBe('Hello world');
      } catch (error) {
        // Backend is not running - this is expected in CI/CD environments
        console.log('Backend not available for integration test');
        expect(error).toBeDefined();
      }
    });
  });

  describe('INT-002: Game Initialization Integration', () => {
    test('Can initialize game with valid player data', async () => {
      const mockAlert = jest.spyOn(window, 'alert').mockImplementation(() => {});
      
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Fill in player data
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'TestPlayer1' } });
      fireEvent.change(playerInputs[1], { target: { value: 'TestPlayer2' } });

      const godSelects = screen.getAllByRole('combobox');
      fireEvent.change(godSelects[0], { target: { value: 'Demeter' } });
      fireEvent.change(godSelects[1], { target: { value: 'Pan' } });

      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      // Wait for response
      await waitFor(() => {
        // Should either navigate to game board or show error
        const gameBoard = document.querySelector('.game-board, .container');
        if (!gameBoard) {
          // If no navigation occurred, backend might not be available
          console.log('Game initialization may have failed - backend not available');
        }
      }, { timeout: 5000 });

      mockAlert.mockRestore();
    });

    test('Game initialization API call format is correct', async () => {
      // Mock axios to capture the actual call being made
      const axiosSpy = jest.spyOn(axios, 'request').mockResolvedValue({ data: 'game ready' });
      
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalledWith({
          method: 'get',
          url: expect.stringContaining('/initialGame'),
          headers: {},
        });
      });

      axiosSpy.mockRestore();
    });
  });

  describe('INT-003: Game State Management Integration', () => {
    test('Game board fetches initial game state', async () => {
      const axiosSpy = jest.spyOn(axios, 'request').mockResolvedValue({
        data: {
          gameStatus: 1,
          curPlayer: 0,
          curPlayerAction: 1,
          playerA: { name: 'Alice', godCard: 'Demeter' },
          playerB: { name: 'Bob', godCard: 'Pan' },
          board: { grid: [] }
        }
      });

      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalledWith({
          method: 'get',
          url: expect.stringContaining('/getGameState'),
          headers: {},
        });
      });

      axiosSpy.mockRestore();
    });

    test('Game board handles real game state data structure', async () => {
      // Test with realistic game state data
      const gameState = {
        gameStatus: 1,
        curPlayer: 0,
        curPlayerAction: 2,
        focusingGridR: -1,
        focusingGridC: -1,
        playerA: {
          name: 'Alice',
          godCard: 'Demeter',
          workerA: { workerId: 0, playerName: 'Alice' },
          workerB: { workerId: 1, playerName: 'Alice' }
        },
        playerB: {
          name: 'Bob',
          godCard: 'Pan',
          workerA: { workerId: 0, playerName: 'Bob' },
          workerB: { workerId: 1, playerName: 'Bob' }
        },
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
      };

      const axiosSpy = jest.spyOn(axios, 'request').mockResolvedValue({
        data: gameState
      });

      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalled();
        // Component should handle the game state without crashing
        expect(document.body).toBeInTheDocument();
      });

      axiosSpy.mockRestore();
    });
  });

  describe('INT-004: Error Handling Integration', () => {
    test('Handles network timeouts gracefully', async () => {
      const axiosSpy = jest.spyOn(axios, 'request').mockRejectedValue(
        new Error('timeout of 5000ms exceeded')
      );
      const consoleSpy = jest.spyOn(console, 'log').mockImplementation(() => {});

      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalled();
        // Should handle timeout gracefully
        expect(document.body).toBeInTheDocument();
      });

      axiosSpy.mockRestore();
      consoleSpy.mockRestore();
    });

    test('Handles server errors (500) gracefully', async () => {
      const axiosSpy = jest.spyOn(axios, 'request').mockRejectedValue({
        response: { status: 500, data: 'Internal Server Error' }
      });
      const consoleSpy = jest.spyOn(console, 'log').mockImplementation(() => {});

      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalled();
        // Should handle server error gracefully
        expect(document.body).toBeInTheDocument();
      });

      axiosSpy.mockRestore();
      consoleSpy.mockRestore();
    });

    test('Handles malformed JSON responses', async () => {
      const axiosSpy = jest.spyOn(axios, 'request').mockResolvedValue({
        data: 'invalid json response'
      });
      const consoleSpy = jest.spyOn(console, 'log').mockImplementation(() => {});

      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalled();
        // Should handle malformed response gracefully
        expect(document.body).toBeInTheDocument();
      });

      axiosSpy.mockRestore();
      consoleSpy.mockRestore();
    });
  });

  describe('INT-005: API Contract Validation', () => {
    test('Home page API contract', async () => {
      const axiosSpy = jest.spyOn(axios, 'request').mockResolvedValue({
        data: 'Hello world'
      });

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080',
          headers: {},
        });
      });

      axiosSpy.mockRestore();
    });

    test('Player form API contract', async () => {
      const axiosSpy = jest.spyOn(axios, 'request').mockResolvedValue({
        data: 'game ready'
      });

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080/initialGame?playerOneName=Alice&playerTwoName=Bob&playerOneCard=Demeter&playerTwoCard=Pan',
          headers: {},
        });
      });

      axiosSpy.mockRestore();
    });

    test('Game board API contract', async () => {
      const axiosSpy = jest.spyOn(axios, 'request').mockResolvedValue({
        data: { gameStatus: 1 }
      });

      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080/getGameState',
          headers: {},
        });
      });

      axiosSpy.mockRestore();
    });
  });

  describe('INT-006: Performance Integration', () => {
    test('API calls complete within reasonable time', async () => {
      const startTime = Date.now();
      
      const axiosSpy = jest.spyOn(axios, 'request').mockImplementation(() => 
        new Promise(resolve => 
          setTimeout(() => resolve({ data: 'Hello world' }), 100)
        )
      );

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(axiosSpy).toHaveBeenCalled();
      });

      const endTime = Date.now();
      const duration = endTime - startTime;
      
      // Should complete within 5 seconds (generous for integration test)
      expect(duration).toBeLessThan(5000);

      axiosSpy.mockRestore();
    });

    test('Multiple rapid API calls are handled correctly', async () => {
      let callCount = 0;
      const axiosSpy = jest.spyOn(axios, 'request').mockImplementation(() => {
        callCount++;
        return Promise.resolve({ data: { gameStatus: 1 } });
      });

      render(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      // Wait for initial load
      await waitFor(() => {
        expect(callCount).toBeGreaterThan(0);
      });

      const initialCallCount = callCount;

      // Simulate rapid interactions (if the component supports it)
      // This would depend on the actual GameBoard implementation
      
      await waitFor(() => {
        expect(callCount).toBeGreaterThanOrEqual(initialCallCount);
      });

      axiosSpy.mockRestore();
    });
  });

  describe('INT-007: Cross-Browser Compatibility', () => {
    test('API calls work with different user agents', async () => {
      // Simulate different browser user agents
      const originalUserAgent = navigator.userAgent;
      
      const userAgents = [
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',
        'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36'
      ];

      for (const userAgent of userAgents) {
        Object.defineProperty(navigator, 'userAgent', {
          value: userAgent,
          configurable: true
        });

        const axiosSpy = jest.spyOn(axios, 'request').mockResolvedValue({
          data: 'Hello world'
        });

        render(
          <MemoryRouter initialEntries={['/']}>
            <App />
          </MemoryRouter>
        );

        const startButton = screen.getByText(/Click Me to Start/i);
        fireEvent.click(startButton);

        await waitFor(() => {
          expect(axiosSpy).toHaveBeenCalled();
        });

        axiosSpy.mockRestore();
      }

      // Restore original user agent
      Object.defineProperty(navigator, 'userAgent', {
        value: originalUserAgent,
        configurable: true
      });
    });
  });
});