import { axiosClient } from './axiosClient';

export interface Bid {
  id: number;
  auctionId: number;
  bidderId: number;
  amount: number;
  timestamp: string;
}

export const bidApi = {
  getHighestBid: async (auctionId: number): Promise<Bid> => {
    const response = await axiosClient.get<Bid>(`/auctions/${auctionId}/bids/highest`);
    return response.data;
  },
  
  getBidHistory: async (auctionId: number): Promise<Bid[]> => {
    const response = await axiosClient.get<Bid[]>(`/auctions/${auctionId}/bids`);
    return response.data;
  }
};
