import React from 'react';

const HomePage: React.FC = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen px-4">
      <div className="text-center max-w-2xl">
        <h1 className="text-5xl font-bold bg-gradient-to-r from-indigo-500 to-sky-500 bg-clip-text text-transparent mb-6">
          BidStream
        </h1>
        <p className="text-lg text-slate-400 mb-8">
          Real-time auction platform powered by WebSocket technology.
          Place bids, track auctions, and win items in a live competitive environment.
        </p>
        <div className="flex gap-4 justify-center">
          <a
            href="/login"
            className="px-6 py-3 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg font-medium transition-colors duration-200"
          >
            Sign In
          </a>
          <a
            href="/register"
            className="px-6 py-3 border border-slate-600 hover:border-indigo-500 text-slate-300 hover:text-white rounded-lg font-medium transition-colors duration-200"
          >
            Create Account
          </a>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
