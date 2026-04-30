import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error('React Error Boundary caught an error:', error, errorInfo);
    
    // Auto-reload once if it's a chunk loading error (common in Vite production apps)
    if (error.message && (error.message.includes('dynamically imported module') || error.message.includes('fetch'))) {
      if (!sessionStorage.getItem('reloaded_from_chunk_error')) {
        sessionStorage.setItem('reloaded_from_chunk_error', 'true');
        window.location.reload();
      }
    }
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: '40px', textAlign: 'center', minHeight: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
          <h2>Oops! Something went wrong.</h2>
          <p style={{ color: 'gray', marginBottom: '20px' }}>The app encountered an unexpected error.</p>
          <button 
            onClick={() => {
              sessionStorage.clear();
              window.location.href = '/'; 
            }}
            style={{ padding: '10px 20px', background: '#2563EB', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' }}
          >
            Reload App
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

export default ErrorBoundary;
