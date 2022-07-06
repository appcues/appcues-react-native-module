# Centralized Screen Tracking With React Navigation

The `onStateChange` prop of `NavigationContainer` can be used to call `Appcues.screen` once rather than in each View:

```js
import { useRef } from 'react';
import { NavigationContainer, useNavigationContainerRef } from '@react-navigation/native';
import * as Appcues from 'appcues-react-native';

export default () => {
  const navigationRef = useNavigationContainerRef();
  const routeNameRef = useRef();

  return (
    <NavigationContainer
      ref={navigationRef}
      onReady={() => {
        routeNameRef.current = navigationRef.getCurrentRoute().name;
      }}
      onStateChange={async () => {
        const previousRouteName = routeNameRef.current;
        const currentRouteName = navigationRef.getCurrentRoute().name;

        if (previousRouteName !== currentRouteName) {
          Appcues.screen(currentRouteName);
        }

        // Save the current route name for later comparison
        routeNameRef.current = currentRouteName;
      }}
    >
      {/* ... */}
    </NavigationContainer>
  );
};
```

Refer to the React Navigation doc [Screen Tracking for Analytics](https://reactnavigation.org/docs/screen-tracking) for more details.
