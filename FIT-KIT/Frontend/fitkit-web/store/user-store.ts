import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

interface UserState {
  userId: string | null;
  username: string | null;
  email: string | null;
  setUserId: (id: string) => void;
  setUser: (user: { id: string; username: string; email?: string | null }) => void;
  clearUser: () => void;
}

export const useUserStore = create<UserState>()(
  persist(
    (set) => ({
      userId: null,
      username: null,
      email: null,
      setUserId: (id) => set({ userId: id }),
      setUser: (user) => set({
        userId: user.id,
        username: user.username,
        email: user.email ?? null,
      }),
      clearUser: () => set({ userId: null, username: null, email: null }),
    }),
    {
      name: 'fitkit-user-storage',
      storage: createJSONStorage(() => localStorage),
    }
  )
);
