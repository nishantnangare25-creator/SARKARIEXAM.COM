import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Trigger redeploy after security off
export default defineConfig({

  plugins: [react()],
  build: {
    rolldownOptions: {
      output: {
        codeSplitting: {
          groups: [
            {
              name: 'firebase',
              test(id) { return id.includes('node_modules/firebase') }
            },
            {
              name: 'vendor',
              test(id) {
                return id.includes('node_modules/react/') || 
                       id.includes('node_modules/react-dom/') || 
                       id.includes('node_modules/react-router-dom/')
              }
            },
            {
              name: 'charts',
              test(id) { return id.includes('node_modules/recharts') }
            },
            {
              name: 'pdf',
              test(id) { return id.includes('node_modules/jspdf') || id.includes('node_modules/html2pdf') }
            },
            {
              name: 'markdown',
              test(id) { 
                return id.includes('node_modules/react-markdown') || 
                       id.includes('node_modules/marked') || 
                       id.includes('node_modules/remark-gfm') 
              }
            },
            {
              name: 'i18n',
              test(id) { return id.includes('node_modules/i18next') }
            },
            {
              name: 'pdfjs',
              test(id) { return id.includes('node_modules/pdfjs-dist') }
            }
          ]
        }
      }
    },
    chunkSizeWarningLimit: 1000,
  },
  logLevel: 'info',
})



