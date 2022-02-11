import React from 'react';
import { Text as ReactText } from 'react-native';

export default function Text(props) {
    return (
      <ReactText style={{ fontSize: 17, marginTop: 8}}>
        {props.children}
      </ReactText>
    );
  }