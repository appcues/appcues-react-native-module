import React, { useState } from 'react';
import * as Appcues from 'appcues-react-native-sdk'
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import { AppcuesTheme } from './style/AppcuesStyle';
import PlainButton from './components/PlainButton';
import UserContext from './contexts/UserContext';
import SignInScreen from './screens/signin/SignInScreen';
import MainScreen from './screens/main/MainScreen';


const RootStack = createNativeStackNavigator();

const App = () => {

  // App state for current user ID, which is used in the 
  // UserContext.Provider below to make accessible to other elements
  // in the view hierarchy (sign in screen, profile screen)
  const [userID, setUserID] = useState('default-00000');

  // Initialize the Appcues SDK as early as possible in your app
  Appcues.setup('ACCOUNT_ID', 'APP_ID')

  return (
    <UserContext.Provider value={{ userID, setUserID }}>
      <NavigationContainer theme={AppcuesTheme}>
        <RootStack.Navigator >
        <RootStack.Screen name="Sign In" component={SignInScreen}
            options={({ navigation }) => ({
              headerShadowVisible: false,
              headerTitleStyle: { fontWeight: 'bold' },
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
};

export default App;
