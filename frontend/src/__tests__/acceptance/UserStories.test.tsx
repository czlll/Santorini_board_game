import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import axios from 'axios';
import App from '../../App';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Santorini Game - User Stories Acceptance Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.spyOn(console, 'log').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('User Story 1: As a player, I want to start a new game', () => {
    test('US-001: Player can access the game from home page', async () => {
      mockedAxios.mockResolvedValueOnce({
        data: 'Hello world'
      });

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      // Given: Player is on the home page
      expect(screen.getByText(/Hello, Welcome to my Santorini Game/i)).toBeInTheDocument();
      
      // When: Player clicks the start button
      const startButton = screen.getByText(/Click Me to Start/i);
      expect(startButton).toBeInTheDocument();
      
      fireEvent.click(startButton);

      // Then: System should check backend connection
      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080',
          headers: {},
        });
      });
    });

    test('US-002: Player receives feedback when backend is unavailable', async () => {
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
      
      mockedAxios.mockRejectedValueOnce(new Error('Connection failed'));

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      // Given: Backend is unavailable
      // When: Player tries to start the game
      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      // Then: Player should see an error message
      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith('Not connected to backend');
      });

      alertSpy.mockRestore();
    });
  });

  describe('User Story 2: As a player, I want to set up game with another player', () => {
    test('US-003: Players can enter their names', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Given: Players are on the setup page
      expect(screen.getByText(/Player One Info/i)).toBeInTheDocument();
      expect(screen.getByText(/Player Two Info/i)).toBeInTheDocument();

      // When: Players enter their names
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      // Then: Names should be stored correctly
      expect(playerInputs[0]).toHaveValue('Alice');
      expect(playerInputs[1]).toHaveValue('Bob');
    });

    test('US-004: Players can select different god cards', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Given: Players are on the setup page
      const selects = screen.getAllByRole('combobox');

      // When: Players select different god cards
      fireEvent.change(selects[0], { target: { value: 'Demeter' } });
      fireEvent.change(selects[1], { target: { value: 'Pan' } });

      // Then: Selections should be stored correctly
      expect(selects[0]).toHaveValue('Demeter');
      expect(selects[1]).toHaveValue('Pan');

      // And: No warning should be shown
      const warningElement = screen.getByText(/certain player have duplicate god card/i);
      expect(warningElement).toHaveClass('invisible');
    });

    test('US-005: System prevents duplicate god card selection', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Given: Players are on the setup page
      const selects = screen.getAllByRole('combobox');

      // When: Players select the same god card
      fireEvent.change(selects[0], { target: { value: 'Demeter' } });
      fireEvent.change(selects[1], { target: { value: 'Demeter' } });

      // Then: Warning should be displayed
      const warningElement = screen.getByText(/certain player have duplicate god card/i);
      expect(warningElement).toBeVisible();
      expect(warningElement).not.toHaveClass('invisible');
    });

    test('US-006: Players can start game with valid setup', async () => {
      mockedAxios.mockResolvedValueOnce({
        data: 'game ready'
      });

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Given: Players have entered valid information
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      const selects = screen.getAllByRole('combobox');
      fireEvent.change(selects[0], { target: { value: 'Demeter' } });
      fireEvent.change(selects[1], { target: { value: 'Pan' } });

      // When: Players click start game
      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      // Then: Game should be initialized
      await waitFor(() => {
        expect(mockedAxios).toHaveBeenCalledWith({
          method: 'get',
          url: 'http://localhost:8080/initialGame?playerOneName=Alice&playerTwoName=Bob&playerOneCard=Demeter&playerTwoCard=Pan',
          headers: {},
        });
      });
    });
  });

  describe('User Story 3: As a player, I want to see available god cards', () => {
    test('US-007: All required god cards are available', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Given: Player is on the setup page
      // When: Player looks at god card options
      // Then: All required god cards should be available
      const expectedGodCards = [
        'Demeter',   // Can build twice
        'Minotaur',  // Can push opponent workers
        'Pan',       // Can win by jumping down
        'Hermes',    // Can move multiple times
        'Apollo',    // Can swap with opponent workers
        'Hephaestus' // Can build twice on same space
      ];

      expectedGodCards.forEach(godCard => {
        expect(screen.getAllByText(godCard)).toHaveLength(2);
      });
    });

    test('US-008: God card selection is intuitive', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Given: Player is on the setup page
      const selects = screen.getAllByRole('combobox');

      // When: Player changes god card selection
      fireEvent.change(selects[0], { target: { value: 'Minotaur' } });

      // Then: Selection should update immediately
      expect(selects[0]).toHaveValue('Minotaur');

      // When: Player changes to another god card
      fireEvent.change(selects[0], { target: { value: 'Apollo' } });

      // Then: Selection should update again
      expect(selects[0]).toHaveValue('Apollo');
    });
  });

  describe('User Story 4: As a player, I want clear error messages', () => {
    test('US-009: Clear message when player name is missing', async () => {
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Given: Player one name is empty
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      // When: Player tries to start game
      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      // Then: Clear error message should be shown
      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith('Player one did not enter name');
      });

      alertSpy.mockRestore();
    });

    test('US-010: Clear message when god cards are duplicate', async () => {
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Given: Both players have same god card and valid names
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

      const selects = screen.getAllByRole('combobox');
      fireEvent.change(selects[1], { target: { value: 'Demeter' } }); // Same as player 1

      // When: Player tries to start game
      const startButton = screen.getByText(/Start Game/i);
      fireEvent.click(startButton);

      // Then: Clear error message should be shown
      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith(
          'certain player have duplicate god card, please make sure each selection is different'
        );
      });

      alertSpy.mockRestore();
    });

    test('US-011: Clear message when backend connection fails', async () => {
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
      
      mockedAxios.mockRejectedValueOnce(new Error('Network Error'));

      render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      // Given: Backend is not available
      // When: Player tries to start
      const startButton = screen.getByText(/Click Me to Start/i);
      fireEvent.click(startButton);

      // Then: Clear error message should be shown
      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith('Not connected to backend');
      });

      alertSpy.mockRestore();
    });
  });

  describe('User Story 5: As a player, I want responsive UI', () => {
    test('US-012: Form elements respond immediately to input', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      // Given: Player is on the form
      const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
      const selects = screen.getAllByRole('combobox');

      // When: Player types in name field
      fireEvent.change(playerInputs[0], { target: { value: 'A' } });
      // Then: Input should update immediately
      expect(playerInputs[0]).toHaveValue('A');

      // When: Player continues typing
      fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
      // Then: Input should update immediately
      expect(playerInputs[0]).toHaveValue('Alice');

      // When: Player changes god card
      fireEvent.change(selects[0], { target: { value: 'Pan' } });
      // Then: Selection should update immediately
      expect(selects[0]).toHaveValue('Pan');
    });

    test('US-013: Warning messages appear and disappear appropriately', () => {
      render(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      const selects = screen.getAllByRole('combobox');
      const warningElement = screen.getByText(/certain player have duplicate god card/i);

      // Given: Different god cards are selected (default state)
      // Then: Warning should be hidden
      expect(warningElement).toHaveClass('invisible');

      // When: Player selects duplicate god card
      fireEvent.change(selects[1], { target: { value: 'Demeter' } });
      // Then: Warning should appear immediately
      expect(warningElement).toBeVisible();
      expect(warningElement).not.toHaveClass('invisible');

      // When: Player changes to different god card
      fireEvent.change(selects[1], { target: { value: 'Pan' } });
      // Then: Warning should disappear immediately
      expect(warningElement).toHaveClass('invisible');
    });
  });

  describe('User Story 6: As a player, I want to navigate between game screens', () => {
    test('US-014: Player can navigate through game flow', async () => {
      // Test navigation from home to player form
      mockedAxios.mockResolvedValueOnce({ data: 'Hello world' });

      const { rerender } = render(
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      );

      // Start from home page
      expect(screen.getByText(/Hello, Welcome to my Santorini Game/i)).toBeInTheDocument();

      // Navigate to player form (simulated)
      rerender(
        <MemoryRouter initialEntries={['/playerform']}>
          <App />
        </MemoryRouter>
      );

      expect(screen.getByText(/Welcome to Santorini Game/i)).toBeInTheDocument();

      // Navigate to game board (simulated)
      rerender(
        <MemoryRouter initialEntries={['/gameboard']}>
          <App />
        </MemoryRouter>
      );

      // Should render game board without crashing
      expect(document.body).toBeInTheDocument();
    });

    test('US-015: All routes are accessible', () => {
      const routes = [
        { path: '/', expectedText: /Hello, Welcome to my Santorini Game/i },
        { path: '/playerform', expectedText: /Welcome to Santorini Game/i },
        { path: '/gameboard', expectedElement: '.container' },
        { path: '/wingame', expectedElement: 'body' }
      ];

      routes.forEach(({ path, expectedText, expectedElement }) => {
        const { unmount } = render(
          <MemoryRouter initialEntries={[path]}>
            <App />
          </MemoryRouter>
        );

        if (expectedText) {
          expect(screen.getByText(expectedText)).toBeInTheDocument();
        } else if (expectedElement) {
          expect(document.querySelector(expectedElement)).toBeInTheDocument();
        }

        unmount();
      });
    });
  });
});