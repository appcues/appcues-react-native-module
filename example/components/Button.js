import React from 'react';
import { TouchableOpacity, Text } from 'react-native';

function Button(props) {
  return (
    <TouchableOpacity
      onPress={props.onPress}
      style={props.style.container}
      nativeID={props.nativeID}
    >
      <Text style={props.style.buttonText}>{props.title}</Text>
    </TouchableOpacity>
  );
}

export function FilledButton(props) {
  return (
    <Button
      style={{
        container: {
          backgroundColor: '#1EB5C4',
          borderRadius: 5,
          paddingVertical: 6,
          paddingHorizontal: 12,
          marginTop: 10,
        },
        buttonText: {
          fontSize: 17,
          color: '#FFFFFF',
          alignSelf: 'center',
        },
      }}
      {...props}
    />
  );
}

export function PlainButton(props) {
  return (
    <Button
      style={{
        buttonText: {
          fontSize: 17,
          color: '#1EB5C4',
          alignSelf: 'center',
        },
      }}
      {...props}
    />
  );
}

export function TintedButton(props) {
  return (
    <Button
      style={{
        container: {
          backgroundColor: '#E8FBFA',
          borderRadius: 5,
          paddingVertical: 6,
          paddingHorizontal: 12,
          marginTop: 10,
        },
        buttonText: {
          fontSize: 17,
          color: '#1EB5C4',
          alignSelf: 'center',
        },
      }}
      {...props}
    />
  );
}
