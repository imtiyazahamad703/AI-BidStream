import { axiosClient } from './axiosClient';

export interface Item {
  id: number;
  sellerId: number;
  title: string;
  description: string;
  startingPrice: number;
  condition: string;
  createdAt: string;
  status: 'AVAILABLE' | 'IN_AUCTION' | 'SOLD';
}

export interface CreateItemData {
  title: string;
  description: string;
  startingPrice: number;
  condition: string;
  attributes?: Record<string, string>;
}

export const itemApi = {
  getSellerItems: async (): Promise<Item[]> => {
    const response = await axiosClient.get<Item[]>('/items/seller');
    return response.data;
  },
  
  createItem: async (data: CreateItemData): Promise<Item> => {
    const response = await axiosClient.post<Item>('/items', data);
    return response.data;
  },

  getItemDetails: async (id: number): Promise<Item> => {
    const response = await axiosClient.get<Item>(`/items/${id}`);
    return response.data;
  },
  
  deleteItem: async (id: number): Promise<void> => {
    await axiosClient.delete(`/items/${id}`);
  }
};
