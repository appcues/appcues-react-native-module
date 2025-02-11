import Ionicons from 'react-native-vector-icons/Ionicons';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { EventsView } from './EventsScreen';
import { ProfileView } from './ProfileScreen';
import { GroupView } from './GroupScreen';
import { EmbedView } from './EmbedScreen';

const MainScreen = createBottomTabNavigator({
  screenOptions: ({ route }) => ({
    tabBarButtonTestID: `tab${route.name}`,
    // headerShown: false,
    tabBarIcon: ({ color, size }) => {
      let iconName = '';

      if (route.name === 'Events') {
        iconName = 'recording-outline';
      } else if (route.name === 'Profile') {
        iconName = 'person-outline';
      } else if (route.name === 'Group') {
        iconName = 'people-outline';
      } else if (route.name === 'Embed') {
        iconName = 'reader-outline';
      }

      return <Ionicons name={iconName} size={size} color={color} />;
    },
  }),
  screens: {
    Events: {
      screen: EventsView,
      options: {
        headerTitle: 'Trigger Events',
        headerShadowVisible: false,
      },
    },
    Profile: {
      screen: ProfileView,
      options: {
        headerTitle: 'Update Profile',
        headerShadowVisible: false,
      },
    },
    Group: {
      screen: GroupView,
      options: {
        headerTitle: 'Update Group',
        headerShadowVisible: false,
      },
    },
    Embed: {
      screen: EmbedView,
      options: {
        headerTitle: 'Embed Experiences',
        headerShadowVisible: false,
      },
    },
  },
});

export default MainScreen;
