import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import axios from 'axios';
import App from '../../App';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Santorini Game - Acceptance Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Mock console.log to avoid noise in tests
    jest.spyOn(console, 'log').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('Complete Game Flow', () => {
    test('AC-001: User can navigate from home to player form successfully', async () => {
      // Mock successful backend connection
      mockedAxios.mockResolvedValueOnce({
        data: 'Hello world'
      });

      const { container } = render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      // Verify home page is displayed
      expect(screen.getByText(/Hello, Welcome to my Santorini Game/i)).toBeInTheDocument();
      
      // Click start button
      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      // Wait for navigation
      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080',
          headers: {},
        });
      });
    });

    test('AC-002: User cannot proceed without backend connection', async () => {
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
      
      // Mock failed backend connection
      mockedAxios.mockRejectedValueOnce(new Error('Network Error'));

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith('Not connected to backend');
      });

      // Should still be on home page
      expect(screen.getByText(/Hello, Welcome to my Santorini Game/i)).toBeInTheDocument();

      alertSpy.mockRestore();
    });

    test('AC-003: Player form validates input correctly', async () => {
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Try to start game without entering names
      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith('Player one did not enter name');
      });

      alertSpy.mockRestore();
    });

    test('AC-004: Player form prevents duplicate god card selection', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Set both players to same god card
      const selects = screen.getAllByRole('combobox');
      fireEvent.change(selects[1], { target: { value: 'Demeter' } });

      // Warning should be visible
      const warningElement = screen.getByText(/certain player have duplicate god card/i);
      expect(warningElement).toBeVisible();
      expect(warningElement).not.toHaveClass('invisible');
    });

    test('AC-005: Complete player setup flow works correctly', async () => {
      // Mock successful game initialization
      mockedAxios.mockResolvedValueOnce({
        data: 'game ready'
      });

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Fill in player names
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      // Select different god cards
      const selects = screen.getAllByRole('combobox');
      fireEvent.change(selects[0], { target: { value: 'Demeter' } });
      fireEvent.change(selects[1], { target: { value: 'Pan' } });

      // Start game
      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080/initialGame?playerOneName=Alice&playerTwoName=Bob&playerOneCard=Demeter&playerTwoCard=Pan',
          headers: {},
        });
      });
    });
  });

  describe('God Card Selection Tests', () => {
    test('AC-006: All required god cards are available for selection', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      const expectedGodCards = ['Demeter', 'Minotaur', 'Pan', 'Hermes', 'Apollo', 'Hephaestus'];
      
      expectedGodCards.forEach(godCard => {
        expect(screen.getAllByText(godCard)).toHaveLength(2); // Should appear in both player selects
      });
    });

    test('AC-007: God card selection updates correctly', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      const selects = screen.getAllByRole('combobox');
      
      // Change player one's god card
      fireEvent.change(selects[0], { target: { value: 'Minotaur' } });
      expect(selects[0]).toHaveValue('Minotaur');

      // Change player two's god card
      fireEvent.change(selects[1], { target: { value: 'Apollo' } });
      expect(selects[1]).toHaveValue('Apollo');

      // Warning should not be visible since cards are different
      const warningElement = screen.getByText(/certain player have duplicate god card/i);
      expect(warningElement).toHaveClass('invisible');
    });
  });

  describe('Error Handling Tests', () => {
    test('AC-008: Handles backend connection errors gracefully', async () => {
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
      
      mockedAxios.mockRejectedValueOnce(new Error('Connection refused'));

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith('Not connected to backend');
      });

      alertSpy.mockRestore();
    });

    test('AC-009: Handles game initialization errors gracefully', async () => {
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
      
      mockedAxios.mockResolvedValueOnce({
        data: 'initialization failed'
      });

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Fill in valid data
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith('Not connected to game board');
      });

      alertSpy.mockRestore();
    });

    test('AC-010: Validates empty player names', async () => {
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Test empty player one name
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      let startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith('Player one did not enter name');
      });

      // Clear and test empty player two name
      alertSpy.mockClear();
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: '' } });

      startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith('Player two did not enter name');
      });

      alertSpy.mockRestore();
    });
  });

  describe('UI Responsiveness Tests', () => {
    test('AC-011: Form elements respond to user input', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      const selects = screen.getAllByRole('combobox');

      // Test input responsiveness
      fireEvent.change(playerInputs[0], { target: { value: 'TestName1' } });
      expect(playerInputs[0]).toHaveValue('TestName1');

      fireEvent.change(playerInputs[1], { target: { value: 'TestName2' } });
      expect(playerInputs[1]).toHaveValue('TestName2');

      // Test select responsiveness
      fireEvent.change(selects[0], { target: { value: 'Hermes' } });
      expect(selects[0]).toHaveValue('Hermes');

      fireEvent.change(selects[1], { target: { value: 'Apollo' } });
      expect(selects[1]).toHaveValue('Apollo');
    });

    test('AC-012: Warning message visibility toggles correctly', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      const selects = screen.getAllByRole('combobox');
      const warningElement = screen.getByText(/certain player have duplicate god card/i);

      // Initially should be invisible (different default cards)
      expect(warningElement).toHaveClass('invisible');

      // Make cards the same
      fireEvent.change(selects[1], { target: { value: 'Demeter' } });
      expect(warningElement).toHaveClass('visible');
      expect(warningElement).not.toHaveClass('invisible');

      // Make cards different again
      fireEvent.change(selects[1], { target: { value: 'Pan' } });
      expect(warningElement).toHaveClass('invisible');
    });
  });

  describe('Navigation Tests', () => {
    test('AC-013: App handles all defined routes', () => {
      const routes = ['/', '/playerform', '/gameboard', '/wingame'];

      routes.forEach(route => {
        const { unmount } = render(
          <MemoryRouter initialEntries={[route]}>
            <App />
          </MemoryRouter>
        );

        // Should not crash
        expect(document.body).toBeInTheDocument();
        unmount();
      });
    });

    test('AC-014: App handles undefined routes gracefully', () => {
      render(
        <MemoryRouter initialEntries={['/undefined-route']}>
          <App />
        </MemoryRouter>
      );

      // Should not crash
      expect(document.body).toBeInTheDocument();
    });
  });
});