import React, { useLayoutEffect, useRef, useState } from 'react';
import * as Appcues from 'appcues-react-native-sdk'
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import UserContext from './contexts/UserContext';
import SignInScreen from './screens/signin/SignInScreen';
import MainScreen from './screens/main/MainScreen';
import { PlainButton } from './components/Button';

const { DefaultTheme } = require("@react-navigation/native");
const RootStack = createNativeStackNavigator();

export default function App() {

  // App state for current user ID, which is used in the 
  // UserContext.Provider below to make accessible to other elements
  // in the view hierarchy (sign in screen, profile screen)
  const [userID, setUserID] = useState('default-00000');

  const firstUpdate = useRef(true);
  useLayoutEffect(() => {
    if (firstUpdate.current) {
      firstUpdate.current = false;
      Appcues.setup('ACCOUNT_ID', 'APP_ID')
      return;
    }
  });

  return (
    <UserContext.Provider value={{ userID, setUserID }}>
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
    </UserContext.Provider>
  );
}