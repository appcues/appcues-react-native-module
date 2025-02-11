#pragma once

#include <react/renderer/components/AppcuesReactNativeSpec/Props.h>
#include <react/renderer/core/ConcreteComponentDescriptor.h>
#include <react/renderer/components/AppcuesReactNativeSpec/AppcuesFrameViewShadowNode.h>

namespace facebook::react {

class AppcuesFrameViewComponentDescriptor final
    : public ConcreteComponentDescriptor<AppcuesFrameViewShadowNode> {
 public:
  using ConcreteComponentDescriptor::ConcreteComponentDescriptor;

  void adopt(ShadowNode& shadowNode) const override {
    auto& layoutableShadowNode =
        static_cast<YogaLayoutableShadowNode&>(shadowNode);
    auto& stateData =
        static_cast<const AppcuesFrameViewShadowNode::ConcreteState&>(
            *shadowNode.getState())
            .getData();

    if (stateData.frameSize.width > 0) {
        layoutableShadowNode.setSize(Size{stateData.frameSize.width, stateData.frameSize.height});
    }

    ConcreteComponentDescriptor::adopt(shadowNode);
  }
};

} // namespace facebook::react
