import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface ReactNativeOptions {
  logging?: boolean;
  apiHost?: string;
  sessionTimeout?: number;
  activityStorageMaxSize?: number;
  activityStorageMaxAge?: number;
  additionalAutoProperties?: Object;
  enableTextScaling?: boolean;
  enableStepRecoveryObserver?: boolean;
}

export interface Spec extends TurboModule {
  setup(
    accountID: string,
    applicationID: string,
    options?: ReactNativeOptions
  ): Promise<void>;

  identify(userID: string, properties?: Object): void;

  reset(): void;

  anonymous(): void;

  group(groupID?: string, properties?: Object): void;

  screen(title: string, properties?: Object): void;

  track(name: string, properties?: Object): void;

  show(experienceID: string): Promise<void>;

  debug(): void;

  didHandleURL(url: string): Promise<boolean>;

  // The addListener and removeListeners implementations will be provided by React Native
  addListener: (eventType: string) => void;
  removeListeners: (count: number) => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('AppcuesReactNative');
