#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <AppcuesReactNativeSpec/AppcuesReactNativeSpec.h>
@interface AppcuesReactNative: RCTEventEmitter <NativeAppcuesReactNativeSpec>
#else
#import <React/RCTBridgeModule.h>
@interface AppcuesReactNative: RCTEventEmitter <RCTBridgeModule>
#endif

@end
