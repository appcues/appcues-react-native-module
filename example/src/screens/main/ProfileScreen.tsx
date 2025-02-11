import { useCallback, useEffect, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import {
  useFocusEffect,
  useNavigation,
  type ParamListBase,
} from '@react-navigation/native';
import { type NativeStackNavigationProp } from '@react-navigation/native-stack';
import * as Appcues from '@appcues/react-native';
import { useUserContext } from '../../contexts/UserContext';
import { FilledButton, TabBarButton } from '../../components/Button';
import Text from '../../components/Text';
import TextInput from '../../components/TextInput';

export const ProfileView = () => {
  const navigation = useNavigation<NativeStackNavigationProp<ParamListBase>>();
  const [givenName, onChangeGivenName] = useState<string | undefined>();
  const [familyName, onChangeFamilyName] = useState<string | undefined>();
  const { userID, setUserID } = useUserContext();

  useFocusEffect(
    useCallback(() => {
      Appcues.screen('Update Profile');
    }, [])
  );

  useEffect(() => {
    navigation.setOptions({
      // eslint-disable-next-line react/no-unstable-nested-components
      headerRight: () => (
        <TabBarButton
          title="Sign Out"
          onPress={() => {
            Appcues.reset();
            setUserID(undefined);
            navigation.popToTop();
          }}
        />
      ),
    });
  }, [navigation, setUserID]);

  return (
    <View style={styles.container}>
      <Text>Given Name</Text>
      <TextInput
        onChangeText={onChangeGivenName}
        placeholder="Given Name"
        value={givenName}
        nativeID="txtGivenName"
      />
      <Text>Family Name</Text>
      <TextInput
        onChangeText={onChangeFamilyName}
        placeholder="Family Name"
        value={familyName}
        nativeID="txtFamilyName"
      />
      <FilledButton
        title="Save"
        onPress={() => {
          Appcues.identify(
            userID!,
            removeEmpty({ givenName: givenName, familyName: familyName })
          );
          onChangeGivenName(undefined);
          onChangeFamilyName(undefined);
        }}
        testID="btnSaveProfile"
      />
    </View>
  );
};

function removeEmpty(obj: object) {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => v !== undefined)
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
