# Appcues React Native Module

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/appcues/appcues-react-native-module/tree/main.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/gh/appcues/appcues-react-native-module/tree/main)
[![npm](https://img.shields.io/npm/v/@appcues/react-native.svg?logo=npm&logoColor=fff&label=NPM+package&color=limegreen)](https://www.npmjs.com/package/@appcues/react-native)
[![License: MIT](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/appcues/appcues-react-native-module/blob/main/LICENSE)

Appcues React Native Module allows you to integrate Appcues experiences into your React Native apps for iOS and Android devices.

This native module is a bridge between the native Appcues SDKs in a React Native application which sends user properties and events to the Appcues API and retrieves and renders Appcues content based on those properties and events.

- [Appcues React Native Module](#appcues-react-native-module)
  - [🚀 Getting Started](#-getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
      - [Standard](#standard-installation)
      - [Segment](#segment)
    - [One Time Setup](#one-time-setup)
      - [Initializing the SDK](#initializing-the-sdk)
      - [Supporting Builder Preview and Screen Capture](#supporting-builder-preview-and-screen-capture)
      - [Enabling Push Notifications](#enabling-push-notifications)
    - [Identifying Users](#identifying-users)
    - [Tracking Screens and Events](#tracking-screens-and-events)
    - [Anchored Tooltips](#anchored-tooltips)
    - [Embedded Experiences](#embedded-experiences)
  - [📝 Documentation](#-documentation)
  - [🎬 Examples](#-examples)
  - [👷 Contributing](#-contributing)
  - [📄 License](#-license)

## 🚀 Getting Started

### Prerequisites

**React Native** - your application should use React Native version 0.73 or above. Applications using Expo should use version 50 or above.

**Android** - your application's `build.gradle` must have a `compileSdkVersion` of 34+ and `minSdkVersion` of 21+. Your project should use the Android Gradle Plugin (AGP) version 8 or above.
```
android {
    compileSdkVersion 34

    defaultConfig {
        minSdkVersion 21
    }
}
```

**iOS** - your application must target iOS 11+ to install the SDK, and iOS 13+ to render Appcues content. Update the iOS project xcodeproj to set the deployment target, if needed. In the application's `Podfile`, include at least this minimum version.
```rb
# Podfile
platform :ios, '11.0'
```

### Installation

Add the Appcues React Native Module dependency to your application. There are options for a standard installation, or for usage through a Segment plugin.

#### Standard Installation

1. In your app's root directory, install the Appcues React Native Module
   ```sh
   npm install --save @appcues/react-native
   # OR
   yarn add @appcues/react-native
   ```
2. Under your application's `ios` folder, run
   ```sh
   pod install
   ```

Note: You do not need to manually update your Podfile to add Appcues.

#### Segment

Appcues supports integration with Segment's [analytics-react-native](https://github.com/segmentio/analytics-react-native) package.  To install with Segment, you'll  use the [Segment Appcues plugin](https://github.com/appcues/segment-appcues-react-native).

### One Time Setup

#### Initializing the SDK

An instance of the Appcues SDK should be initialized when your app launches.

```js
import * as Appcues from '@appcues/react-native'

await Appcues.setup('APPCUES_ACCOUNT_ID', 'APPCUES_APPLICATION_ID')
```

Initializing the SDK requires you to provide two values, an Appcues account ID, and an Appcues mobile application ID. These values can be obtained from your [Appcues settings](https://studio.appcues.com/settings/account). Refer to the help documentation on [Registering your mobile app in Studio](https://docs.appcues.com/article/848-registering-your-mobile-app-in-studio) for more information. Since the React Native bridge is asynchronous, the `setup` call should be resolved before calling subsequent SDK functions.

#### Supporting Builder Preview and Screen Capture

During installation, follow the steps outlined in [Configuring the Appcues URL Scheme](https://github.com/appcues/appcues-react-native-module/blob/main/docs/URLSchemeConfiguring.md). This is necessary for the complete Appcues builder experience, supporting experience preview, screen capture and debugging.

#### Enabling Push Notifications

In a React Native app, push notifications are configured in the native projects for iOS and Android. Please refer to native documentation on [iOS](https://appcues.github.io/appcues-ios-sdk/documentation/appcueskit/pushnotifications/) and [Android](https://github.com/appcues/appcues-android-sdk/blob/main/docs/PushNotification.md) for more details. The `example` directory in this repository contains a reference implementation that includes push notification support. For Expo apps, refer to the [appcues-expo-module](https://github.com/appcues/appcues-expo-module) to integrate push notifications.

### Identifying Users

In order to target content to the right users at the right time, you need to identify users and send Appcues data about them. A user is identified with a unique ID.

```js
// Identify a user
Appcues.identify('my-user-id')
// Identify a user with property
Appcues.identify('my-user-id', {'Company': 'Appcues'})
```

After identifying a user, you can optionally associate that user with group.

```js
// Associate a user with a group, optionally including group properties
Appcues.group('group-id', {'Plan Tier': 'standard'})
```

To ensure the most accurate content targeting based upon group information, it's recommended to supply the group information immediately after a new user is identified.

### Tracking Screens and Events

Events are the “actions” your users take in your application, which can be anything from clicking a certain button to viewing a specific screen. Once you’ve installed and initialized the Appcues React Native Module, you can start tracking screens and events using the following methods:

```js
// Track event
Appcues.track('Sent Message')
// Track event with property
Appcues.track('Deleted Contact', {'ID': 123 })

// Track screen
Appcues.screen('Contact List')
// Track screen with property
Appcues.screen('Contact Details', {'Contact Reference': 'abc'})
```

### Anchored Tooltips

Anchored tooltips use element targeting to point directly at specific views in your application. For more information about how to configure your application's views for element targeting, refer to the [Anchored Tooltips Guide](https://github.com/appcues/appcues-react-native-module/blob/main/docs/AnchoredTooltips.md).

### Embedded Experiences

Add `AppcuesFrameView` instances in your application layouts to support embedded experience content, with a non-modal presentation. For more information about how to configure your application layouts to use frame views, refer to the guide on [Configuring an AppcuesFrameView](https://github.com/appcues/appcues-react-native-module/blob/main/docs/AppcuesFrameViewConfiguring.md).

## 📝 Documentation

More technical documentation about this module (including instruction for usage with Expo Bare and Managed workflow apps) is available in the [`docs` directory](https://github.com/appcues/appcues-react-native-module/tree/main/docs). Full documentation is available at https://docs.appcues.com/

## 🎬 Examples

The `example` directory in this repository contains full example iOS/Android app to providing references for correct installation and usage of the Appcues API.

## 👷 Contributing

See the [contributing guide](https://github.com/appcues/appcues-react-native-module/blob/main/CONTRIBUTING.md) to learn how to get set up for development and how to contribute to the project.

## 📄 License

This project is licensed under the MIT License. See [LICENSE](https://github.com/appcues/appcues-react-native-module/blob/main/LICENSE) for more information.
