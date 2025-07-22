import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import axios from 'axios';
import Home from '../Home';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Mock useNavigate
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('Home Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders home page elements correctly', () => {
    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    expect(screen.getByText(/Hello, Welcome to my Santorini Game/i)).toBeInTheDocument();
    expect(screen.getByText(/Click Me to Start/i)).toBeInTheDocument();
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  test('navigates to player form when backend connection is successful', async () => {
    mockedAxios.mockResolvedValueOnce({
      data: 'Hello world'
    });

    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    const startButton = screen.getByText(/Click Me to Start/i);
    fireEvent.click(startButton);

    await waitFor(() => {
      expect(mockedAxios).toHaveBeenCalledWith({
        method: 'get',
        url: 'http://localhost:8080',
        headers: {},
      });
      expect(mockNavigate).toHaveBeenCalledWith('/playerForm');
    });
  });

  test('shows alert when backend connection fails', async () => {
    // Mock window.alert
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
    
    mockedAxios.mockRejectedValueOnce(new Error('Network Error'));

    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    const startButton = screen.getByText(/Click Me to Start/i);
    fireEvent.click(startButton);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith('Not connected to backend');
    });

    alertSpy.mockRestore();
  });

  test('shows alert when backend returns unexpected response', async () => {
    // Mock window.alert
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
    
    mockedAxios.mockResolvedValueOnce({
      data: 'Unexpected response'
    });

    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    const startButton = screen.getByText(/Click Me to Start/i);
    fireEvent.click(startButton);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith('Not connected to backend');
    });

    alertSpy.mockRestore();
  });

  test('button has correct styling classes', () => {
    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    const button = screen.getByRole('button');
    expect(button).toHaveClass('btn', 'btn-primary', 'mx-auto', 'mt-2', 'mb-5', 'w-8');
  });
});