import { axiosClient } from './axiosClient';
import { Item } from './itemApi';

export interface Auction {
  id: number;
  itemId: number;
  sellerId: number;
  startingPrice: number;
  currentBid: number;
  status: 'PENDING' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  startTime: string;
  endTime: string;
  item?: Item;
}

export interface CreateAuctionData {
  itemId: number;
  startTime: string; // ISO 8601 format
  endTime: string;   // ISO 8601 format
}

export const auctionApi = {
  getSellerAuctions: async (): Promise<Auction[]> => {
    const response = await axiosClient.get<Auction[]>('/auctions/seller');
    return response.data;
  },

  getActiveAuctions: async (): Promise<Auction[]> => {
    const response = await axiosClient.get<Auction[]>('/auctions/active');
    return response.data;
  },
  
  createAuction: async (data: CreateAuctionData): Promise<Auction> => {
    const response = await axiosClient.post<Auction>('/auctions', data);
    return response.data;
  },

  getAuctionDetails: async (id: number): Promise<Auction> => {
    const response = await axiosClient.get<Auction>(`/auctions/${id}`);
    return response.data;
  },

  searchActiveAuctions: async (query: string, category?: string): Promise<Auction[]> => {
    const response = await axiosClient.get<Auction[]>('/auctions/search', {
      params: { query, category }
    });
    return response.data;
  }
};
