import React, { useContext } from "react";
import { Text, TextInput, View } from 'react-native';
import { useFocusEffect } from "@react-navigation/native";
import * as Appcues from 'appcues-react-native-sdk'
import FilledButton from '../../components/FilledButton';
import PlainButton from "../../components/PlainButton";
import UserContext from "../../contexts/UserContext";
import AppcuesStyle from "../../style/AppcuesStyle";

const SignInScreen = ({navigation}) => {
  const { userID, setUserID } = useContext(UserContext);

  useFocusEffect(
    React.useCallback(() => {
      Appcues.screen('Sign In')
    }, [])
  );

  return (
    <View style={{ flex: 1 }}>
        <View style={{ flex: 1, alignItems: 'stretch', paddingTop: 35, paddingLeft: 40, paddingRight: 40 }}>
        <Text style={AppcuesStyle.text}>User ID</Text>
        <TextInput style={AppcuesStyle.input} onChangeText={setUserID} placeholder="UserID" value={userID} />
        <FilledButton title='Sign In' onPress={() =>  {
              Appcues.identify(userID)
              setUserID(userID)
              navigation.navigate('Main')
            }}/>
      </View>
      <View style={{ paddingBottom: 35}}>
        <PlainButton  title='Anonymous User' onPress={() => {
              Appcues.anonymous()
              navigation.navigate('Main')
          }} />
      </View>
    </View>
  );
};

export default SignInScreen