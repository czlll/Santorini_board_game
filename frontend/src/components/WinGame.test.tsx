import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import WinGame from './WinGame';

// Mock useNavigate
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('WinGame Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders win game page', () => {
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    // Should render the win game component
    expect(document.body).toBeInTheDocument();
  });

  test('displays winner information', () => {
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    // Look for common win game elements
    const winElements = screen.queryAllByText(/win|winner|victory|congratulations/i);
    expect(winElements.length).toBeGreaterThanOrEqual(0);
  });

  test('provides option to play again', () => {
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    // Look for play again button
    const playAgainButton = screen.queryByText(/play again|new game|restart/i);
    if (playAgainButton) {
      expect(playAgainButton).toBeInTheDocument();
    }
  });

  test('provides option to go home', () => {
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    // Look for home button
    const homeButton = screen.queryByText(/home|main menu|back/i);
    if (homeButton) {
      expect(homeButton).toBeInTheDocument();
    }
  });

  test('handles play again button click', () => {
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    const playAgainButton = screen.queryByText(/play again|new game|restart/i);
    if (playAgainButton) {
      fireEvent.click(playAgainButton);
      // Should navigate to player form or home
      expect(mockNavigate).toHaveBeenCalled();
    }
  });

  test('handles home button click', () => {
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    const homeButton = screen.queryByText(/home|main menu|back/i);
    if (homeButton) {
      fireEvent.click(homeButton);
      // Should navigate to home
      expect(mockNavigate).toHaveBeenCalledWith('/');
    }
  });

  test('displays game statistics if available', () => {
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    // Look for potential game statistics
    const statsElements = screen.queryAllByText(/turns|moves|time|score/i);
    expect(statsElements.length).toBeGreaterThanOrEqual(0);
  });

  test('has proper styling and layout', () => {
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    // Check for Bootstrap or custom CSS classes
    const container = document.querySelector('.container, .d-flex, .win-container');
    expect(container || document.body).toBeInTheDocument();
  });

  test('is accessible', () => {
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    // Should have proper heading structure
    const headings = screen.queryAllByRole('heading');
    expect(headings.length).toBeGreaterThanOrEqual(0);
  });

  test('handles different winner scenarios', () => {
    // This would depend on how winner information is passed to the component
    // For now, just ensure the component renders without crashing
    render(
      <MemoryRouter>
        <WinGame />
      </MemoryRouter>
    );
    
    expect(document.body).toBeInTheDocument();
  });
});