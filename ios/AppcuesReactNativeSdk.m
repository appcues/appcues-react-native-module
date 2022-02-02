#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(AppcuesReactNativeSdk, NSObject)

RCT_EXTERN_METHOD(setup:(NSString *)accountID applicationID:(NSString *))

RCT_EXTERN_METHOD(identify:(NSString *)userID properties:(NSDictionary *))

RCT_EXTERN_METHOD(screen:(NSString *)title properties:(NSDictionary *))

RCT_EXTERN_METHOD(track:(NSString *)name properties:(NSDictionary *))

RCT_EXTERN_METHOD(show:(NSString *)experienceID)

RCT_EXTERN_METHOD(debug)

@end
