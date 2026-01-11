import React from 'react';

interface Participant {
  id: number;
  username: string;
  isOnline: boolean;
}

interface ParticipantInfoProps {
  participants: Participant[];
}

const ParticipantInfo: React.FC<ParticipantInfoProps> = ({ participants }) => {
  const onlineCount = participants.filter(p => p.isOnline).length;

  return (
    <div className="bg-slate-800 border border-slate-700 rounded-xl overflow-hidden flex flex-col mt-6">
      <div className="p-4 border-b border-slate-700 bg-slate-900/50 flex justify-between items-center">
        <h3 className="font-semibold text-white">Participants</h3>
        <span className="bg-indigo-500/20 text-indigo-400 text-xs px-2 py-1 rounded-full">
          {onlineCount} Online
        </span>
      </div>
      
      <div className="p-4 overflow-y-auto max-h-48 space-y-2">
        {participants.length === 0 ? (
          <div className="text-slate-500 text-sm text-center py-2">No participants yet.</div>
        ) : (
          participants.map((participant) => (
            <div key={participant.id} className="flex items-center justify-between">
              <span className="text-slate-300 text-sm">{participant.username}</span>
              <span className={`h-2 w-2 rounded-full ${participant.isOnline ? 'bg-green-500' : 'bg-slate-500'}`}></span>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ParticipantInfo;
