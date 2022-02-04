const { DefaultTheme } = require("@react-navigation/native");
import { StyleSheet } from 'react-native';

export const AppcuesTheme = {
    ...DefaultTheme,
    colors: {
      ...DefaultTheme.colors,
      primary: '#5C5CFF',
      background: `#FFFFFF`,
      border: `#FFFFFF`
    },
  };

export const AppcuesStyle = StyleSheet.create({  
  text: {
    fontSize: 17,
    marginTop: 8
  },
  input: {
    height: 35,
    borderWidth: 1,
    borderRadius: 5,
    borderColor: '#F1F1F1',
    padding: 5,
    marginTop: 8,
    fontSize: 14
  }
});

export default AppcuesStyle;