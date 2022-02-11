import React from 'react';
import { TouchableOpacity, Text } from 'react-native';

function Button(props) {
  return (
      <TouchableOpacity onPress={props.onPress} style={props.style.container}>
          <Text style={props.style.buttonText}>
              {props.title}
          </Text>
      </TouchableOpacity>
  );
}

export function FilledButton(props) {
  return (
    Button({title: props.title, onPress: props.onPress, style: {
      container: {
        backgroundColor: "#1EB5C4",
        borderRadius: 5,
        paddingVertical: 6,
        paddingHorizontal: 12,
        marginTop: 10
      },
      buttonText: {
        fontSize: 17,
        color: "#FFFFFF",
        alignSelf: "center",
      }
    }})
  );
}

export function PlainButton(props) {
  return (
    Button({title: props.title, onPress: props.onPress, style: {
      buttonText: {
        fontSize: 17,
        color: "#1EB5C4",
        alignSelf: "center",
      }
    }})
  );
}

export function TintedButton(props) {
  return (
    Button({title: props.title, onPress: props.onPress, style: {
      container: {
        backgroundColor: "#E8FBFA",
        borderRadius: 5,
        paddingVertical: 6,
        paddingHorizontal: 12,
        marginTop: 10
      },
      buttonText: {
        fontSize: 17,
        color: "#1EB5C4",
        alignSelf: "center",
      }
    }})
  );
}