import React, { useState } from 'react';
import { Alert, Modal, StyleSheet, Text, Pressable, View } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import * as Appcues from '@appcues/react-native';
import { TintedButton, PlainButton } from '../../components/Button';

const Stack = createNativeStackNavigator();

const EventsView = () => {
  useFocusEffect(
    React.useCallback(() => {
      Appcues.screen('Trigger Events');
    }, [])
  );

  const [modalVisible, setModalVisible] = useState(false);

  return (
    <View style={styles.container}>
      <Modal
        animationType="slide"
        transparent={true}
        visible={modalVisible}
        onShow={() => Appcues.track('modal shown')}
        onRequestClose={() => {
          Alert.alert('Modal has been closed.');
          setModalVisible(!modalVisible);
        }}
      >
        <View style={styles.centeredView}>
          <View style={styles.modalView}>
            <Text style={styles.modalText}>Hello World!</Text>
            <Pressable
              nativeID="btnHideModal"
              style={[styles.button, styles.buttonClose]}
              onPress={() => setModalVisible(!modalVisible)}>
              <Text style={styles.textStyle}>Hide Modal</Text>
            </Pressable>
          </View>
        </View>
      </Modal>

      <TintedButton
        title="Trigger Event 1"
        nativeID="btnEvent1"
        onPress={() => Appcues.track('event1')}
      />
      <TintedButton
        title="Trigger Event 2"
        nativeID="btnEvent2"
        onPress={() => Appcues.track('event2')}
      />
      <TintedButton
        title="Show Modal"
        nativeID="btnShowModal"
        onPress={() => {
          setModalVisible(true);
        }}
      />
    </View>
  );
};

export default function EventsScreen() {
  return (
    <Stack.Navigator>
      <Stack.Screen
        name="Trigger Events"
        component={EventsView}
        options={{
          headerShadowVisible: false,
          headerTitleStyle: { fontWeight: '600' },
          headerRight: () => (
            <PlainButton title="Debug" onPress={() => Appcues.debug()} />
          ),
        }}
      />
    </Stack.Navigator>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'stretch',
    paddingTop: 35,
    paddingLeft: 40,
    paddingRight: 40,
  },
  centeredView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 22,
  },
  modalView: {
    margin: 20,
    backgroundColor: 'white',
    borderRadius: 20,
    padding: 35,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  button: {
    borderRadius: 20,
    padding: 10,
    elevation: 2,
  },
  buttonOpen: {
    backgroundColor: '#F194FF',
  },
  buttonClose: {
    backgroundColor: '#2196F3',
  },
  textStyle: {
    color: 'white',
    fontWeight: 'bold',
    textAlign: 'center',
  },
  modalText: {
    marginBottom: 15,
    textAlign: 'center',
  },
});
