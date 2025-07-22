import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import axios from 'axios';
import PlayerForm from '../PlayerForm';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Mock useNavigate
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('PlayerForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders player form elements correctly', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    expect(screen.getByText(/Welcome to Santorini Game/i)).toBeInTheDocument();
    expect(screen.getByText(/Player One Info/i)).toBeInTheDocument();
    expect(screen.getByText(/Player Two Info/i)).toBeInTheDocument();
    expect(screen.getByText(/Start Game/i)).toBeInTheDocument();
    
    // Check for input fields
    expect(screen.getAllByPlaceholderText(/please enter your player name/i)).toHaveLength(2);
    
    // Check for god card selects
    expect(screen.getAllByDisplayValue('Demeter')).toHaveLength(1);
    expect(screen.getAllByDisplayValue('Pan')).toHaveLength(1);
  });

  test('shows alert when player one name is empty', async () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    // Fill player two name but leave player one empty
    const playerTwoInput = screen.getAllByPlaceholderText(/please enter your player name/i)[1];
    fireEvent.change(playerTwoInput, { target: { value: 'Player Two' } });

    const startButton = screen.getByText(/Start Game/i);
    fireEvent.click(startButton);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith('Player one did not enter name');
    });

    alertSpy.mockRestore();
  });

  test('shows alert when player two name is empty', async () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    // Fill player one name but leave player two empty
    const playerOneInput = screen.getAllByPlaceholderText(/please enter your player name/i)[0];
    fireEvent.change(playerOneInput, { target: { value: 'Player One' } });

    const startButton = screen.getByText(/Start Game/i);
    fireEvent.click(startButton);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith('Player two did not enter name');
    });

    alertSpy.mockRestore();
  });

  test('shows alert when both players select same god card', async () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    // Fill both player names
    const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
    fireEvent.change(playerInputs[0], { target: { value: 'Player One' } });
    fireEvent.change(playerInputs[1], { target: { value: 'Player Two' } });

    // Set both players to same god card
    const playerTwoSelect = screen.getAllByDisplayValue('Pan')[0];
    fireEvent.change(playerTwoSelect, { target: { value: 'Demeter' } });

    const startButton = screen.getByText(/Start Game/i);
    fireEvent.click(startButton);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith(
        'certain player have duplicate god card, please make sure each selection is different'
      );
    });

    alertSpy.mockRestore();
  });

  test('shows warning message when god cards are duplicate', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    // Set both players to same god card
    const playerTwoSelect = screen.getAllByDisplayValue('Pan')[0];
    fireEvent.change(playerTwoSelect, { target: { value: 'Demeter' } });

    expect(screen.getByText(/certain player have duplicate god card/i)).toBeVisible();
  });

  test('hides warning message when god cards are different', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    // Default state should have different god cards (Demeter and Pan)
    const warningElement = screen.getByText(/certain player have duplicate god card/i);
    expect(warningElement).toHaveClass('invisible');
  });

  test('navigates to game board when game initialization is successful', async () => {
    mockedAxios.mockResolvedValueOnce({
      data: 'game ready'
    });

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    // Fill both player names
    const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
    fireEvent.change(playerInputs[0], { target: { value: 'Alice' } });
    fireEvent.change(playerInputs[1], { target: { value: 'Bob' } });

    const startButton = screen.getByText(/Start Game/i);
    fireEvent.click(startButton);

    await waitFor(() => {
      expect(mockedAxios).toHaveBeenCalledWith({
        method: 'get',
        url: 'http://localhost:8080/initialGame?playerOneName=Alice&playerTwoName=Bob&playerOneCard=Demeter&playerTwoCard=Pan',
        headers: {},
      });
      expect(mockNavigate).toHaveBeenCalledWith('/gameBoard');
    });
  });

  test('shows alert when game initialization fails', async () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
    
    mockedAxios.mockResolvedValueOnce({
      data: 'initialization failed'
    });

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    // Fill both player names
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

  test('all god card options are available', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const godCardOptions = ['Demeter', 'Minotaur', 'Pan', 'Hermes', 'Apollo', 'Hephaestus'];
    
    godCardOptions.forEach(godCard => {
      expect(screen.getAllByText(godCard)).toHaveLength(2); // Should appear in both selects
    });
  });

  test('player names update correctly when typed', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerInputs = screen.getAllByPlaceholderText(/please enter your player name/i);
    
    fireEvent.change(playerInputs[0], { target: { value: 'TestPlayer1' } });
    fireEvent.change(playerInputs[1], { target: { value: 'TestPlayer2' } });

    expect(playerInputs[0]).toHaveValue('TestPlayer1');
    expect(playerInputs[1]).toHaveValue('TestPlayer2');
  });

  test('god card selections update correctly', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const selects = screen.getAllByRole('combobox');
    
    fireEvent.change(selects[0], { target: { value: 'Minotaur' } });
    fireEvent.change(selects[1], { target: { value: 'Apollo' } });

    expect(selects[0]).toHaveValue('Minotaur');
    expect(selects[1]).toHaveValue('Apollo');
  });
});