# Using Appcues React Native with the Expo Bare Workflow

The Appcues React Native module can be easily integrated into an app using the Expo Bare Workflow.

## Installation and Setup

```sh
$ expo install @appcues/react-native
```

```js
import * as Appcues from '@appcues/react-native';

await Appcues.setup('APPCUES_ACCOUNT_ID', 'APPCUES_APPLICATION_ID');

Appcues.identify('my-user-id');

Appcues.screen('My Screen Name');
```

## Usage with Expo Go

If you use the Expo Go app to run your bare project, review the [Using Expo Go in Bare Workflow])https://docs.expo.dev/bare/using-expo-client/) Expo doc. Specifically, consider following the wrapper approach that checks if `NativeModules.AppcuesReactNative` exists and falls back to a no-op or `console.log`. See the code snippet in the [Expo Managed Workflow doc](https://github.com/appcues/appcues-react-native-module/blob/main/docs/ExpoManagedWorkflow.md).

The Appcues SDK will not function in the Expo Go app, but would work when you deploy your bare app.
