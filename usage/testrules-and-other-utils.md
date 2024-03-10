# TestRules & other utils

Some utilities and TestRules to facilitate UI and Screenshot Testing&#x20;

## TestRules

```kotlin
@get:Rule
val disableAnimations = DisableAnimationsTestRule()

// Sets the Locale of the app under test only, i.e. the per-app language preference feature
@get:Rule
val inAppLocale = InAppLocaleTestRule("en")

// Sets the Locale of the Android system
@get:Rule
val systemLocale = SystemLocaleTestRule("en")

@get:Rule
val fontSize = FontSizeTestRule(FontSize.LARGEST)

@get:Rule
val displaySize = DisplaySizeTestRule(DisplaySize.LARGEST)

@get:Rule
val uiMode = UiModeTestRule(UiMode.NIGHT)
```

## **WaitFor...**

1. `waitForActivity`\
   Analogue to the one defined in [pedrovgs/Shot](https://github.com/pedrovgs/Shot). It's also available in this library for compatibility with other screenshot testing frameworks, like Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android).
2. `waitForFragment`\
   Analogue to `waitForActivity` but for Fragment.
3. `activity.waitForComposeView`\
   Returns the root Composable in the activity as a ComposeView. You can call later `drawToBitmap` or `drawToBitmapWithElevation` on it to screenshot test its corresponding bitmap.
4. `waitForMeasuredView/Dialog/ViewHolder(exactWidth, exactHeight)`\
   Inflates the layout in the main thread, sets its width and height to those given, and waits till the thread is idle, returning the inflated view. \
   Comes in handy with libraries that do not support to take a screenshot with a given width/height, like Dropshots.

<figure><img src="https://user-images.githubusercontent.com/6097181/211920753-35ee8f0b-d661-4623-8619-418c3972f1c2.png" alt="" width="563"><figcaption><p>Difference between waitForView() and waitForMeasuredView()</p></figcaption></figure>

{% hint style="warning" %}
Prefer `waitForMeasuredView` over `waitForView` (which is discouraged), specially if using Dropshots
{% endhint %}

## **Inflate or measure**

1. `activity.inflate(R.layout_of_your_view)`\
   Use it to inflate android Views with the activity's context configuration. In doing so, the configuration becomes effective in the view. It also adds the view to the Activity's root.\

2. `activity.inflateAndWaitForIdle(R.layout_of_your_view)`\
   Like `activity.inflate(...)`, but waits till the view is Idle to return it

{% hint style="warning" %}
Do not wrap it with `waitForMeasuredView{...}` or it will throw an exception.
{% endhint %}

3. `MeasureViewHelpers`\
   Analogue to the `ViewHelpers` defined in Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android). In most cases, you don't need to use it directly but via `waitForMeasuredView(exactWidth, exactHeight)`, which calls `MeasuredViewHelpers` under the hood.
