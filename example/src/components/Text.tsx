/* eslint-disable react-native/no-inline-styles */
import { Text as ReactText } from 'react-native';

export type TextProps = ReactText['props'];

export default function Text(props: TextProps) {
  return (
    <ReactText style={{ fontSize: 17, marginTop: 8 }}>
      {props.children}
    </ReactText>
  );
}
