#pragma once

#include <react/renderer/components/AppcuesReactNativeSpec/AppcuesFrameViewShadowNode.h>
#include <react/renderer/components/AppcuesReactNativeSpec/AppcuesFrameViewComponentDescriptor.h>
#include <react/renderer/core/ConcreteComponentDescriptor.h>
#include <react/renderer/componentregistry/ComponentDescriptorProviderRegistry.h>

namespace facebook::react {

void AppcuesReactNativeSpec_registerComponentDescriptorsFromCodegen(
    std::shared_ptr<const ComponentDescriptorProviderRegistry> registry);

} // namespace facebook::react
