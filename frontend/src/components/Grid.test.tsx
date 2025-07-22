import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import Grid from './Grid';

describe('Grid Component', () => {
  const mockProps = {
    row: 2,
    col: 3,
    tower: { level: 1, hasDome: false },
    worker: null,
    onClick: jest.fn(),
    focusing: false
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders grid cell with correct position', () => {
    render(<Grid {...mockProps} />);
    
    const gridElement = screen.getByRole('button');
    expect(gridElement).toBeInTheDocument();
  });

  test('calls onClick when clicked', () => {
    render(<Grid {...mockProps} />);
    
    const gridElement = screen.getByRole('button');
    fireEvent.click(gridElement);
    
    expect(mockProps.onClick).toHaveBeenCalledTimes(1);
  });

  test('displays tower level correctly', () => {
    const propsWithTower = {
      ...mockProps,
      tower: { level: 2, hasDome: false }
    };
    
    render(<Grid {...propsWithTower} />);
    
    // Should display tower level 2
    expect(screen.getByText('2')).toBeInTheDocument();
  });

  test('displays dome when tower has dome', () => {
    const propsWithDome = {
      ...mockProps,
      tower: { level: 3, hasDome: true }
    };
    
    render(<Grid {...propsWithDome} />);
    
    // Should display dome indicator
    const gridElement = screen.getByRole('button');
    expect(gridElement).toHaveClass('dome'); // Assuming dome class exists
  });

  test('displays worker when present', () => {
    const propsWithWorker = {
      ...mockProps,
      worker: { workerId: 0, playerName: 'Alice' }
    };
    
    render(<Grid {...propsWithWorker} />);
    
    // Should display worker
    const gridElement = screen.getByRole('button');
    expect(gridElement).toHaveTextContent('A'); // Assuming worker is shown as first letter
  });

  test('applies focusing class when focusing is true', () => {
    const focusingProps = {
      ...mockProps,
      focusing: true
    };
    
    render(<Grid {...focusingProps} />);
    
    const gridElement = screen.getByRole('button');
    expect(gridElement).toHaveClass('focusing'); // Assuming focusing class exists
  });

  test('displays different tower levels correctly', () => {
    const levels = [0, 1, 2, 3];
    
    levels.forEach(level => {
      const { rerender } = render(
        <Grid {...mockProps} tower={{ level, hasDome: false }} />
      );
      
      if (level > 0) {
        expect(screen.getByText(level.toString())).toBeInTheDocument();
      }
      
      rerender(<div />); // Clear for next iteration
    });
  });

  test('handles different worker types', () => {
    const workers = [
      { workerId: 0, playerName: 'Alice' },
      { workerId: 1, playerName: 'Alice' },
      { workerId: 0, playerName: 'Bob' },
      { workerId: 1, playerName: 'Bob' }
    ];
    
    workers.forEach(worker => {
      const { rerender } = render(
        <Grid {...mockProps} worker={worker} />
      );
      
      const gridElement = screen.getByRole('button');
      expect(gridElement).toBeInTheDocument();
      
      rerender(<div />); // Clear for next iteration
    });
  });

  test('handles empty cell (no tower, no worker)', () => {
    const emptyProps = {
      ...mockProps,
      tower: { level: 0, hasDome: false },
      worker: null
    };
    
    render(<Grid {...emptyProps} />);
    
    const gridElement = screen.getByRole('button');
    expect(gridElement).toBeInTheDocument();
    expect(gridElement).not.toHaveTextContent(/[0-9]/); // No level numbers
  });

  test('applies correct CSS classes based on state', () => {
    render(<Grid {...mockProps} />);
    
    const gridElement = screen.getByRole('button');
    expect(gridElement).toHaveClass('grid-cell'); // Assuming base class exists
  });

  test('is accessible with proper ARIA attributes', () => {
    render(<Grid {...mockProps} />);
    
    const gridElement = screen.getByRole('button');
    expect(gridElement).toHaveAttribute('aria-label'); // Should have accessibility label
  });

  test('handles rapid clicks without issues', () => {
    render(<Grid {...mockProps} />);
    
    const gridElement = screen.getByRole('button');
    
    // Rapid clicks
    fireEvent.click(gridElement);
    fireEvent.click(gridElement);
    fireEvent.click(gridElement);
    
    expect(mockProps.onClick).toHaveBeenCalledTimes(3);
  });

  test('displays complex game state correctly', () => {
    const complexProps = {
      row: 1,
      col: 1,
      tower: { level: 2, hasDome: false },
      worker: { workerId: 1, playerName: 'Bob' },
      onClick: jest.fn(),
      focusing: true
    };
    
    render(<Grid {...complexProps} />);
    
    const gridElement = screen.getByRole('button');
    expect(gridElement).toBeInTheDocument();
    expect(gridElement).toHaveClass('focusing');
    expect(screen.getByText('2')).toBeInTheDocument(); // Tower level
  });
});