/**
 * @type {import('@react-native-community/cli-types').UserDependencyConfig}
 */
module.exports = {
  dependency: {
    platforms: {
      android: {
        componentDescriptors: ['AppcuesFrameViewComponentDescriptor'],
        cmakeListsPath: './CMakeLists.txt',
      },
    },
  },
};
