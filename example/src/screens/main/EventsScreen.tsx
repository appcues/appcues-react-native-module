import { useCallback, useEffect } from 'react';
import { StyleSheet, View } from 'react-native';
import {
  useFocusEffect,
  useNavigation,
  type ParamListBase,
} from '@react-navigation/native';
import { type NativeStackNavigationProp } from '@react-navigation/native-stack';
import * as Appcues from '@appcues/react-native';
import { TintedButton, TabBarButton } from '../../components/Button';

export const EventsView = () => {
  const navigation = useNavigation<NativeStackNavigationProp<ParamListBase>>();

  useFocusEffect(
    useCallback(() => {
      Appcues.screen('Trigger Events');
    }, [])
  );

  useEffect(() => {
    navigation.setOptions({
      // eslint-disable-next-line react/no-unstable-nested-components
      headerRight: () => (
        <TabBarButton title="Debug" onPress={() => Appcues.debug()} />
      ),
    });
  }, [navigation]);

  return (
    <View style={styles.container}>
      <TintedButton
        title="Trigger Event 1"
        testID="btnEvent1"
        onPress={() => Appcues.track('event1')}
      />
      <TintedButton
        title="Trigger Event 2"
        testID="btnEvent2"
        onPress={() => Appcues.track('event2')}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'stretch',
    paddingTop: 35,
    paddingLeft: 40,
    paddingRight: 40,
  },
});
