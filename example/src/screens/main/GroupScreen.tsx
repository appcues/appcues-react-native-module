import { useCallback, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import * as Appcues from '@appcues/react-native';
import { FilledButton } from '../../components/Button';
import Text from '../../components/Text';
import TextInput from '../../components/TextInput';

export const GroupView = () => {
  const [groupID, onChangeGroupID] = useState('');

  useFocusEffect(
    useCallback(() => {
      Appcues.screen('Update Group');
    }, [])
  );

  return (
    <View style={styles.container}>
      <Text>Group</Text>
      <TextInput
        onChangeText={onChangeGroupID}
        placeholder="Group"
        value={groupID}
        nativeID="txtGroup"
      />
      <FilledButton
        title="Save"
        onPress={() => {
          Appcues.group(groupID, { test_user: true });
          onChangeGroupID('');
        }}
        testID="btnSaveGroup"
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
