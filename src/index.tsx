import { NativeEventEmitter, NativeModules } from 'react-native';

interface ReactNativeOptions {
  logging?: boolean;
  apiHost?: string;
  settingsHost?: string;
  sessionTimeout?: number;
  activityStorageMaxSize?: number;
  activityStorageMaxAge?: number;
  additionalAutoProperties?: object;
  enableTextScaling?: boolean;
  enableStepRecoveryObserver?: boolean;
}

const LINKING_ERROR = `The package '@appcues/react-native' doesn't seem to be linked.`;

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const Module = isTurboModuleEnabled
  ? require('./NativeAppcuesReactNative').default
  : NativeModules.AppcuesReactNative;

const AppcuesReactNative = Module
  ? Module
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
): Promise<void> {
  const rnVersion = require('react-native/package.json').version;
  const augmentedOptions = {
    ...options,
    additionalAutoProperties: {
      ...options?.additionalAutoProperties,
      _applicationFramework: 'react-native',
      _applicationFrameworkVersion: rnVersion,
    },
  };
  return AppcuesReactNative.setup(accountID, applicationID, augmentedOptions);
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

export function group(groupID?: string, properties?: object) {
  AppcuesReactNative.group(groupID, properties);
}

export function screen(title: string, properties?: object) {
  AppcuesReactNative.screen(title, properties);
}

export function track(name: string, properties?: object) {
  AppcuesReactNative.track(name, properties);
}

export function show(experienceID: string): Promise<void> {
  return AppcuesReactNative.show(experienceID);
}

export function debug() {
  AppcuesReactNative.debug();
}

export function didHandleURL(url: string): Promise<boolean> {
  return AppcuesReactNative.didHandleURL(url);
}

export const analyticsEventEmitter = new NativeEventEmitter(Module);

export { default as AppcuesFrameView } from './AppcuesFrameViewNativeComponent';
export * from './AppcuesFrameViewNativeComponent';
