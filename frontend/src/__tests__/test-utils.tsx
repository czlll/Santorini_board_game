import React, { ReactElement } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

// Custom render function that includes providers
const AllTheProviders = ({ children, initialEntries = ['/'] }: { 
  children: React.ReactNode;
  initialEntries?: string[];
}) => {
  return (
    <MemoryRouter initialEntries={initialEntries}>
      {children}
    </MemoryRouter>
  );
};

const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'> & { initialEntries?: string[] }
) => {
  const { initialEntries, ...renderOptions } = options || {};
  
  return render(ui, {
    wrapper: ({ children }) => (
      <AllTheProviders initialEntries={initialEntries}>
        {children}
      </AllTheProviders>
    ),
    ...renderOptions,
  });
};

// Mock axios helper
export const mockAxiosSuccess = (data: any) => {
  const axios = require('axios');
  axios.mockResolvedValueOnce({ data });
};

export const mockAxiosError = (error: Error) => {
  const axios = require('axios');
  axios.mockRejectedValueOnce(error);
};

// Mock alert helper
export const mockAlert = () => {
  return jest.spyOn(window, 'alert').mockImplementation(() => {});
};

// Mock console.log helper
export const mockConsoleLog = () => {
  return jest.spyOn(console, 'log').mockImplementation(() => {});
};

// Test data factories
export const createMockGameData = (overrides = {}) => ({
  playerA: {
    name: 'Alice',
    godCard: 'Demeter',
    ...overrides
  },
  playerB: {
    name: 'Bob',
    godCard: 'Pan',
    ...overrides
  },
  gameStatus: 1,
  curPlayer: 0,
  curPlayerAction: 0,
  board: {
    grid: Array(5).fill(null).map((_, row) => 
      Array(5).fill(null).map((_, col) => ({
        row,
        col,
        tower: { level: 0, hasDome: false },
        worker: null
      }))
    )
  },
  ...overrides
});

export const createMockPlayerData = (name: string, godCard: string) => ({
  name,
  godCard
});

// Re-export everything
export * from '@testing-library/react';
export { customRender as render };