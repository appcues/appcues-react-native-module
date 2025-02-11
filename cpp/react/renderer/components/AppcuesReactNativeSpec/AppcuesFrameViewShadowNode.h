#pragma once

#include <react/renderer/components/AppcuesReactNativeSpec/EventEmitters.h>
#include <react/renderer/components/AppcuesReactNativeSpec/Props.h>
#include <react/renderer/components/AppcuesReactNativeSpec/AppcuesFrameViewState.h>
#include <react/renderer/components/view/ConcreteViewShadowNode.h>
#include <jsi/jsi.h>

namespace facebook::react {

JSI_EXPORT extern const char AppcuesFrameViewComponentName[];

/*
 * `ShadowNode` for <AppcuesFrameView> component.
 */
class AppcuesFrameViewShadowNode final : public ConcreteViewShadowNode<
    AppcuesFrameViewComponentName,
    AppcuesFrameViewProps,
    AppcuesFrameViewEventEmitter,
    AppcuesFrameViewState> {
  public:
  using ConcreteViewShadowNode::ConcreteViewShadowNode;
};

} // namespace facebook::react
