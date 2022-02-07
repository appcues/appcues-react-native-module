import React from 'react';
import { Text, TextInput, View } from 'react-native';
import { useFocusEffect } from "@react-navigation/native";
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import * as Appcues from 'appcues-react-native-sdk'
import FilledButton from '../../components/FilledButton';
import AppcuesStyle from '../../style/AppcuesStyle';

const Stack = createNativeStackNavigator();

const GroupView = () => {
  useFocusEffect(
    React.useCallback(() => { Appcues.screen('Update Group') }, [])
  );

  const [groupID, onChangeGroupID] = React.useState(null);

  return (
    <View style={{ flex: 1, alignItems: 'stretch', paddingTop: 35, paddingLeft: 40, paddingRight: 40 }}>
      <Text style={AppcuesStyle.text}>Group</Text>
      <TextInput style={AppcuesStyle.input} onChangeText={onChangeGroupID} placeholder="Group" value={groupID} />
      <FilledButton title='Save' onPress={() => {
            Appcues.group(groupID, {'test_user': true})
            onChangeGroupID(null)
          }
        } />
    </View>
  );
};

const GroupScreen = () => {
  return (
    <Stack.Navigator>
      <Stack.Screen name="Update Group" component={GroupView}
        options={{ headerShadowVisible: false, headerTitleStyle: { fontWeight: '600' } }} />
    </Stack.Navigator>
  );
}

export default GroupScreen