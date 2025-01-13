# Configuring Views for Anchored Tooltips

The Appcues React Native Module supports anchored tooltips targeting any React Native [`View`](https://reactnative.dev/docs/view) in your application's layout.

Instrumenting your application views as described below allows the Appcues React Native Module to create a mobile view selector for each view. This selector is used by the Appcues Mobile Builder to create and target anchored tooltips. When a user qualifies for a flow, this selector is used to render the anchored tooltip content.

## Instrumenting React Native Views

The following `View` properties are used to identify elements, in order of precedence:

* [`nativeID`](https://reactnative.dev/docs/view#nativeid)
* [`testID`](https://reactnative.dev/docs/view#testid)

These value are used to locate the native view that is created on the iOS or Android platform at runtime. At least one identifiable property must be set. The best way to ensure great performance of React Native anchored tooltips in Appcues is to set a unique `nativeID` on each `View` element that may be targeted.

The `nativeID` or `testID` value must be unique on the screen where an anchored tooltip may be targeted.

```js
<Button
  onPress={...}
  title="Save Profile"
  nativeID="btnSaveProfile"
/>
```

### Bottom Tab Navigation

A common use case for anchored tooltips is bottom tab navigation elements. For typical tab layouts using the Bottom Tabs Navigation from the React Navigation library (version 7.x), use the [`tabBarButtonTestID`](https://reactnavigation.org/docs/bottom-tab-navigator/#tabbarbuttontestid) option on the tab items to ensure the normal `testID` selector is set for the resulting view (note the property is called [`tabBarTestID`](https://reactnavigation.org/docs/6.x/bottom-tab-navigator#tabbartestid) in previous versions).

In the example below, three main tabs are defined and provide the `tabBarButtonTestID` for anchored tooltip targeting.
```js
<Tab.Navigator>
    <Tab.Screen
    name="Events"
    component={EventsScreen}
    options={{ title: 'Events', tabBarButtonTestID: 'tabEvents' }}
    />
    <Tab.Screen
    name="Profile"
    component={ProfileScreen}
    options={{ title: 'Profile', tabBarButtonTestID: 'tabProfile' }}
    />
    <Tab.Screen
    name="Group"
    component={GroupScreen}
    options={{ title: 'Group', tabBarButtonTestID: 'tabGroup' }}
    />
</Tab.Navigator>
```

## Other Considerations

### Selector Uniqueness
Ensure that view identifiers used for selectors are unique within the visible views on the screen at the time an anchored tooltip is attempting to render. If no unique match is found, the Appcues flow will terminate with an error. It is not required that selectors are globally unique across the application, but they must be on any given screen layout.

### Consistent View Identifiers
Maintain consistency with view identifiers as new versions of the app are released. For example, if a key navigation tab was using an identifier like "Home Tab" in several versions of the application, then changed to "Home" - this would break the ability for selectors using "Home Tab" to be able to find that view and target a tooltip in the newer versions of the app. You could build multiple flows targeting different versions of the application, but it helps keep things simplest if consistent view identifiers can be maintained over time.
