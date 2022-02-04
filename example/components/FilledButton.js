import React, { Component } from 'react';
import { TouchableOpacity, Text, StyleSheet } from 'react-native';

const style = StyleSheet.create({
    container: {
      backgroundColor: "#5C5CFF",
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
  });

class FilledButton extends Component {
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

export default FilledButton;