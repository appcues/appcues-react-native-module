#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(AppcuesReactNative, RCTEventEmitter)

RCT_EXTERN_METHOD(setup:(NSString *)accountID applicationID:(NSString *) options:(NSDictionary *) additionalAutoProperties:(NSDictionary *) resolver:(RCTPromiseResolveBlock) rejecter:(RCTPromiseRejectBlock))

RCT_EXTERN_METHOD(identify:(NSString *)userID properties:(NSDictionary *))

RCT_EXTERN_METHOD(reset)

RCT_EXTERN_METHOD(anonymous)

RCT_EXTERN_METHOD(group:(NSString *)groupID properties:(NSDictionary *))

RCT_EXTERN_METHOD(screen:(NSString *)title properties:(NSDictionary *))

RCT_EXTERN_METHOD(track:(NSString *)name properties:(NSDictionary *))

RCT_EXTERN_METHOD(show:(NSString *)experienceID resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(debug)

RCT_EXTERN_METHOD(didHandleURL:(NSString *)url resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)

@end
