import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';

const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="bg-slate-800 border-b border-slate-700 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="flex-shrink-0 text-xl font-bold bg-gradient-to-r from-indigo-500 to-sky-500 bg-clip-text text-transparent">
              BidStream
            </Link>
            <div className="hidden md:block ml-10">
              <div className="flex items-baseline space-x-4">
                <Link to="/auctions" className="text-slate-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors">
                  Browse Auctions
                </Link>
                {isAuthenticated && user?.role === 'SELLER' && (
                  <Link to="/seller/dashboard" className="text-slate-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors">
                    Seller Dashboard
                  </Link>
                )}
              </div>
            </div>
          </div>
          <div className="hidden md:block">
            <div className="ml-4 flex items-center md:ml-6 gap-4">
              {isAuthenticated ? (
                <>
                  <span className="text-sm text-slate-400">Welcome, {user?.email}</span>
                  <button
                    onClick={handleLogout}
                    className="bg-slate-700 hover:bg-slate-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
                  >
                    Logout
                  </button>
                </>
              ) : (
                <>
                  <Link to="/login" className="text-slate-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors">
                    Sign In
                  </Link>
                  <Link to="/register" className="bg-indigo-600 hover:bg-indigo-500 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
                    Create Account
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
