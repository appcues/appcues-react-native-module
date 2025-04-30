#import "AppcuesReactNative.h"

#ifdef RCT_NEW_ARCH_ENABLED
#import <AppcuesReactNativeSpec/AppcuesReactNativeSpec.h>
#endif

// This condition is needed to support use_frameworks.
#if __has_include("appcues_react_native-Swift.h")
#import "appcues_react_native-Swift.h"
#else
#import "appcues_react_native/appcues_react_native-Swift.h"
#endif

@interface AppcuesReactNative () <EventDelegate>
@end

@implementation AppcuesReactNative {
    Implementation *implementation;
    BOOL hasListeners;
}

- (instancetype)init {
  self = [super init];
  if(self) {
      implementation = [Implementation new];
      implementation.delegate = self;
      hasListeners = NO;
  }
  return self;
}

RCT_EXPORT_MODULE()

+ (BOOL)requiresMainQueueSetup {
  return NO;
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)setup:(NSString *)accountID applicationID:(NSString *)applicationID options:(JS::NativeAppcuesReactNative::ReactNativeOptions &)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {

    NSMutableDictionary *optionsDict = [NSMutableDictionary dictionary];

    // Add items to the dictionary
    if (options.logging().has_value()) {
        optionsDict[@"logging"] = [NSNumber numberWithBool:options.logging().value()];
    }
    optionsDict[@"apiHost"] = options.apiHost();
    if (options.sessionTimeout().has_value()) {
        optionsDict[@"sessionTimeout"] = @(options.sessionTimeout().value());
    }
    if (options.activityStorageMaxSize().has_value()) {
        optionsDict[@"activityStorageMaxSize"] = @(options.activityStorageMaxSize().value());
    }
    if (options.activityStorageMaxAge().has_value()) {
        optionsDict[@"activityStorageMaxAge"] = @(options.activityStorageMaxAge().value());
    }
    optionsDict[@"additionalAutoProperties"] = options.additionalAutoProperties();
    if (options.enableTextScaling().has_value()) {
        optionsDict[@"enableTextScaling"] = [NSNumber numberWithBool:options.enableTextScaling().value()];
    }
    if (options.enableStepRecoveryObserver().has_value()) {
        optionsDict[@"enableStepRecoveryObserver"] = [NSNumber numberWithBool:options.enableStepRecoveryObserver().value()];
    }

    [implementation setupWithAccountID:accountID
                         applicationID:applicationID
                               options:optionsDict
                               resolve:resolve
                                reject:reject];
}
#else
RCT_EXPORT_METHOD(setup:(NSString *)accountID applicationID:(NSString *)applicationID options:(NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [implementation setupWithAccountID:accountID
                         applicationID:applicationID
                               options:options
                               resolve:resolve
                                reject:reject];
}

#endif

RCT_EXPORT_METHOD(identify:(NSString *)userID properties:(NSDictionary *)properties) {
    [implementation identifyWithUserID:userID
                            properties:properties];
}

RCT_EXPORT_METHOD(reset) {
    [implementation reset];
}

RCT_EXPORT_METHOD(anonymous) {
    [implementation anonymous];
}

RCT_EXPORT_METHOD(group:(NSString *)groupID properties:(NSDictionary *)properties) {
    [implementation groupWithGroupID:groupID
                          properties:properties];
}

RCT_EXPORT_METHOD(screen:(NSString *)title properties:(NSDictionary *)properties) {
    [implementation screenWithTitle:title
                         properties:properties];
}

RCT_EXPORT_METHOD(track:(NSString *)name properties:(NSDictionary *)properties) {
    [implementation trackWithName:name
                       properties:properties];
}

RCT_EXPORT_METHOD(show:(NSString *)experienceID resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [implementation showWithExperienceID:experienceID
                                resolver:resolve
                                rejecter:reject];
}

RCT_EXPORT_METHOD(debug) {
    [implementation debug];
}

RCT_EXPORT_METHOD(didHandleURL:(NSString *)url resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [implementation didHandleURLWithUrl:url
                               resolver:resolve
                               rejecter:reject];
}


- (NSArray<NSString *> *)supportedEvents {
    return @[
        [Implementation eventName]
    ];
}

- (void)sendEventWithName:(NSString *)name result:(NSDictionary<NSString *,id> *)result {
    if (hasListeners) {
        [self sendEventWithName:name
                           body:result];
    }
}

- (void)startObserving {
    hasListeners = YES;
    NSLog(@"START OBSERVING");
}

- (void)stopObserving {
    hasListeners = NO;
    NSLog(@"STOP OBSERVING");
}

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeAppcuesReactNativeSpecJSI>(params);
}
#endif

@end
