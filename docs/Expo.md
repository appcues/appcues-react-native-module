# Using Appcues React Native with Expo

The Appcues React Native module can be easily integrated into an app using Expo.

## Installation and Setup

```sh
$ npx expo install @appcues/react-native
```

The Appcues React Native module includes native code, so you must generate a new development build. If you use Continuous Native Generation (CNG), run `npx expo prebuild`, otherwise follow the [Create a development build](https://docs.expo.dev/develop/development-builds/create-a-build/) guide.

```js
import * as Appcues from '@appcues/react-native';

await Appcues.setup('APPCUES_ACCOUNT_ID', 'APPCUES_APPLICATION_ID');

Appcues.identify('my-user-id');

Appcues.screen('My Screen Name');
```

## Push Notifications

Appcues maintains `@appcues/expo-config`, an Expo config plugin that automatically configures push notifications for your app. See https://github.com/appcues/appcues-expo-module for details.
