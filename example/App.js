import React, { useEffect, useState } from 'react';
import { Linking } from "react-native";
import * as Appcues from 'appcues-react-native'
import { DefaultTheme, NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import UserContext from './contexts/UserContext';
import SignInScreen from './screens/signin/SignInScreen';
import MainScreen from './screens/main/MainScreen';
import { PlainButton } from './components/Button';

const RootStack = createNativeStackNavigator();

export default function App() {

  // App state for current user ID, which is used in the 
  // UserContext.Provider below to make accessible to other elements
  // in the view hierarchy (sign in screen, profile screen)
  const [userID, setUserID] = useState('default-00000');

  // Ensures that first _real_ render of the app doesn't occur until
  // SDK init complete - to avoid screen view analytics before SDK is ready
  const [initComplete, setInitComplete] = useState(false)

  useEffect(() => {
    Appcues.setup('APPCUES_ACCOUNT_ID', 'APPCUES_APPLICATION_ID')
    setInitComplete(true)
  }, []);

  return (
    <UserContext.Provider value={{ userID, setUserID }}>
      {initComplete && <RootView/>}
    </UserContext.Provider>
  );
}

function RootView() {
  return (
    <NavigationContainer theme={{
      ...DefaultTheme,
      colors: {
        ...DefaultTheme.colors,
        primary: '#1EB5C4',
        background: `#FFFFFF`,
        border: `#FFFFFF`
      },
    }}>
      <RootStack.Navigator >
        <RootStack.Screen name="Sign In" component={SignInScreen}
          options={({ navigation }) => ({
            headerShadowVisible: false,
            headerTitleStyle: { fontWeight: '600' },
            headerRight: () => (
              <PlainButton title="Skip"
                onPress={() => {
                  navigation.navigate('Main')
                }} />)
          })} />
        <RootStack.Screen name="Main" component={MainScreen}
          options={{ presentation: 'fullScreenModal', animation: 'fade', headerShown: false }} />
      </RootStack.Navigator>
    </NavigationContainer>
  );
}

Linking.addEventListener('url', async ({ url }) => {
  const appcuesDidHandleURL = await Appcues.didHandleURL(url);

  if (!appcuesDidHandleURL) {
    // Handle a non-Appcues URL
  }
});