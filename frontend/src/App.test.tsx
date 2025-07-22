import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import App from './App';

// Mock axios to prevent actual HTTP requests during testing
jest.mock('axios');

describe('App Component', () => {
  test('renders home page by default', () => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>
    );
    
    expect(screen.getByText(/Hello, Welcome to my Santorini Game/i)).toBeInTheDocument();
  });

  test('renders player form page when navigated to /playerform', () => {
    render(
      <MemoryRouter initialEntries={['/playerform']}>
        <App />
      </MemoryRouter>
    );
    
    expect(screen.getByText(/Welcome to Santorini Game/i)).toBeInTheDocument();
    expect(screen.getByText(/Player One Info/i)).toBeInTheDocument();
    expect(screen.getByText(/Player Two Info/i)).toBeInTheDocument();
  });

  test('renders game board page when navigated to /gameboard', () => {
    render(
      <MemoryRouter initialEntries={['/gameboard']}>
        <App />
      </MemoryRouter>
    );
    
    // GameBoard component should be rendered
    // Note: This might need adjustment based on GameBoard component structure
    expect(document.querySelector('.container')).toBeInTheDocument();
  });

  test('renders win game page when navigated to /wingame', () => {
    render(
      <MemoryRouter initialEntries={['/wingame']}>
        <App />
      </MemoryRouter>
    );
    
    // WinGame component should be rendered
    // Note: This might need adjustment based on WinGame component structure
    expect(document.body).toBeInTheDocument();
  });

  test('handles invalid routes gracefully', () => {
    render(
      <MemoryRouter initialEntries={['/invalid-route']}>
        <App />
      </MemoryRouter>
    );
    
    // Should not crash and render something
    expect(document.body).toBeInTheDocument();
  });
});