import React, { useContext, useState } from 'react';
import { Text, TextInput, View } from 'react-native';
import { useFocusEffect } from "@react-navigation/native";
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import * as Appcues from 'appcues-react-native-sdk'
import UserContext from '../../contexts/UserContext';
import AppcuesStyle from '../../style/AppcuesStyle';
import FilledButton from '../../components/FilledButton';
import PlainButton from '../../components/PlainButton';

const Stack = createNativeStackNavigator();

const ProfileView = () => {
  useFocusEffect(
    React.useCallback(() => { Appcues.screen('Update Profile') }, [])
  );

  const [givenName, onChangeGivenName] = useState(null);
  const [familyName, onChangeFamilyName] = useState(null);
  const { userID } = useContext(UserContext);

  return (
    <View style={{ flex: 1, alignItems: 'stretch', paddingTop: 35, paddingLeft: 40, paddingRight: 40 }}>
      <Text style={AppcuesStyle.text}>Given Name</Text>
      <TextInput style={AppcuesStyle.input} onChangeText={onChangeGivenName} placeholder="Given Name" value={givenName} />
      <Text style={AppcuesStyle.text}>Family Name</Text>
      <TextInput style={AppcuesStyle.input} onChangeText={onChangeFamilyName} placeholder="Family Name" value={familyName} />
      <FilledButton title='Save' onPress={() => {
          Appcues.identify(userID, { 'givenName': givenName, 'familyName': familyName })
          onChangeGivenName(null)
          onChangeFamilyName(null)
        }} />
    </View>
  );
};

const ProfileScreen = ({ navigation }) => {

  const { setUserID } = useContext(UserContext);

  return (
    <Stack.Navigator>
      <Stack.Screen name="Update Profile" component={ProfileView}
        options={{ headerShadowVisible: false, headerTitleStyle: { fontWeight: 'bold' },
          headerRight: () => (
            <PlainButton title="Sign Out" onPress={() => {
              Appcues.reset()
              setUserID(null)
              navigation.pop()
            }} />
          ),
        }} />
    </Stack.Navigator>
  );
}

export default ProfileScreen