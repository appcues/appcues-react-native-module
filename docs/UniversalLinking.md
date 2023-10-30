# Universal Linking

Universal links are an elegant solution for deep linking in a React Native project. Refer to the [React Native doc on Linking](https://reactnative.dev/docs/linking) for details.

## iOS Considerations

React Native apps supporting universal linking typically use [this snippet](https://reactnative.dev/docs/linking#enabling-deep-links) which calls `RCTLinkingManager`:

```objc
- (BOOL)application:(UIApplication *)application continueUserActivity:(nonnull NSUserActivity *)userActivity
 restorationHandler:(nonnull void (^)(NSArray<id<UIUserActivityRestoring>> * _Nullable))restorationHandler
{
 return [RCTLinkingManager application:application
                  continueUserActivity:userActivity
                    restorationHandler:restorationHandler];
}
```

Implementing the `UIApplicationDelegate.application(_:continue:restorationHandler:)` function like above causes all links (deep links _and_ regular web links) from the Appcues SDK to be passed to your implementation of `Linking.addEventListener('url', callback)`. If your `Linking` callback is not prepared for non-deep links, the result will be button links in Appcues flows being broken.

To resolve this, you may define a custom key, `AppcuesUniversalLinkHostAllowList`, in your _Info.plist_ file with an array of (string) host values to control which URLs are passed to `RCTLinkingManager` to be handled as a univeral link. A URL whose host matches any of the provided host values will be treated as a universal link and passed to `Linking.addEventListener('url', callback)`. Non matching URLs will be handled by the Appcues SDK.

If the `AppcuesUniversalLinkHostAllowList` key is absent from _Info.plist_, all https links will be treated as potential universal links.

Example _Info.plist_:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    ...
    <key>AppcuesUniversalLinkHostAllowList</key>
    <array>
        <string>my-domain.com</string>
    </array>
</dict>
</plist>
```

For apps using Expo, the `AppcuesUniversalLinkHostAllowList` value can be added in _app.json_ in the [ios.infoPlist](https://docs.expo.dev/versions/latest/config/app/#infoplist) object:

```json
{
  "expo": {
    ...
    "ios": {
      "infoPlist": {
        "AppcuesUniversalLinkHostAllowList": ["my-domain.com"]
      }
    }
  }
}
```
