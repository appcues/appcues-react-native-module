# Appcues React Native Module

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/appcues/appcues-react-native-module/tree/main.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/gh/appcues/appcues-react-native-module/tree/main)
[![npm](https://img.shields.io/npm/v/@appcues/react-native.svg?logo=npm&logoColor=fff&label=NPM+package&color=limegreen)](https://www.npmjs.com/package/@appcues/react-native)
[![License: MIT](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/appcues/appcues-react-native-module/blob/main/LICENSE)

Appcues React Native Module allows you to integrate Appcues experiences into your React Native apps for iOS and Android devices.

This native module is a bridge between the native Appcues SDKs in a React Native application which sends user properties and events to the Appcues API and retrieves and renders Appcues content based on those properties and events.

- [Appcues React Native Module](#appcues-react-native-module)
  - [🚀 Getting Started](#-getting-started)
    - [Installation](#installation)
    - [One Time Setup](#one-time-setup)
      - [Initializing the SDK](#initializing-the-sdk)
      - [Supporting Debugging and Experience Previewing](#supporting-debugging-and-experience-previewing)
    - [Identifying Users](#identifying-users)
    - [Tracking Screens and Events](#tracking-screens-and-events)
  - [📝 Documentation](#-documentation)
  - [🎬 Examples](#-examples)
  - [👷 Contributing](#-contributing)
  - [📄 License](#-license)

## 🚀 Getting Started

### Prerequisites

**Android** - your application's `build.gradle` must have a `compileSdkVersion` of 33+ and `minSdkVersion` of 21+
```
android {
    compileSdkVersion 33

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

### One Time Setup

#### Initializing the SDK

An instance of the Appcues SDK should be initialized when your app launches.

```js
import * as Appcues from '@appcues/react-native'

await Appcues.setup('APPCUES_ACCOUNT_ID', 'APPCUES_APPLICATION_ID')
```

Initializing the SDK requires you to provide two values, an Appcues account ID, and an Appcues mobile application ID. These values can be obtained from your [Appcues settings](https://studio.appcues.com/settings/account). Refer to the help documentation on [Registering your mobile app in Studio](https://docs.appcues.com/article/848-registering-your-mobile-app-in-studio) for more information. Since the React Native bridge is asynchronous, the `setup` call should be resolved before calling subsequent SDK functions.

#### Supporting Debugging and Experience Previewing

Supporting debugging and experience previewing is not required for the Appcues React Native Module to function, but it is necessary for the optimal Appcues builder experience. Refer to the [URL Scheme Configuration Guide](https://github.com/appcues/appcues-react-native-module/blob/main/docs/URLSchemeConfiguring.md) for details on how to configure.

### Identifying Users

In order to target content to the right users at the right time, you need to identify users and send Appcues data about them. A user is identified with a unique ID.

```js
// Identify a user
Appcues.identify('my-user-id')
// Identify a user with property
Appcues.identify('my-user-id', {'Company': 'Appcues'})
```

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

## 📝 Documentation

More technical documentation about this module (including instruction for usage with Expo Bare and Managed workflow apps) is available in the [`docs` directory](https://github.com/appcues/appcues-react-native-module/tree/main/docs). Full documentation is available at https://docs.appcues.com/

## 🎬 Examples

The `example` directory in this repository contains full example iOS/Android app to providing references for correct installation and usage of the Appcues API.

## 👷 Contributing

See the [contributing guide](https://github.com/appcues/appcues-react-native-module/blob/main/CONTRIBUTING.md) to learn how to get set up for development and how to contribute to the project.

## 📄 License

This project is licensed under the MIT License. See [LICENSE](https://github.com/appcues/appcues-react-native-module/blob/main/LICENSE) for more information.
