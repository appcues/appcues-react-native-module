# Using Appcues React Native with the Expo Managed Workflow

The Appcues React Native module can be integrated into an app using the Expo Manaaged Workflow using a simple wrapper. Testing Appcues functionality requires an Expo development build.

## Installation and Setup

```sh
$ npx expo install @appcues/react-native
```

### Wrapping the Appcues SDK

To allow your project to continue to work with the Expo Go app, create a wrapper around the Appcues React Native module that only invokes the Appcues SDK when it's available and otherwise falls back to a `console.log` that allows you to verify your calls to the wrapper are being made in the correct places.

Create a file `AppcuesWrapper.js` that wraps the `NativeModule`:

```js
import React from 'react';
import { NativeModules, UIManager, View } from 'react-native';

// Get native module or use fallback object
const AppcuesWrapper = NativeModules.AppcuesReactNative ?? {
  setup: (accountID, applicationID, options) => { console.log(`Appcues.setup(${accountID}, ${applicationID}, ${JSON.stringify(options)})`) },
  identify: (userID, properties) => { console.log(`Appcues.identify(${userID}, ${JSON.stringify(properties)})`) },
  reset: () => { console.log(`Appcues.reset()`) },
  anonymous: () => { console.log(`Appcues.anonymous()`) },
  group: (groupID, properties) => { console.log(`Appcues.group(${groupID}, ${JSON.stringify(properties)}`) },
  screen: (title, properties) => { console.log(`Appcues.screen(${title}, ${JSON.stringify(properties)})`) },
  track: (name, properties) => { console.log(`Appcues.track(${name}, ${JSON.stringify(properties)})`) },
  show: (experienceID) => { console.log(`Appcues.show(${experienceID})`) },
  debug: () => { console.log(`Appcues.debug()`) },
  didHandleURL: (url) => { console.log(`Appcues.didHandleURL(${url})`); return false },
};

export async function setup(accountID, applicationID, options) {
  return AppcuesWrapper.setup(accountID, applicationID, options, { _applicationFramework: 'expo' });
}

export function identify(userID, properties) {
  AppcuesWrapper.identify(userID, properties);
}

export function reset() {
  AppcuesWrapper.reset();
}

export function anonymous() {
  AppcuesWrapper.anonymous();
}

export function group(groupID, properties) {
  AppcuesWrapper.group(groupID, properties);
}

export function screen(title, properties) {
  AppcuesWrapper.screen(title, properties);
}

export function track(name, properties) {
  AppcuesWrapper.track(name, properties);
}

export function show(experienceID) {
  AppcuesWrapper.show(experienceID);
}

export function debug() {
  AppcuesWrapper.debug();
}

export async function didHandleURL(url) {
  return await AppcuesWrapper.didHandleURL(url);
}

const PlaceholderFrameView = (props) => <View style={props.style} />;

export const WrappedAppcuesFrameView =
  UIManager.getViewManagerConfig('AppcuesFrameView') != null
    ? require('@appcues/react-native').AppcuesFrameView
    : PlaceholderFrameView;
```

### Usage

Instead of importing from `@appcues/react-native` directly, reference the `AppcuesWrapper`:

```js
 import * as AppcuesWrapper from './AppcuesWrapper';
 await AppcuesWrapper.setup('APPCUES_ACCOUNT_ID', 'APPCUES_APPLICATION_ID');

 AppcuesWrapper.identify('my-user-id');

 AppcuesWrapper.screen('My Screen Name');

 <AppcuesWrapper.WrappedAppcuesFrameView frameID="frame-id" />
 ```

 > Tip: For more concise usage of a frame view, use a named import instead of a namespace import.
 > ```js
 > import { WrappedAppcuesFrameView } from './AppcuesWrapper';
 > <WrappedAppcuesFrameView frameID="frame-id" />
 > ```

### Custom Fonts

If your application uses custom fonts, optionally supply initialization options declaring where those font files can be found at runtime, for usage in Appcues experiences. These paths are platform specific, and the default values used by Expo are shown below. The iOS path refers to the path where font assets can be found in the app bundle. The Android path refers to the path within the application assets where fonts can be found, by default, the at the root level (empty string). If you are using the default path, you do not need to specify any init option value - these will already be used by default.

```js
await AppcuesWrapper.setup('APPCUES_ACCOUNT_ID', 'APPCUES_APPLICATION_ID', {
  iosFontsPath: '/assets/assets/fonts',
  androidFontsPath: '',
});
```

### Create a Development Build

A development build is necessary to use actually use the real Appcues SDK functionality. The [Getting Started](https://docs.expo.dev/development/getting-started) guide provided by Expo is a comprehensive reference, but the minimal steps are described here:

```sh
 $ npx expo install expo-dev-client

 # ios
 $ eas device:create # register test devices onto ad hoc provisioning profile
 $ eas build --profile development --platform ios

 # android
 eas build --profile development --platform android

 # Install the build on a device, then:
 $ npx expo start --dev-client

 # Then scan the QR code to open on the device
 ```
