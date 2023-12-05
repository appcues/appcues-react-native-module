import {
  requireNativeComponent,
  NativeModules,
  Platform,
  UIManager,
  type ViewStyle,
} from 'react-native';

interface ReactNativeOptions {
  logging?: boolean;
  apiHost?: string;
  sessionTimeout?: number;
  activityStorageMaxSize?: number;
  activityStorageMaxAge?: number;
  additionalAutoProperties?: any;
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
): Promise<void> {
  return AppcuesReactNative.setup(accountID, applicationID, options, {
    _applicationFramework: 'react-native',
    _applicationFrameworkVersion: require('react-native/package.json').version,
  });
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

const ComponentName = 'AppcuesFrameView';

type AppcuesFrameProps = {
  frameID: string;
  style?: ViewStyle;
};

export const AppcuesFrameView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<AppcuesFrameProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
