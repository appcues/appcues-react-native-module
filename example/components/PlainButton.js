import React, { Component } from 'react';
import { TouchableOpacity, Text, StyleSheet } from 'react-native';

const style = StyleSheet.create({
    buttonText: {
      fontSize: 17,
      color: "#5C5CFF",
      alignSelf: "center",
    }
  });

class PlainButton extends Component {
    render() {
        return (
            <TouchableOpacity onPress={this.props.onPress}>
                <Text style={style.buttonText}>
                    {this.props.title}
                </Text>
            </TouchableOpacity>
        );
    }
}

export default PlainButton;