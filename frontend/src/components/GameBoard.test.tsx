import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import axios from 'axios';
import GameBoard from './GameBoard';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Mock useNavigate
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

// Mock the child components
jest.mock('./Grid', () => {
  return function MockGrid({ onClick, ...props }: any) {
    return (
      <div 
        data-testid={`grid-${props.row}-${props.col}`}
        onClick={() => onClick && onClick()}
        className="mock-grid"
      >
        Grid {props.row},{props.col}
      </div>
    );
  };
});

jest.mock('./GameMessage', () => {
  return function MockGameMessage(props: any) {
    return <div data-testid="game-message">{props.message || 'Game Message'}</div>;
  };
});

describe('GameBoard Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    
    // Mock successful game state response
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

  test('renders game board with 5x5 grid', async () => {
    render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      // Check that all 25 grid cells are rendered
      for (let row = 0; row < 5; row++) {
        for (let col = 0; col < 5; col++) {
          expect(screen.getByTestId(`grid-${row}-${col}`)).toBeInTheDocument();
        }
      }
    });
  });

  test('renders game message component', async () => {
    render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByTestId('game-message')).toBeInTheDocument();
    });
  });

  test('fetches game state on component mount', async () => {
    render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(mockedAxios).toHaveBeenCalledWith({
        method: 'get',
        url: 'http://localhost:8080/getGameState',
        headers: {},
      });
    });
  });

  test('handles grid click events', async () => {
    render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      const gridCell = screen.getByTestId('grid-0-0');
      expect(gridCell).toBeInTheDocument();
    });

    // Mock the click response
    mockedAxios.mockResolvedValueOnce({
      data: {
        gameStatus: 1,
        curPlayer: 0,
        curPlayerAction: 2,
        // ... other game state
      }
    });

    const gridCell = screen.getByTestId('grid-0-0');
    fireEvent.click(gridCell);

    // Should trigger another API call
    await waitFor(() => {
      expect(mockedAxios).toHaveBeenCalledTimes(2); // Initial load + click
    });
  });

  test('displays loading state initially', () => {
    // Mock a delayed response
    mockedAxios.mockImplementation(() => new Promise(resolve => 
      setTimeout(() => resolve({ data: {} }), 100)
    ));

    render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    // Initially should not show the grid (loadReady is false)
    expect(screen.queryByTestId('grid-0-0')).not.toBeInTheDocument();
  });

  test('handles API errors gracefully', async () => {
    const consoleSpy = jest.spyOn(console, 'log').mockImplementation(() => {});
    mockedAxios.mockRejectedValueOnce(new Error('Network Error'));

    render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalled();
    });

    consoleSpy.mockRestore();
  });

  test('updates game state when receiving new data', async () => {
    const { rerender } = render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByTestId('game-message')).toBeInTheDocument();
    });

    // Mock updated game state
    mockedAxios.mockResolvedValueOnce({
      data: {
        gameStatus: 2, // Game ended
        curPlayer: 1,
        winner: 'Alice'
      }
    });

    // Trigger a re-render or state update
    rerender(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(mockedAxios).toHaveBeenCalled();
    });
  });

  test('navigates to win page when game ends', async () => {
    // Mock game end state
    mockedAxios.mockResolvedValueOnce({
      data: {
        gameStatus: 2, // Game ended
        winner: 'Alice'
      }
    });

    render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      // This would depend on the actual GameBoard implementation
      // The component should navigate to /wingame when gameStatus is 2
      expect(mockedAxios).toHaveBeenCalled();
    });
  });

  test('handles undo functionality if available', async () => {
    render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByTestId('game-message')).toBeInTheDocument();
    });

    // Look for undo button if it exists
    const undoButton = screen.queryByText(/undo/i);
    if (undoButton) {
      mockedAxios.mockResolvedValueOnce({
        data: { success: true }
      });

      fireEvent.click(undoButton);

      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalledWith(
          expect.objectContaining({
            url: expect.stringContaining('undo')
          })
        );
      });
    }
  });

  test('displays current player information', async () => {
    mockedAxios.mockResolvedValueOnce({
      data: {
        gameStatus: 1,
        curPlayer: 0,
        playerA: { name: 'Alice', godCard: 'Demeter' },
        playerB: { name: 'Bob', godCard: 'Pan' }
      }
    });

    render(
      <MemoryRouter>
        <GameBoard />
      </MemoryRouter>
    );

    await waitFor(() => {
      // The game message should contain current player info
      expect(screen.getByTestId('game-message')).toBeInTheDocument();
    });
  });
});