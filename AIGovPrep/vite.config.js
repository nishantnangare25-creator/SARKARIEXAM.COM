import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          firebase: ['firebase/app', 'firebase/auth', 'firebase/firestore'],
          charts: ['recharts'],
          pdf: ['jspdf', 'html2pdf.js'],
          markdown: ['react-markdown', 'marked', 'remark-gfm'],
        }
      }
    },
    chunkSizeWarningLimit: 1000,
  },
  logLevel: 'info',
})
