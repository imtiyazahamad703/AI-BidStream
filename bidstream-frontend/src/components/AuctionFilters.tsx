import React from 'react';

const AuctionFilters: React.FC = () => {
  return (
    <div className="bg-slate-800 p-4 rounded-xl border border-slate-700 mb-6 flex flex-wrap gap-4 items-center">
      <div className="flex-1 min-w-[200px]">
        <input 
          type="text" 
          placeholder="Search auctions..." 
          className="w-full bg-slate-900 border border-slate-600 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-indigo-500"
        />
      </div>
      <div>
        <select className="bg-slate-900 border border-slate-600 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-indigo-500">
          <option value="ALL">All Categories</option>
          <option value="ELECTRONICS">Electronics</option>
          <option value="VEHICLES">Vehicles</option>
          <option value="REAL_ESTATE">Real Estate</option>
        </select>
      </div>
      <button className="bg-indigo-600 hover:bg-indigo-500 text-white px-6 py-2 rounded-lg font-medium transition-colors">
        Filter
      </button>
    </div>
  );
};

export default AuctionFilters;
