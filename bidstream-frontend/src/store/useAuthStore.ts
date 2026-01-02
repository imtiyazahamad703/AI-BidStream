import { create } from 'zustand';

export interface User {
  id: number;
  email: string;
  role: 'BIDDER' | 'SELLER' | 'ADMIN';
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  setUser: (user: User | null) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,
  isLoading: true, // Initially true while we might check token on load
  setUser: (user) => set({ user, isAuthenticated: !!user, isLoading: false }),
  logout: () => {
    localStorage.removeItem('auth_token');
    set({ user: null, isAuthenticated: false, isLoading: false });
  },
}));
