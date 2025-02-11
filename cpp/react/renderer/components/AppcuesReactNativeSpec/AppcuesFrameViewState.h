#pragma once

#include <react/renderer/graphics/Size.h>

#ifdef ANDROID
#include <folly/dynamic.h>
#endif

namespace facebook::react {

class AppcuesFrameViewState {
public:
  AppcuesFrameViewState() = default;
  AppcuesFrameViewState(Size size): frameSize(size) {}

#ifdef ANDROID
  AppcuesFrameViewState(AppcuesFrameViewState const &previousState, folly::dynamic data): frameSize(Size{
    (Float)data["frameWidth"].getDouble(),
    (Float)data["frameHeight"].getDouble()
  }){};

  folly::dynamic getDynamic() const {
    return {};
  };
#endif

  const Size frameSize;

};

} // namespace facebook::react
