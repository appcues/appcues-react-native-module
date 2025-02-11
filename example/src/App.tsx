import { useEffect, useState } from 'react';
import { Linking } from 'react-native';
import { createStaticNavigation, DefaultTheme } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import * as Appcues from '@appcues/react-native';
import { UserContext } from './contexts/UserContext';
import SignInScreen from './screens/signin/SignInScreen';
import MainScreen from './screens/main/MainScreen';

const RootStack = createNativeStackNavigator({
  initialRouteName: 'SignIn',
  screens: {
    SignIn: {
      screen: SignInScreen,
      options: {
        headerTitle: 'Sign In',
        headerShadowVisible: false,
      },
    },
    Main: {
      screen: MainScreen,
      options: {
        presentation: 'fullScreenModal',
        animation: 'fade',
        headerShown: false,
      },
    },
  },
});

const Navigation = createStaticNavigation(RootStack);

const Theme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    background: '#FFF',
    primary: '#1EB5C4',
  },
};

export default function App() {
  // App state for current user ID, which is used in the
  // UserContext.Provider below to make accessible to other elements
  // in the view hierarchy (sign in screen, profile screen)
  const [userID, setUserID] = useState<string | undefined>('default-00000');

  // Ensures that first _real_ render of the app doesn't occur until
  // SDK init complete - to avoid screen view analytics before SDK is ready
  const [initComplete, setInitComplete] = useState(false);

  useEffect(() => {
    const initializeSdk = async () => {
      await Appcues.setup('APPCUES_ACCOUNT_ID', 'APPCUES_APPLICATION_ID');
      setInitComplete(true);
    };
    initializeSdk();
  }, []);

  return (
    <UserContext.Provider value={{ userID, setUserID }}>
      {initComplete && <Navigation theme={Theme} />}
    </UserContext.Provider>
  );
}

Linking.addEventListener('url', async ({ url }) => {
  const appcuesDidHandleURL = await Appcues.didHandleURL(url);

  if (!appcuesDidHandleURL) {
    // Handle a non-Appcues URL
  }
});
