import {
  Button as RNButton,
  type ButtonProps,
  TouchableOpacity,
  Text,
  type ViewStyle,
  type TextStyle,
} from 'react-native';

function Button(
  props: ButtonProps & {
    style: { container?: ViewStyle; buttonText?: TextStyle };
  }
) {
  return (
    <TouchableOpacity
      onPress={props.onPress}
      style={props.style.container}
      testID={props.testID}
    >
      <Text style={props.style.buttonText}>{props.title}</Text>
    </TouchableOpacity>
  );
}

export function FilledButton(props: ButtonProps) {
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

export function PlainButton(props: ButtonProps) {
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

export function TintedButton(props: ButtonProps) {
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

export function TabBarButton(props: ButtonProps) {
  return <RNButton color="#1EB5C4" {...props} />;
}
