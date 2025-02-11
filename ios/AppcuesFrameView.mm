#ifdef RCT_NEW_ARCH_ENABLED

#import "AppcuesFrameView.h"

#import "AppcuesFrameViewComponentDescriptor.h"
#import <react/renderer/components/AppcuesReactNativeSpec/EventEmitters.h>
#import <react/renderer/components/AppcuesReactNativeSpec/Props.h>
#import <react/renderer/components/AppcuesReactNativeSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

// This condition is needed to support use_frameworks.
#if __has_include("appcues_react_native-Swift.h")
#import "appcues_react_native-Swift.h"
#else
#import "appcues_react_native/appcues_react_native-Swift.h"
#endif

using namespace facebook::react;

@interface AppcuesFrameView () <RCTAppcuesFrameViewViewProtocol, WrapperSizeDelegate>

@end

@implementation AppcuesFrameView {
    AppcuesFrameWrapperView *_view;
    AppcuesFrameViewShadowNode::ConcreteState::Shared _state;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<AppcuesFrameViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const AppcuesFrameViewProps>();
    _props = defaultProps;

      _view = [[AppcuesFrameWrapperView alloc] initWithUiManager:nil];
      _view.delegate = self;

      self.contentView = _view;
    }

  return self;
}

+ (BOOL)shouldBeRecycled
{
  return NO;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<AppcuesFrameViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<AppcuesFrameViewProps const>(props);

    if (oldViewProps.frameID != newViewProps.frameID) {
        NSString * frameID = [[NSString alloc] initWithUTF8String: newViewProps.frameID.c_str()];
        _view.frameID = frameID;
    }

    [super updateProps:props oldProps:oldProps];
}

- (void)updateState:(State::Shared const &)state oldState:(State::Shared const &)oldState
{
  _state = std::static_pointer_cast<const AppcuesFrameViewShadowNode::ConcreteState>(state);
}

- (void) setSizeTo:(CGSize)size
{
    _state->updateState(AppcuesFrameViewState({size.width, size.height}));
}

Class<RCTComponentViewProtocol> AppcuesFrameViewCls(void)
{
    return AppcuesFrameView.class;
}

@end

#endif
