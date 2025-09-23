cmake_minimum_required(VERSION 3.13)
project(AppcuesReactNative)

set(CMAKE_VERBOSE_MAKEFILE on)

file(GLOB_RECURSE cpp_SRCS CONFIGURE_DEPENDS ../cpp/*.cpp)
file(GLOB_RECURSE react_codegen_SRCS CONFIGURE_DEPENDS ./build/generated/source/codegen/jni/*.cpp)

add_library(
  react_codegen_AppcuesReactNativeSpec
  OBJECT
  ${cpp_SRCS}
  ${react_codegen_SRCS}
)

target_include_directories(react_codegen_AppcuesReactNativeSpec PUBLIC ../cpp)
target_include_directories(react_codegen_AppcuesReactNativeSpec PUBLIC ./build/generated/source/codegen/jni)

# https://github.com/react-native-community/discussions-and-proposals/discussions/816
# This if-then-else can be removed once this library does not support version below 0.76
# https://github.com/facebook/react-native/blob/68453a1f90b4a21b3f48cba07ab3c820fda32dac/packages/react-native-codegen/src/generators/modules/GenerateModuleJniH.js#L91-L104
if(ReactAndroid_VERSION_MINOR GREATER_EQUAL 76)
  target_link_libraries(
    react_codegen_AppcuesReactNativeSpec
    fbjni
    jsi
    # We need to link different libraries based on whether we are building rncore or not, that's necessary
    # because we want to break a circular dependency between react_codegen_rncore and reactnative
    reactnative
  )
else()
  target_link_libraries(
    react_codegen_AppcuesReactNativeSpec
    fbjni
    folly_runtime
    glog
    jsi
    react_codegen_rncore
    react_debug
    react_nativemodule_core
    react_render_componentregistry
    react_render_core
    react_render_debug
    react_render_graphics
    react_render_imagemanager
    react_render_mapbuffer
    react_utils
    rrc_image
    rrc_view
    turbomodulejsijni
    yoga
  )
endif()

if(ReactAndroid_VERSION_MINOR GREATER_EQUAL 80)
  target_compile_reactnative_options(react_codegen_AppcuesReactNativeSpec PRIVATE)
else()
  target_compile_options(
    react_codegen_AppcuesReactNativeSpec
    PRIVATE
    -DLOG_TAG=\"ReactNative\"
    -fexceptions
    -frtti
    -std=c++20
    -Wall
  )
endif()
