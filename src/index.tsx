import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'appcues-react-native-sdk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const AppcuesReactNativeSdk = NativeModules.AppcuesReactNativeSdk
  ? NativeModules.AppcuesReactNativeSdk
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function setup(accountID: string, applicationID: string) {
  AppcuesReactNativeSdk.setup(accountID, applicationID)
}

export function identify(userID: string, properties?: object) {
  AppcuesReactNativeSdk.identify(userID, properties)
}

export function reset() {
  AppcuesReactNativeSdk.reset()
}

export function anonymous() {
  AppcuesReactNativeSdk.anonymous()
}

export function group(groupID: string, properties?: object) {
  AppcuesReactNativeSdk.group(groupID, properties)
}

export function screen(title: string, properties?: object) {
  AppcuesReactNativeSdk.screen(title, properties)
}

export function track(name: string, properties?: object) {
  AppcuesReactNativeSdk.track(name, properties)
}

export function show(experienceID: string) {
  AppcuesReactNativeSdk.show(experienceID)
}

export function debug() {
  AppcuesReactNativeSdk.debug()
}
