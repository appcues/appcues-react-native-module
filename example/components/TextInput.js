import React from 'react';
import { TextInput as ReactTextInput } from 'react-native';

export default function TextInput(props) {
  const { value, onChangeText } = props;

  return (
    <ReactTextInput onChangeText={onChangeText} placeholder={props.placeholder} value={value}
      style={{
        height: 35,
        borderWidth: 1,
        borderRadius: 5,
        borderColor: '#F1F1F1',
        padding: 5,
        marginTop: 8,
        fontSize: 14
      }} />
  );
}