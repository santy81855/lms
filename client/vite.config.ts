/// <reference types="vitest/config" />
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
        '@': path.resolve(__dirname, './src')
    }
  },
  test: {
    globals: true,             // Enables global functions like 'describe' and 'test' without importing them
    environment: 'jsdom',      // Tells Vitest to use the browser-like environment
    setupFiles: './src/setupTests.ts', // Runs before your tests to configure matchers
  },
});
