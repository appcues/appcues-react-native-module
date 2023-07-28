import React, { useContext } from 'react';
import { StyleSheet, View } from 'react-native';
import { useFocusEffect, useNavigation } from '@react-navigation/native';
import * as Appcues from '@appcues/react-native';
import UserContext from '../../contexts/UserContext';
import { FilledButton, PlainButton } from '../../components/Button';
import Text from '../../components/Text';
import TextInput from '../../components/TextInput';

export default function SignInScreen() {
  const { userID, setUserID } = useContext(UserContext);
  const navigation = useNavigation();

  useFocusEffect(
    React.useCallback(() => {
      Appcues.screen('Sign In');
    }, [])
  );

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
            Appcues.identify(userID);
            setUserID(userID);
            navigation.navigate('Main');
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
