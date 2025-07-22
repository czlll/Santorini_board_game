import React from 'react';
import { render, screen } from '@testing-library/react';
import GameMessage from './GameMessage';

describe('GameMessage Component', () => {
  test('renders game message correctly', () => {
    const testMessage = 'Player A\'s turn to move';
    render(<GameMessage message={testMessage} />);
    
    expect(screen.getByText(testMessage)).toBeInTheDocument();
  });

  test('renders empty message', () => {
    render(<GameMessage message="" />);
    
    const messageElement = screen.getByTestId('game-message') || screen.getByRole('status');
    expect(messageElement).toBeInTheDocument();
  });

  test('renders different types of messages', () => {
    const messages = [
      'Player A\'s turn to place worker',
      'Player B\'s turn to move',
      'Player A\'s turn to build',
      'Player A wins!',
      'Invalid move. Try again.',
      'Game started successfully'
    ];

    messages.forEach(message => {
      const { rerender } = render(<GameMessage message={message} />);
      expect(screen.getByText(message)).toBeInTheDocument();
      rerender(<div />); // Clear for next iteration
    });
  });

  test('handles long messages', () => {
    const longMessage = 'This is a very long game message that should still be displayed correctly without breaking the layout or causing any issues with the user interface';
    render(<GameMessage message={longMessage} />);
    
    expect(screen.getByText(longMessage)).toBeInTheDocument();
  });

  test('handles special characters in messages', () => {
    const specialMessage = 'Player "Alice" can\'t move to (2,3) - blocked by tower!';
    render(<GameMessage message={specialMessage} />);
    
    expect(screen.getByText(specialMessage)).toBeInTheDocument();
  });

  test('applies correct CSS classes', () => {
    const message = 'Test message';
    render(<GameMessage message={message} />);
    
    const messageElement = screen.getByText(message);
    expect(messageElement).toHaveClass('game-message'); // Assuming this class exists
  });

  test('updates message when props change', () => {
    const initialMessage = 'Initial message';
    const updatedMessage = 'Updated message';
    
    const { rerender } = render(<GameMessage message={initialMessage} />);
    expect(screen.getByText(initialMessage)).toBeInTheDocument();
    
    rerender(<GameMessage message={updatedMessage} />);
    expect(screen.getByText(updatedMessage)).toBeInTheDocument();
    expect(screen.queryByText(initialMessage)).not.toBeInTheDocument();
  });

  test('handles undefined or null message gracefully', () => {
    // Test with undefined
    const { rerender } = render(<GameMessage message={undefined as any} />);
    expect(screen.getByTestId('game-message') || screen.getByRole('status')).toBeInTheDocument();
    
    // Test with null
    rerender(<GameMessage message={null as any} />);
    expect(screen.getByTestId('game-message') || screen.getByRole('status')).toBeInTheDocument();
  });

  test('is accessible with proper semantic markup', () => {
    const message = 'Accessible message';
    render(<GameMessage message={message} />);
    
    const messageElement = screen.getByText(message);
    // Should be in a semantic element like <p>, <div>, or have role="status"
    expect(messageElement.tagName).toMatch(/^(P|DIV|SPAN)$/);
  });

  test('handles HTML entities in messages', () => {
    const messageWithEntities = 'Player A &amp; Player B are playing';
    render(<GameMessage message={messageWithEntities} />);
    
    expect(screen.getByText(messageWithEntities)).toBeInTheDocument();
  });
});