import React from 'react';
import { StyleSheet, View } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import * as Appcues from '@appcues/react-native';
import { TintedButton, PlainButton } from '../../components/Button';

const Stack = createNativeStackNavigator();

const EventsView = () => {
  useFocusEffect(
    React.useCallback(() => {
      Appcues.screen('Trigger Events');
    }, [])
  );

  return (
    <View style={styles.container}>
      <TintedButton
        title="Trigger Event 1"
        nativeID="btnEvent1"
        onPress={() => Appcues.track('event1')}
      />
      <TintedButton
        title="Trigger Event 2"
        nativeID="btnEvent2"
        onPress={() => Appcues.track('event2')}
      />
    </View>
  );
};

export default function EventsScreen() {
  return (
    <Stack.Navigator>
      <Stack.Screen
        name="Trigger Events"
        component={EventsView}
        options={{
          headerShadowVisible: false,
          headerTitleStyle: { fontWeight: '600' },
          headerRight: () => (
            <PlainButton title="Debug" onPress={() => Appcues.debug()} />
          ),
        }}
      />
    </Stack.Navigator>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'stretch',
    paddingTop: 35,
    paddingLeft: 40,
    paddingRight: 40,
  },
});
