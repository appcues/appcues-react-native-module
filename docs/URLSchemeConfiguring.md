# Configuring the Appcues URL Scheme

The Appcues React Native Module includes support for a custom URL scheme that supports previewing Appcues experiences in-app prior to publishing and launching the Appcues debugger.

## Prerequisites

Your app must be configured to support [Linking](https://reactnative.dev/docs/linking). Ensure your application is configured for both Android and iOS by following the guide for [Enabling Deep Linking](https://reactnative.dev/docs/linking#enabling-deep-links).

## Register the Custom URL Scheme

Add the Appcues scheme to your project configuration. Replace `APPCUES_APPLICATION_ID` in the snippet below with your app's Appcues Application ID.

For example, if your Appcues Application ID is `123-xyz` your url scheme value would be `appcues-123-xyz`.

```sh
npx uri-scheme add appcues-APPCUES_APPLICATION_ID --ios
npx uri-scheme add appcues-APPCUES_APPLICATION_ID --android
```

## Handle the Custom URL Scheme

URL's need to be handled with an event listener:

```js
import { Linking } from "react-native";
import * as Appcues from '@appcues/react-native';

Linking.addEventListener('url', async ({ url }) => {
  const appcuesDidHandleURL = await Appcues.didHandleURL(url);

  if (!appcuesDidHandleURL) {
    // Handle a non-Appcues URL
  }
});
```

## Verifying the Custom URL Scheme

Test that the URL scheme handling is set up correctly by opening the Appcues debugger:

```sh
# ios
npx uri-scheme open "appcues-APPCUES_APPLICATION_ID://sdk/debugger" --ios

# android
npx uri-scheme open "appcues-APPCUES_APPLICATION_ID://sdk/debugger" --android
```
