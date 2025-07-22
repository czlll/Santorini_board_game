import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import axios from 'axios';
import PlayerForm from './PlayerForm';

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

  test('updates player names when typing', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerOneInput = screen.getAllByPlaceholderText(/please enter your player name/i)[0];
    const playerTwoInput = screen.getAllByPlaceholderText(/please enter your player name/i)[1];

    fireEvent.change(playerOneInput, { target: { value: 'Alice' } });
    fireEvent.change(playerTwoInput, { target: { value: 'Bob' } });

    expect(playerOneInput).toHaveValue('Alice');
    expect(playerTwoInput).toHaveValue('Bob');
  });

  test('updates god card selections', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerOneSelect = screen.getAllByRole('combobox')[0];
    const playerTwoSelect = screen.getAllByRole('combobox')[1];

    fireEvent.change(playerOneSelect, { target: { value: 'Minotaur' } });
    fireEvent.change(playerTwoSelect, { target: { value: 'Apollo' } });

    expect(playerOneSelect).toHaveValue('Minotaur');
    expect(playerTwoSelect).toHaveValue('Apollo');
  });

  test('shows warning when same god cards are selected', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerTwoSelect = screen.getAllByRole('combobox')[1];
    fireEvent.change(playerTwoSelect, { target: { value: 'Demeter' } });

    expect(screen.getByText(/certain player have duplicate god card/i)).toBeVisible();
  });

  test('hides warning when different god cards are selected', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerTwoSelect = screen.getAllByRole('combobox')[1];
    fireEvent.change(playerTwoSelect, { target: { value: 'Minotaur' } });

    const warningElement = screen.getByText(/certain player have duplicate god card/i);
    expect(warningElement).toHaveClass('invisible');
  });

  test('shows alert when player one name is empty', () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerTwoInput = screen.getAllByPlaceholderText(/please enter your player name/i)[1];
    fireEvent.change(playerTwoInput, { target: { value: 'Bob' } });

    const startButton = screen.getByText(/Start Game/i);
    fireEvent.click(startButton);

    expect(alertSpy).toHaveBeenCalledWith('Player one did not enter name');
    alertSpy.mockRestore();
  });

  test('shows alert when player two name is empty', () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerOneInput = screen.getAllByPlaceholderText(/please enter your player name/i)[0];
    fireEvent.change(playerOneInput, { target: { value: 'Alice' } });

    const startButton = screen.getByText(/Start Game/i);
    fireEvent.click(startButton);

    expect(alertSpy).toHaveBeenCalledWith('Player two did not enter name');
    alertSpy.mockRestore();
  });

  test('shows alert when duplicate god cards are selected', () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerOneInput = screen.getAllByPlaceholderText(/please enter your player name/i)[0];
    const playerTwoInput = screen.getAllByPlaceholderText(/please enter your player name/i)[1];
    const playerTwoSelect = screen.getAllByRole('combobox')[1];

    fireEvent.change(playerOneInput, { target: { value: 'Alice' } });
    fireEvent.change(playerTwoInput, { target: { value: 'Bob' } });
    fireEvent.change(playerTwoSelect, { target: { value: 'Demeter' } });

    const startButton = screen.getByText(/Start Game/i);
    fireEvent.click(startButton);

    expect(alertSpy).toHaveBeenCalledWith(
      'certain player have duplicate god card, please make sure each selection is different'
    );
    alertSpy.mockRestore();
  });

  test('navigates to game board when form is valid and backend responds correctly', async () => {
    mockedAxios.mockResolvedValueOnce({
      data: 'game ready'
    });

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerOneInput = screen.getAllByPlaceholderText(/please enter your player name/i)[0];
    const playerTwoInput = screen.getAllByPlaceholderText(/please enter your player name/i)[1];
    const playerTwoSelect = screen.getAllByRole('combobox')[1];

    fireEvent.change(playerOneInput, { target: { value: 'Alice' } });
    fireEvent.change(playerTwoInput, { target: { value: 'Bob' } });
    fireEvent.change(playerTwoSelect, { target: { value: 'Minotaur' } });

    const startButton = screen.getByText(/Start Game/i);
    fireEvent.click(startButton);

    await waitFor(() => {
      expect(mockedAxios).toHaveBeenCalledWith({
        method: 'get',
        url: 'http://localhost:8080/initialGame?playerOneName=Alice&playerTwoName=Bob&playerOneCard=Demeter&playerTwoCard=Minotaur',
        headers: {},
      });
      expect(mockNavigate).toHaveBeenCalledWith('/gameBoard');
    });
  });

  test('shows alert when backend connection fails', async () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
    mockedAxios.mockResolvedValueOnce({
      data: 'unexpected response'
    });

    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const playerOneInput = screen.getAllByPlaceholderText(/please enter your player name/i)[0];
    const playerTwoInput = screen.getAllByPlaceholderText(/please enter your player name/i)[1];

    fireEvent.change(playerOneInput, { target: { value: 'Alice' } });
    fireEvent.change(playerTwoInput, { target: { value: 'Bob' } });

    const startButton = screen.getByText(/Start Game/i);
    fireEvent.click(startButton);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith('Not connected to game board');
    });

    alertSpy.mockRestore();
  });

  test('contains all available god cards in select options', () => {
    render(
      <MemoryRouter>
        <PlayerForm />
      </MemoryRouter>
    );

    const expectedGods = ['Demeter', 'Minotaur', 'Pan', 'Hermes', 'Apollo', 'Hephaestus'];
    
    expectedGods.forEach(god => {
      expect(screen.getAllByText(god)).toHaveLength(2); // Each god appears in both selects
    });
  });
});