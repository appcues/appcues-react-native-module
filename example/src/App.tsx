import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import * as Appcues from 'appcues-react-native-sdk'

export default function App() {
  Appcues.setup('ACCOUNT_ID', 'APP_ID')
  Appcues.identify("USER_ID")

  return (
    <View style={styles.container}>
      <Text>Appcues React Native!</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
