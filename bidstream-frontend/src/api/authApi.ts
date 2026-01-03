import { axiosClient } from './axiosClient';
import { User } from '../store/useAuthStore';

export interface RegistrationData {
  firstName: string;
  lastName: string;
  email: string;
  passwordHash: string; // the backend expects this field name in the DTO
  role: 'BIDDER' | 'SELLER';
}

export interface LoginData {
  email: string;
  passwordHash: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export const authApi = {
  register: async (data: RegistrationData): Promise<AuthResponse> => {
    const response = await axiosClient.post<AuthResponse>('/auth/register', data);
    return response.data;
  },
  login: async (data: LoginData): Promise<AuthResponse> => {
    const response = await axiosClient.post<AuthResponse>('/auth/login', data);
    return response.data;
  }
};
