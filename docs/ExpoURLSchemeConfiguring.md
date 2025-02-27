# Configuring the Appcues URL Scheme with the Expo Managed Workflow

The Appcues React Native Module includes support for a custom URL scheme that supports previewing Appcues experiences in-app prior to publishing and launching the Appcues debugger. This functionality can also be utilized in an Expo Managed Workflow app.

This guide assumes you're using the `AppcuesWrapper` approach described in [Using Appcues React Native with the Expo Managed Workflow](https://github.com/appcues/appcues-react-native-module/blob/main/docs/ExpoManagedWorkflow.md).

## Register the Custom URL Scheme

Add a [scheme](https://docs.expo.dev/versions/latest/config/app/#scheme) property in your `app.json` with a value of `appcues-APPCUES_APPLICATION_ID` where you replace `APPCUES_APPLICATION_ID` with your app's Appcues Application ID:

```json
{
  "expo": {
    ...
    "scheme": "appcues-APPCUES_APPLICATION_ID"
  }
}
```

## Handle the Custom URL Scheme

Install the [expo-linking](https://docs.expo.dev/versions/latest/sdk/linking) package which provides support for parsing deep links into your app:

```sh 
$ npx expo install expo-linking
```

Use the `useURL()` hook to listen to URL changes and pass any new URLs to the Appcues SDK to try and handle.

```js
import { useEffect } from 'react';
import * as Linking from 'expo-linking';
import * as AppcuesWrapper from './AppcuesWrapper';

const url = Linking.useURL();
useEffect(() => {
  AppcuesWrapper.didHandleURL(url).then((appcuesDidHandleURL) => {
    if (!appcuesDidHandleURL) {
      // Handle a non-Appcues URL
    }
  });
}, [url]);
```

## Verifying the Custom URL Scheme

A new Development Build or your app must to be created to be able to use the newly registered URL Scheme. Review [Creating a Development Build](https://github.com/appcues/appcues-react-native-module/blob/main/docs/ExpoManagedWorkflow.md#create-a-development-build) for details.

To verify the URL scheme is registered and properly handled, open the Appcues debugger in your app with `AppcuesWrapper.debug()`, and tap the Appcues Deep Link row to check the configuration. If you see a green check mark, everything is set up properly!

Note that the `useURL()` hook is only called when the link value changes, so if you're using the Appcues Mobile Builder preview functionality multiple times in a row, the URL handler will not be called. A simple workaround is to use the shake-to-refresh feature of the mobile flow preview: when previewing a mobile flow, shake your device to reload the preview using the latest changes from the Mobile Builder.
