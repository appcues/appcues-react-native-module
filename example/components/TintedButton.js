import React, { Component } from 'react';
import { TouchableOpacity, Text, StyleSheet } from 'react-native';

const style = StyleSheet.create({
    container: {
      backgroundColor: "#E2E1FF",
      borderRadius: 5,
      paddingVertical: 6,
      paddingHorizontal: 12,
      marginTop: 10
    },
    buttonText: {
      fontSize: 17,
      color: "#5C5CFF",
      alignSelf: "center",
    }
  });

class TintedButton extends Component {
    render() {
        return (
            <TouchableOpacity onPress={this.props.onPress} style={style.container}>
                <Text style={style.buttonText}>
                    {this.props.title}
                </Text>
            </TouchableOpacity>
        );
    }
}

export default TintedButton;