# Registering Custom Fonts with the Expo Managed Workflow

[Expo Font](https://docs.expo.dev/versions/latest/sdk/font) is a library that allows loading fonts at runtime and using them in React Native components.

This guide shows how to set up fonts in your Expo project to work in the Appcues React Native module.

- [Registering Custom Fonts with the Expo Managed Workflow](#registering-custom-fonts-with-the-expo-managed-workflow)
  - [Naming Font Files](#naming-font-files)
  - [Expo SDK 50 and above](#expo-sdk-50-and-above)
  - [Expo SDK 49 and below](#expo-sdk-49-and-below)
    - [Configuring the Native Projects](#configuring-the-native-projects)
    - [Registering Custom fonts with iOS](#registering-custom-fonts-with-ios)
    - [Summary](#summary)

## Naming Font Files

The file name for a font must match the PostScript name of the font for your flows to properly load the custom font on both Android and iOS.
This is because in native Android apps, fonts are referenced by their resource name which is the filename[^1], while in native iOS apps, fonts are referenced by their PostScript name[^2].

On macOS, you can find the PostScript name of a font by opening it with the Font Book app and selecting the Font Info tab.

## Expo SDK 50 and above

We recommend using the Expo Font config plugin to directly embed your fonts in your native project.

```json
// app.json
{
  "expo": {
    ...
    "plugins": [
      [
        "expo-font",
        {
          "fonts": [
            "./assets/fonts/Mulish-Regular.ttf"
          ]
        }
      ]
    ]
  }
}
```

Refer to the [Expo Font documentation](https://docs.expo.dev/develop/user-interface/fonts/#embed-the-font-in-your-native-project) for more details.

## Expo SDK 49 and below

For Expo SDK 49 and below, Expo Font only supports runtime font loading which is incompatible with the Appcues React Native module because these fonts are not configured or registered with the mobile OS making them inaccessible to for usage in your Appcues flows. The following steps configure a custom post-install script to give the same result as the configuration above.

### Configuring the Native Projects

For an Android build, the font files need to be copied to the expected directory. For an iOS build, the font files need to be added to the Xcode project[^3]. To do this while maintaining support for the Expo managed workflow and builds with EAS, update your package.json file with a `eas-build-post-install` hook[^4] that utilizes two custom scripts:

```json
// package.json
{
  "name": "my-app",
  "scripts": {
    "start": "expo start",
    "eas-build-post-install": "./configure-app-fonts.sh"
  },
  "dependencies": {
    "@appcues/react-native": "*",
    "expo": "*"
    ...
  }
}
```

The contents of configure-app-fonts.sh:

```sh
#!/bin/bash

# Assumes that source font files are located in ./assets/fonts

if [[ "$EAS_BUILD_PLATFORM" == "android" ]]; then
  echo "Copy fonts to resources"
  mkdir -p ./android/app/src/main/assets/fonts && cp ./assets/fonts/*.ttf "$_"
  ls -1 ./android/app/src/main/assets/fonts
elif [[ "$EAS_BUILD_PLATFORM" == "ios" ]]; then
  ruby ./bundle-ios-fonts.rb
fi
```

The contents of bundle-ios-fonts.rb:

```rb
# Assumes that source font files are located in ./assets/fonts
# This script is meant to be run only once per xcodeproj because it doesn't check if a resource is already added

# Can rely on this being installed because cocoapods is installed
require 'xcodeproj'

puts 'Add fonts to bundle'

project_path = Dir["ios/*.xcodeproj"].first
project = Xcodeproj::Project.open(project_path)
target = project.targets.first

resource_paths = Dir['assets/fonts/*.ttf'].map { |file_path| project.new_file("../#{file_path}") }
target.add_resources(resource_paths)

# Print the files for debugging
puts resource_paths

puts 'Saving Xcode project'
project.save
```

> Note: these scripts assume your font files are located in `assets/fonts` in your expo project. If this is not the case, update the paths in the scripts (`cp ./<path>/*.ttf "$_"` and `Dir['<path>/*.ttf']`).

### Registering Custom fonts with iOS

After adding the font file to your project, you need to let iOS know about the font. This is done by adding a key `UIAppFonts` to the iOS projects Info.plist. The value is an array value with the name of the font file as an item of the array. Be sure to include the file extension as part of the name.

To set the Info.plist value in your Expo project, add an array of your font file names in your app.json at `expo.ios.infoPlist.UIAppFonts`:

```json
{
  "expo": {
    ...
    "ios": {
      ...
      "infoPlist": {
        "UIAppFonts": [
          "Mulish-Regular.ttf",
          "Mulish-Bold.ttf"
        ]
      }
    }
  }
}
```

### Summary

Your project should look like this:

```
📁 assets
   📁 fonts
      📄 Mulish-Regular.ttf // File name matches PostScript name
      📄 Mulish-Bold.ttf    // File name matches PostScript name
📄 app.json                 // Updated with UIAppFonts values
📄 package.json             // Updated with eas-build-post-install hook
📄 configure-app-fonts.sh   // New file, must be executable (chmod +x configure-app-fonts.sh)
📄 bundle-ios-fonts.rb      // New file
```

Create new development and production builds of your app with these changes. You can use the Available Fonts section of the Appcues Debugger in a new build to verify that fonts are properly installed. If successful, your custom fonts will show in the App-Specific Fonts section of the Available Fonts detail screen.

[^1]: "The resource name, which is either the filename excluding the extension..." https://developer.android.com/guide/topics/resources/providing-resources
[^2]: "When retrieving the font with `custom(_:size:)`, match the name of the font with the font’s PostScript name." https://developer.apple.com/documentation/swiftui/applying-custom-fonts-to-text/#Apply-a-font-supporting-dynamic-sizing
[^3]: Refer to https://developer.apple.com/documentation/uikit/text_display_and_fonts/adding_a_custom_font_to_your_app
[^4]: Refer to https://docs.expo.dev/build-reference/npm-hooks/
