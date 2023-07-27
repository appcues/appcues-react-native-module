#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(AppcuesFrameViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(frameID, NSString)
RCT_EXPORT_VIEW_PROPERTY(fixedSize, BOOL)

@end
