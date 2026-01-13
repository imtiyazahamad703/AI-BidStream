import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import BidHistory from '../components/BidHistory';
import ParticipantInfo from '../components/ParticipantInfo';
import NotificationCenter from '../components/NotificationCenter';
import { wsService } from '../api/stompClient';
import useAuthStore from '../store/useAuthStore';

const mockBids = [
  { id: 1, auctionId: 1, bidderId: 101, amount: 250.00, timestamp: new Date().toISOString() },
  { id: 2, auctionId: 1, bidderId: 102, amount: 200.00, timestamp: new Date(Date.now() - 10000).toISOString() }
];

const mockNotifications = [
  { id: '1', message: 'Auction Started!', type: 'INFO' as const, timestamp: new Date().toISOString() }
];

const mockParticipants = [
  { id: 101, username: 'bidder1', isOnline: true },
  { id: 102, username: 'bidder2', isOnline: false },
  { id: 103, username: 'bidder3', isOnline: true }
];

const LiveAuctionRoom: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const token = useAuthStore(state => state.token);
  const [isConnected, setIsConnected] = useState(false);

  const [bids, setBids] = useState(mockBids);
  const [participants, setParticipants] = useState(mockParticipants);
  const [notifications, setNotifications] = useState(mockNotifications);

  useEffect(() => {
    if (token) {
      wsService.connect(token);
      setIsConnected(true);
      
      const bidSub = wsService.subscribe(`/topic/auctions/${id}/bids`, (message) => {
        setBids(prev => [message, ...prev]);
      });

      const participantSub = wsService.subscribe(`/topic/auctions/${id}/participants`, (message) => {
        if (message.type === 'JOIN' || message.type === 'LEAVE') {
          // Handle participant updates
        }
      });

      return () => {
        bidSub?.unsubscribe();
        participantSub?.unsubscribe();
        wsService.disconnect();
        setIsConnected(false);
      };
    }
  }, [token, id]);

  return (
    <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-4 gap-6 h-[calc(100vh-6rem)]">
      <div className="lg:col-span-3 flex flex-col space-y-4">
        {/* Main View Area */}
        <div className="bg-slate-800 border border-slate-700 rounded-xl p-6 flex-1 flex flex-col">
          <div className="flex justify-between items-center mb-6 pb-4 border-b border-slate-700">
            <h1 className="text-2xl font-bold text-white">Live Auction #{id}</h1>
            <div className="flex items-center space-x-6">
              <div className="bg-slate-900 px-4 py-2 rounded-lg border border-slate-600">
                <span className="text-slate-400 text-sm font-medium mr-2">Time Remaining:</span>
                <span className="text-white font-mono text-xl">00:14:59</span>
              </div>
              <div className="bg-indigo-500/20 px-4 py-2 rounded-lg border border-indigo-500/30">
                <span className="text-indigo-300 text-sm font-medium mr-2">Current Highest Bid:</span>
                <span className="text-indigo-400 font-bold text-xl">
                  ${bids.length > 0 ? bids[0].amount.toFixed(2) : '0.00'}
                </span>
              </div>
              <div className="flex items-center space-x-2">
                <span className="relative flex h-3 w-3">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
                  <span className="relative inline-flex rounded-full h-3 w-3 bg-red-500"></span>
                </span>
                <span className="text-red-400 font-semibold text-sm">LIVE</span>
              </div>
            </div>
          </div>
          
          <div className="flex-1 bg-slate-900 rounded-lg flex items-center justify-center border border-slate-700">
            <span className="text-slate-500">Product Image Stream</span>
          </div>
        </div>
      </div>

      <div className="lg:col-span-1">
        {/* Bid Stream Panel */}
        <BidHistory bids={bids} />
        <ParticipantInfo participants={participants} />
      </div>
      
      <NotificationCenter notifications={notifications} />
    </div>
  );
};

export default LiveAuctionRoom;
