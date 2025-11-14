# Configuring the Appcues URL Scheme with Expo

The Appcues React Native Module includes support for a custom URL scheme that supports previewing Appcues experiences in-app prior to publishing and launching the Appcues debugger. This functionality can also be utilized in an Expo app.

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

## Compatibility with Expo Router

Expo Router will always evaluate a URL with the assumption that the URL targets a specific page within the app. To opt Appcues SDK deep links out of this URL handling, create a special file called `+native-intent.tsx` at the top level of your project's **app** directory, and `return null` for Appcues deep links. Refer to the [expo-router documentation](https://docs.expo.dev/router/advanced/native-intent/#rewrite-incoming-native-deep-links) for details.

```js
export function redirectSystemPath({ path, initial }) {
  // If the incoming link starts with the Appcues UYL scheme, skip having the router handle it
  if (path?.startsWith('appcues')) {
    return null;
  }

  // Otherwise proceed as normal
  return path;
}
```

## Verifying the Custom URL Scheme

A new Development Build of your app must to be created to be able to use the newly registered URL Scheme. Review [Creating a Development Build](https://docs.expo.dev/develop/development-builds/introduction/) for details.

To verify the URL scheme is registered and properly handled, open the Appcues debugger in your app with `AppcuesWrapper.debug()`, and tap the Appcues Deep Link row to check the configuration. If you see a green check mark, everything is set up properly!

Note that the `useURL()` hook is only called when the link value changes, so if you're using the Appcues Mobile Builder preview functionality multiple times in a row, the URL handler will not be called. A simple workaround is to use the shake-to-refresh feature of the mobile flow preview: when previewing a mobile flow, shake your device to reload the preview using the latest changes from the Mobile Builder.
