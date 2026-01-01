import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import HomePage from './pages/HomePage';

const App: React.FC = () => {
  return (
    <Router>
      <div className="min-h-screen bg-slate-900 text-slate-100 font-sans selection:bg-indigo-500/30">
        <Routes>
          <Route path="/" element={<HomePage />} />
          {/* Fallback route */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
