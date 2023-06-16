import React from 'react';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import EventsScreen from './EventsScreen';
import ProfileScreen from './ProfileScreen';
import GroupScreen from './GroupScreen';

const Tab = createBottomTabNavigator();

export default function MainScreen() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarIcon: ({ color, size }) => {
          let iconName;

          if (route.name === 'Events') {
            iconName = 'recording-outline';
          } else if (route.name === 'Profile') {
            iconName = 'person-outline';
          } else if (route.name === 'Group') {
            iconName = 'people-outline';
          }
          return <Ionicons name={iconName} size={size} color={color} />;
        },
      })}
    >
      <Tab.Screen
        name="Events"
        component={EventsScreen}
        options={{ title: 'Events', tabBarTestID: 'tabEvents' }}
      />
      <Tab.Screen
        name="Profile"
        component={ProfileScreen}
        options={{ title: 'Profile', tabBarTestID: 'tabProfile' }}
      />
      <Tab.Screen
        name="Group"
        component={GroupScreen}
        options={{ title: 'Group', tabBarTestID: 'tabGroup' }}
      />
    </Tab.Navigator>
  );
}
