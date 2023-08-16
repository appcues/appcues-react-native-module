# Configuring an AppcuesFrameView

Using `AppcuesFrameView` instances in your layouts will allow you to embed inline Appcues experience content in your app. This style of pattern is non-modal in the user's experience, differing from modals and tooltips used in mobile flows. Any number of desired embedded experiences can be rendered in the application at any given time.

## Using an AppcuesFrameView

Insert `AppcuesFrameView` instances wherever you would like Appcues embedded experience content to potentially appear in React Native application layouts. Each frame should use a unique `frameID` (String). This identifier is used when building embedded experiences, informing Appcues the exact location in your app that the content should be targeted.

```js
<AppcuesFrameView frameID="frame1" />
```

By default, these views will not take up any space in the rendered layout. Only when qualified experience content is targeted to these frames will they actually be visible. You can think of this process as reserving placeholder locations in your application UI for potential additional content.

## Other Considerations

* The `frameID` registered with Appcues for each frame should ideally be globally unique in the application, but at least must be unique on the screen where experience content may be targeted. 
* Some `AppcuesFrameView` instances may not be visible on the screen when it first loads, if they are lower down on a scrolling page, for instance. However, when they scroll into view, any qualified content on that screen will then render into that position.
* When configuring settings for triggering embedded experience content, make sure that the experience is triggered on the same screen where the target `frameID` exists.
* To preview embedded content from the mobile builder inside your application, you may need to initiate the preview and then navigate to the screen where the target `frameID` exists.
