# appcues-react-native-module

Native Module to bridge the native Appcues SDKs in a React Native application.

## Setup

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
