#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import "RCTBridge.h"

// This condition is needed to support use_frameworks.
#if __has_include("appcues_react_native-Swift.h")
#import "appcues_react_native-Swift.h"
#else
#import "appcues_react_native/appcues_react_native-Swift.h"
#endif

@interface AppcuesFrameViewManager : RCTViewManager
@end

@implementation AppcuesFrameViewManager

RCT_EXPORT_MODULE(AppcuesFrameView)

#ifndef RCT_NEW_ARCH_ENABLED
- (UIView *)view
{
  return [[AppcuesFrameWrapperView alloc] initWithUiManager:self.bridge.uiManager];
}
#endif

RCT_EXPORT_VIEW_PROPERTY(frameID, NSString)

@end
