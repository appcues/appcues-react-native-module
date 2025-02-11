# Appcues React Native Example App

This is a simple Android and iOS app built with React Native that integrates with Appcues React Native Module.

## 🚀 Setup

Refer to https://reactnative.dev/docs/environment-setup for general React Native setup. This example project uses the React Native CLI.

```sh
# Install dependencies for the module. Only necessary because this is referenced locally by the example app.
yarn install

# Install dependencies for the example app.
cd ./example
yarn install

# Install cocoapods
cd ./ios
bundle install # One time only
bundle exec pod install
```

This example app requires you to fill in an Appcues Account ID and an Appcues Application ID in `App.js`. You can enter your own values found in [Appcues Studio](https://studio.appcues.com), or use the following test values:
```
APPCUES_ACCOUNT_ID=103523
APPCUES_APPLICATION_ID=ca73c634-1978-46b4-b73d-eb3367a66925
```

```sh
# Run the app for Android
yarn android

# Run the app for iOS
yarn ios
```

## ✨ Functionality

The example app demonstrates the core functionality of the Appcues React Native Module across 4 screens.

### Sign In Screen

This screen is identified as `Sign In` for screen targeting.

Provide a User ID for use with `Appcues.identify()` or select an anonymous ID using `Appcues.anonymous()`.

### Events Screen

This screen is identified as `Trigger Events` for screen targeting.

Two buttons demonstrate `Appcues.track()` calls.

The navigation bar also includes a button to launch the in-app debugger with `Appcues.debug()`.

### Profile Screen

This screen is identified as `Update Profile` for screen targeting.

Textfields are included to update the profile attributes for the current user using `Appcues.identify()`.

The navigation bar also includes a button to sign out and navigate back to the Sign In Screen along with calling `Appcues.reset()`.

### Group Screen

This screen is identified as `Update Group` for screen targeting.

A textfield is included to set the group for the current user using `Appcues.group()`.
