import React from 'react';
import { View } from 'react-native';
import { useFocusEffect } from "@react-navigation/native";
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import * as Appcues from 'appcues-react-native-sdk'
import { FilledButton } from '../../components/Button';
import Text from '../../components/Text';
import TextInput from '../../components/TextInput';

const Stack = createNativeStackNavigator();

const GroupView = () => {
  useFocusEffect(
    React.useCallback(() => { Appcues.screen('Update Group') }, [])
  );

  const [groupID, onChangeGroupID] = React.useState(null);

  return (
    <View style={{ flex: 1, alignItems: 'stretch', paddingTop: 35, paddingLeft: 40, paddingRight: 40 }}>
      <Text>Group</Text>
      <TextInput onChangeText={onChangeGroupID} placeholder="Group" value={groupID} />
      <FilledButton title='Save' onPress={() => {
            Appcues.group(groupID, {'test_user': true})
            onChangeGroupID(null)
          }
        } />
    </View>
  );
};

export default function GroupScreen() {
  return (
    <Stack.Navigator>
      <Stack.Screen name="Update Group" component={GroupView}
        options={{ headerShadowVisible: false, headerTitleStyle: { fontWeight: '600' } }} />
    </Stack.Navigator>
  );
}