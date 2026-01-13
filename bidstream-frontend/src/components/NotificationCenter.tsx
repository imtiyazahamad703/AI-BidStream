import React from 'react';

interface Notification {
  id: string;
  message: string;
  type: 'INFO' | 'WARNING' | 'OUTBID' | 'SUCCESS';
  timestamp: string;
}

interface NotificationCenterProps {
  notifications: Notification[];
}

const NotificationCenter: React.FC<NotificationCenterProps> = ({ notifications }) => {
  if (notifications.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-50 flex flex-col space-y-2 max-w-sm w-full">
      {notifications.map(notif => (
        <div 
          key={notif.id}
          className={`p-4 rounded-xl shadow-lg border backdrop-blur-md ${
            notif.type === 'OUTBID' 
              ? 'bg-red-500/20 border-red-500/50 text-red-100'
              : notif.type === 'SUCCESS'
                ? 'bg-green-500/20 border-green-500/50 text-green-100'
                : 'bg-slate-800/90 border-slate-600 text-white'
          } flex justify-between items-start animate-fade-in-up`}
        >
          <div className="flex flex-col">
            <span className="font-medium text-sm">{notif.message}</span>
            <span className="text-xs opacity-75 mt-1">
              {new Date(notif.timestamp).toLocaleTimeString()}
            </span>
          </div>
          {notif.type === 'OUTBID' && (
            <span className="text-xl animate-pulse ml-3">⚠️</span>
          )}
        </div>
      ))}
    </div>
  );
};

export default NotificationCenter;
