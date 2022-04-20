# appcues-react-native-module

Native Module to bridge the native Appcues SDKs in a React Native application.

## Setup

**NOTE:** because the Appcues iOS podspec isn't public, the Appcues SDK must be references locally. The Podfile in `./example/ios` will need to be updated to match your local path to the Appcues iOS SDK (if your `appcues-ios-sdk` repo is a sibling to `appcues-react-native-sdk` you should already be good to go).

Refer to https://reactnative.dev/docs/environment-setup for general React Native Setup. This project uses the React Native CLI (not Expo, which might work, but is untested)

```sh
# need node, watchman, yarn, cocoapods
brew install node
brew install watchman
npm install -g yarn
gem install cocoapods
```

```sh
yarn install

cd ./example
yarn install

cd ./iOS
pod install
```

## Running

### iOS

Page views and events are tracked.

```
open ./example/ios/AppcuesReactNativeExample.xcworkspace
```

## Modifying

If you're changing the plugin, you need to `yarn install` to sync plugin changes to the test app (React Native doesnt support symlinked modules).

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
