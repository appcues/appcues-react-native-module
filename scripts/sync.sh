#!/bin/sh

# compile typescript to ./lib
yarn prepare

# copy relevant files to example app
cp -R ./android ./example/node_modules/@appcues/react-native
cp -R ./ios ./example/node_modules/@appcues/react-native
cp -R ./lib ./example/node_modules/@appcues/react-native
# ./src isn't strictly necessary, but copying anyways to avoid confusion
cp -R ./src ./example/node_modules/@appcues/react-native
