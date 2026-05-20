import { useCallback, useEffect, useState } from 'react';
import { StyleSheet, View, Modal, Text, Pressable } from 'react-native';
import {
  useFocusEffect,
  useNavigation,
  type ParamListBase,
} from '@react-navigation/native';
import { type NativeStackNavigationProp } from '@react-navigation/native-stack';
import * as Appcues from '@appcues/react-native';
import { TintedButton, TabBarButton } from '../../components/Button';

export const EventsView = () => {
  const navigation = useNavigation<NativeStackNavigationProp<ParamListBase>>();
  const [modalVisible, setModalVisible] = useState(false);

  useFocusEffect(
    useCallback(() => {
      Appcues.screen('Trigger Events');
    }, [])
  );

  useEffect(() => {
    navigation.setOptions({
      // eslint-disable-next-line react/no-unstable-nested-components
      headerRight: () => (
        <TabBarButton title="Debug" onPress={() => Appcues.debug()} />
      ),
    });
  }, [navigation]);

  return (
    <View style={styles.container}>
      <TintedButton
        title="Trigger Event 1"
        testID="btnEvent1"
        onPress={() => Appcues.track('event1')}
      />
      <TintedButton
        title="Trigger Event 2"
        testID="btnEvent2"
        onPress={() => Appcues.track('event2')}
      />
      <TintedButton
        title="Show Native Modal"
        testID="btnShowModal"
        onPress={() => setModalVisible(true)}
      />

      <Modal
        visible={modalVisible}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Native Modal</Text>
            <Text style={styles.modalText}>
              This creates a separate Android Dialog window. Appcues overlays
              and the debugger FAB should appear above this modal.
            </Text>
            <TintedButton
              title="Trigger Event 1 (from modal)"
              testID="btnEvent1Modal"
              onPress={() => Appcues.track('event1')}
            />
            <Pressable
              style={styles.closeButton}
              onPress={() => setModalVisible(false)}
            >
              <Text style={styles.closeButtonText}>Close Modal</Text>
            </Pressable>
          </View>
        </View>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'stretch',
    paddingTop: 35,
    paddingLeft: 40,
    paddingRight: 40,
  },
  modalOverlay: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalContent: {
    backgroundColor: 'white',
    borderRadius: 16,
    padding: 24,
    width: '85%',
    alignItems: 'center',
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: '600',
    marginBottom: 12,
  },
  modalText: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    marginBottom: 20,
  },
  closeButton: {
    marginTop: 12,
    paddingVertical: 10,
    paddingHorizontal: 24,
  },
  closeButtonText: {
    fontSize: 16,
    color: '#007AFF',
  },
});
