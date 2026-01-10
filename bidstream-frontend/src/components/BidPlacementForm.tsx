import React, { useState } from 'react';

interface BidPlacementFormProps {
  currentBid: number;
  onPlaceBid: (amount: number) => void;
  disabled?: boolean;
}

const BidPlacementForm: React.FC<BidPlacementFormProps> = ({ currentBid, onPlaceBid, disabled }) => {
  const [bidAmount, setBidAmount] = useState<number | ''>('');
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    
    const amount = Number(bidAmount);
    
    if (isNaN(amount) || amount <= currentBid) {
      setError(`Bid must be greater than current price ($${currentBid})`);
      return;
    }

    onPlaceBid(amount);
    setBidAmount('');
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {error && (
        <div className="bg-red-500/10 border border-red-500/50 text-red-400 p-3 rounded-lg text-sm">
          {error}
        </div>
      )}
      <div>
        <label className="block text-sm font-medium text-slate-300 mb-2">Your Maximum Bid</label>
        <div className="relative">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <span className="text-slate-400">$</span>
          </div>
          <input
            type="number"
            step="0.01"
            value={bidAmount}
            onChange={(e) => setBidAmount(e.target.value === '' ? '' : parseFloat(e.target.value))}
            className="w-full bg-slate-900 border border-slate-600 rounded-lg pl-8 pr-4 py-3 text-white focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 text-lg font-semibold"
            placeholder="0.00"
            disabled={disabled}
          />
        </div>
      </div>
      <button
        type="submit"
        disabled={disabled}
        className="w-full bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg px-4 py-4 font-bold text-lg transition-colors disabled:opacity-50"
      >
        Place Bid
      </button>
    </form>
  );
};

export default BidPlacementForm;
