import { NativeModules, Platform } from 'react-native';

interface ReactNativeOptions {
  logging?: boolean;
  apiHost?: string;
  sessionTimeout?: number;
  activityStorageMaxSize?: number;
  activityStorageMaxAge?: number;
}

const LINKING_ERROR =
  `The package 'appcues-react-native' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n' +
  'If you are using the Expo managed workflow, please refer to the Appcues React Native module documentation for Expo.';

const AppcuesReactNative = NativeModules.AppcuesReactNative
  ? NativeModules.AppcuesReactNative
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function setup(
  accountID: string,
  applicationID: string,
  options?: ReactNativeOptions
) {
  AppcuesReactNative.setup(accountID, applicationID, options);
}

export function identify(userID: string, properties?: object) {
  AppcuesReactNative.identify(userID, properties);
}

export function reset() {
  AppcuesReactNative.reset();
}

export function anonymous() {
  AppcuesReactNative.anonymous();
}

export function group(groupID: string, properties?: object) {
  AppcuesReactNative.group(groupID, properties);
}

export function screen(title: string, properties?: object) {
  AppcuesReactNative.screen(title, properties);
}

export function track(name: string, properties?: object) {
  AppcuesReactNative.track(name, properties);
}

export function show(experienceID: string) {
  AppcuesReactNative.show(experienceID);
}

export function debug() {
  AppcuesReactNative.debug();
}

export async function didHandleURL(url: string): Promise<boolean> {
  return await AppcuesReactNative.didHandleURL(url);
}
