import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('firebase')) return 'firebase'
            if (id.includes('react-router-dom') || id.includes('react-dom') || id.includes('/react/')) return 'vendor'
            if (id.includes('recharts')) return 'charts'
            if (id.includes('jspdf') || id.includes('html2pdf')) return 'pdf'
            if (id.includes('react-markdown') || id.includes('marked') || id.includes('remark-gfm')) return 'markdown'
            if (id.includes('i18next')) return 'i18n'
            if (id.includes('pdfjs-dist')) return 'pdfjs'
          }
        }
      }
    },
    chunkSizeWarningLimit: 1000,
  },
  logLevel: 'info',
})

