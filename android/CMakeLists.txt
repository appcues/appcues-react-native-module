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

target_link_libraries(
  react_codegen_AppcuesReactNativeSpec
  fbjni
  jsi
  # We need to link different libraries based on whether we are building rncore or not, that's necessary
  # because we want to break a circular dependency between react_codegen_rncore and reactnative
  reactnative
)

target_compile_options(
  react_codegen_AppcuesReactNativeSpec
  PRIVATE
  -DLOG_TAG=\"ReactNative\"
  -fexceptions
  -frtti
  -std=c++20
  -Wall
)
