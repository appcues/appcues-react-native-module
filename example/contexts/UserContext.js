import { createContext } from "react";

const UserContext = createContext({
  userID: null,
  setUserID: (userID) => {}
});

export default UserContext;