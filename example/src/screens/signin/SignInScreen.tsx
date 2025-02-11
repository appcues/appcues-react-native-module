import { useCallback, useEffect } from 'react';
import { StyleSheet, View } from 'react-native';
import {
  useFocusEffect,
  useNavigation,
  type ParamListBase,
} from '@react-navigation/native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import * as Appcues from '@appcues/react-native';
import { useUserContext } from '../../contexts/UserContext';
import {
  FilledButton,
  PlainButton,
  TabBarButton,
} from '../../components/Button';
import Text from '../../components/Text';
import TextInput from '../../components/TextInput';

export default function SignInScreen() {
  const { userID, setUserID } = useUserContext();
  const navigation = useNavigation<NativeStackNavigationProp<ParamListBase>>();

  useFocusEffect(
    useCallback(() => {
      Appcues.screen('Sign In');
    }, [])
  );
  useEffect(() => {
    navigation.setOptions({
      // eslint-disable-next-line react/no-unstable-nested-components
      headerRight: () => (
        <TabBarButton
          title="Skip"
          onPress={() => {
            navigation.navigate('Main');
          }}
        />
      ),
    });
  }, [navigation]);

  return (
    <View style={styles.root}>
      <View style={styles.container}>
        <Text>User ID</Text>
        <TextInput
          onChangeText={setUserID}
          placeholder="UserID"
          value={userID}
        />
        <FilledButton
          title="Sign In"
          onPress={() => {
            if (userID) {
              Appcues.identify(userID);
              setUserID(userID);
              navigation.navigate('Main');
            }
          }}
        />
      </View>
      <View style={styles.anonymousButtonWrapper}>
        <PlainButton
          title="Anonymous User"
          onPress={() => {
            Appcues.anonymous();
            navigation.navigate('Main');
          }}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
  },

  container: {
    flex: 1,
    alignItems: 'stretch',
    paddingTop: 35,
    paddingLeft: 40,
    paddingRight: 40,
  },

  anonymousButtonWrapper: {
    paddingBottom: 35,
  },
});
