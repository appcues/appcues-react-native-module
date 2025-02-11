import { createContext, useContext } from 'react';

type UserContextType = {
  userID: string | undefined;
  setUserID: (userID: string | undefined) => void;
};

export const UserContext = createContext<UserContextType>(null!);

// This hook can be used to access the user info.
export function useUserContext() {
  return useContext(UserContext);
}
