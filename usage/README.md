# Usage

The examples use [pedrovgs/Shot](https://github.com/pedrovgs/Shot). It also works with any other on-device screenshot testing library that supports _ActivityScenarios_, like&#x20;

* Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android)
* Dropbox [Dropshots](https://github.com/dropbox/dropshots)&#x20;
* Other custom screenshot testing solution based on _ActivityScenarios/FragmentScenarios_.&#x20;

{% hint style="info" %}
For Android-Testify, which does not support _ActivityScenario_ but the deprecated _ActivityTestRule_, have a look at the[ Activity-Testify usage](android-testify-usage.md) section.
{% endhint %}

You can find more complete examples with Shot, Dropshots, Roborazzi and Android-Testify in the [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground) repo.

## Activity

The simplest way is to use the **ActivityScenarioForActivityRule**, to avoid the need for closing the _ActivityScenario_.

```kotlin
@get:Rule
val screenshotRule =
    activityScenarioForActivityRule<MyActivity>(
        config = ActivityConfigItem(
            orientation = Orientation.LANDSCAPE,
            uiMode = UiMode.NIGHT,
            fontSize = FontSize.HUGE,
            systemLocale = "en",
            displaySize = DisplaySize.LARGEST,
        )
    )

@Test
fun snapActivityTest() {
    compareScreenshot(
        activity = screenshotRule.activity,
        name = "your_unique_screenshot_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use  **ActivityScenarioConfigurator.ForActivity()** directly in the test.&#x20;

Apart from that, this would be equivalent

```kotlin
// Sets the Locale of the Android system
@get:Rule(order = 0)
val systemLocale = SystemLocaleTestRule("en")

@get:Rule(order = 1)
val fontSize = FontSizeTestRule(FontSize.HUGE)

@get:Rule(order = 2)
val displaySize = DisplaySizeTestRule(DisplaySize.LARGEST)

@get:Rule(order = 3)
val uiMode = UiModeTestRule(UiMode.NIGHT)

@Test
fun snapActivityTest() {
    // AppLocale, SystemLocale, FontSize & DisplaySize are only supported via TestRules for Activities
    val activityScenario = ActivityScenarioConfigurator.ForActivity()
        .setOrientation(Orientation.LANDSCAPE)
        .launch(MyActivity::class.java)

    val activity = activityScenario.waitForActivity()

    compareScreenshot(
        activity = activity,
        name = "your_unique_screenshot_name"
    )

    activityScenario.close()
}
```

## Fragment

The simplest way is to use the **fragmentScenarioConfiguratorRule**

```kotlin
@get:Rule
val screenshotRule = fragmentScenarioConfiguratorRule<MyFragment>(
        fragmentArgs = bundleOf("arg_key" to "arg_value"),
        config = FragmentConfigItem(
            orientation = Orientation.LANDSCAPE,
            uiMode = UiMode.DAY,
            locale = "de",
            fontSize = FontSize.SMALL,
            displaySize = DisplaySize.SMALL,
            theme = R.style.Custom_Theme,
        ),
    )

@Test
fun snapFragment() {
    compareScreenshot(
        fragment = screenshotRule.fragment,
        name = "your_unique_screenshot_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use the plain **FragmentScenarioConfigurator**. This would be its equivalent:

```kotlin
@Test
fun snapFragment() {
    val screenshotRule =
        FragmentScenarioConfigurator
            .setInitialOrientation(Orientation.LANDSCAPE)
            .setUiMode(UiMode.DAY)
            .setLocale("de")
            .setFontSize(FontSize.SMALL)
            .setDisplaySize(DisplaySize.LARGE)
            .setTheme(R.style.Custom_Theme)
            .launchInContainer<MyFragment>(
                fragmentArgs = bundleOf("arg_key" to "arg_value"),
            )

    compareScreenshot(
        fragment = screeenshotRule.waitForFragment(),
        name = "your_unique_screenshot_name",
    )

    screenshotRule.close()
}
```

## Android View

The simplest way is to use the **ActivityScenarioForViewRule**, to avoid the need for closing the ActivityScenario.

```kotlin
@get:Rule
val rule =
    ActivityScenarioForViewRule(
        config = ViewConfigItem(
            fontSize = FontSize.NORMAL,
            locale = "en",
            orientation = Orientation.PORTRAIT,
            uiMode = UiMode.DAY,
            theme = R.style.Custom_Theme,
            displaySize = DisplaySize.SMALL,
        ),
        backgroundColor = TRANSPARENT,
    )

@Test
fun snapViewHolderTest() {
    // IMPORTANT: The rule inflates a layout inside the activity with its context to inherit the configuration 
    val layout = rule.inflateAndWaitForIdle(R.layout.your_view_holder_layout)

    // wait asynchronously for layout inflation 
    val viewHolder = waitForMeasuredViewHolder {
        YourViewHolder(layout).apply {
            // bind data to ViewHolder here
            ...
        }
    }

    compareScreenshot(
        holder = viewHolder,
        heightInPx = viewHolder.itemView.height,
        name = "your_unique_screenshot_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use **ActivityScenarioConfigurator.ForView()**. This would be its equivalent:

```kotlin
// example for ViewHolder
@Test
fun snapViewHolderTest() {
    val activityScenario =
        ActivityScenarioConfigurator.ForView()
            .setFontSize(FontSize.NORMAL)
            .setLocale("en")
            .setInitialOrientation(Orientation.PORTRAIT)
            .setUiMode(UiMode.DAY)
            .setTheme(R.style.Custom_Theme)
            .setDisplaySize(DisplaySize.SMALL)
            .launchConfiguredActivity(TRANSPARENT)

    val activity = activityScenario.waitForActivity()

    // IMPORTANT: To inherit the configuration, inflate layout inside the activity with its context 
    val layout = activity.inflateAndWaitForIdle(R.layout.your_view_holder_layout)

    // wait asynchronously for layout inflation 
    val viewHolder = waitForMeasuredViewHolder {
        YourViewHolder(layout).apply {
            // bind data to ViewHolder here
            ...
        }
    }

    compareScreenshot(
        holder = viewHolder,
        heightInPx = viewHolder.itemView.height,
        name = "your_unique_screenshot_name",
    )

    activityScenario.close()
}
```

## Jetpack Compose

The simplest way is to use the **ActivityScenarioForComposableRule**, to avoid the need for:

1. calling createEmptyComposeRule()
2. closing the ActivityScenario.

```kotlin
@get:Rule
val screenshotRule = ActivityScenarioForComposableRule(
        config = ComposableConfigItem(
            fontSize = FontSize.SMALL,
            locale = "de",
            uiMode = UiMode.DAY,
            displaySize = DisplaySize.LARGE,
            orientation = Orientation.PORTRAIT,
        ),
        backgroundColor = TRANSPARENT,
    )

@Test
fun snapComposableTest() {
    screenshotRule.activityScenario
        .onActivity {
            it.setContent {
                AppTheme { // this theme must use isSystemInDarkTheme() internally
                    yourComposable()
                }
            }
        }

    compareScreenshot(
        rule = rule.composeRule,
        name = "your_unique_screenshot_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use **ActivityScenarioConfigurator.ForComposable()** together with **createEmptyComposeRule()**. This would be its equivalent:

```kotlin
// needs an EmptyComposeRule to be compatible with ActivityScenario
@get:Rule
val composeTestRule = createEmptyComposeRule()

@Test
fun snapComposableTest() {
    val activityScenario = ActivityScenarioConfigurator.ForComposable()
        .setFontSize(FontSize.SMALL)
        .setLocale("de")
        .setInitialOrientation(Orientation.PORTRAIT)
        .setUiMode(UiMode.DAY)
        .setDisplaySize(DisplaySize.LARGE)
        .launchConfiguredActivity(TRANSPARENT)
        .onActivity {
            it.setContent {
                AppTheme { // this theme must use isSystemInDarkTheme() internally
                    yourComposable()
                }
            }
        }

    activityScenario.waitForActivity()

    compareScreenshot(rule = composeTestRule, name = "your_unique_screenshot_name")

    activityScenario.close()
}
```

### With Dropshots

If you are using a screenshot library that cannot take a _ComposeTestRule_ as argument (e.g. Dropshots), you can still screenshot the Composable as follows:

```kotlin
// with ActivityScenarioForComposableRule
dropshots.assertSnapshot(
    view = activityScenarioForComposableRule.activity.waitForComposeView(),
    name = "your_unique_screenshot_name",
)
```

or

```kotlin
// with ActivityScenarioConfigurator.ForComposable()
val activityScenario =
    ActivityScenarioConfigurator.ForComposable()
    ...
    .launchConfiguredActivity()
    .onActivity {
        ...
    }

dropshots.assertSnapshot(
    view = activityScenario.waitForActivity().waitForComposeView(),
    name = "your_unique_screenshot_name",
)
```

## Rendering elevation

Most screenshot testing libraries use `Canvas` under the hood with `Bitmap.Config.ARGB_8888` as default for generating bitmaps (i.e. the screenshots) from the Activities/Fragments/ViewHolders/Views/Dialogs/Composables...&#x20;

That's because Canvas is supported in all Android versions.\
Nevertheless, such bitmaps generated using `Canvas` have some limitations, e.g. UI elements are rendered without considering elevation (e.g. without shadows). So, how to render screenshots with elevation?

{% hint style="info" %}
Elevation can be manifested in many ways: e.g. a UI layer on top of another or a shadow in a CardView.
{% endhint %}

### Bitmap

Fortunately, most libraries let you pass a bitmap of the UI as an argument in their record/verify methods. In doing so, we can draw the views with elevation to a bitmap by using`PixelCopy`.

{% hint style="warning" %}
Robolectric 4.10+ cannot render shadows or elevation with RNG even with `PixelCopy`, as stated in [this issue](https://github.com/robolectric/robolectric/issues/8081)
{% endhint %}

_AndroidUiTestingUtils_ provides methods to easily generate bitmaps from the Activities/Fragments/ViewHolders/Views/Dialogs/Composables:

1. `drawToBitmap(config = Bitmap.Config.ARGB_8888)` -> uses `Canvas` under the hood
2. `drawToBitmapWithElevation(config = Bitmap.Config.ARGB_8888)` -> uses `PixelCopy` under the hood

and one extra to fully screenshot a scrollable Android View:&#x20;

3. `drawFullScrollableToBitmap(config = Bitmap.Config.ARGB_8888)` -> uses `Canvas` under the hood

Differences between Bitmaps generated via `Canvas` and `Pixel Copy` might be specially noticeable on API 31:

<div align="center">

<img src="https://user-images.githubusercontent.com/6097181/211920600-6cfcdde3-1fd6-4b23-84d1-3eae587c811d.png" alt="Differences between bitmaps generated with Canvas &#x26; Pixel Copy on API 31" width="350">

</div>

{% hint style="info" %}
If using `PixelCopy` with ViewHolders/Views/Dialogs/Composables, consider launching the container Activity with transparent background for a more realistic screenshot of the UI component.

```kotlin
ActivityScenarioConfigurator.ForView() // or .ForComposable()
      ...
      .launchConfiguredActivity(backgroundColor = Color.TRANSPARENT)
```

or

```kotlin
ActivityScenarioForViewRule( // or ActivityScenarioForComposableRule()
      viewConfig = ...,
      backgroundColor = Color.TRANSPARENT,
)
```

Otherwise it uses the default Dark/Light Theme background colors (e.g. white and dark grey).
{% endhint %}

Using `PixelCopy` instead of `Canvas` comes with its own drawbacks though. In general, don't use `PixelCopy` to draw views that don't fit on the screen.

| Canvas                                                                   |                                                    PixelCopy                                                    |
| ------------------------------------------------------------------------ | :-------------------------------------------------------------------------------------------------------------: |
| <p>✅ Can render elements beyond the screen,<br>e.g. long ScrollViews</p> | <p>❌ Cannot render elements beyond the screen, resizing them to fit in the window </p><p>if that's the case</p> |
| ❌ Ignores elevation of UI elements                                       |                                       ✅ Renders elevation of UI elements                                        |

And using `PixelCopy` in your screenshot tests is as simple as this:

```kotlin
// for UI Components like Activities/Fragments/ViewHolders/Views/Dialogs
compareScreenshot(
    bitmap = uiComponent.drawToBitmapWithElevation(),
    name = "your_unique_screenshot_name",
)
```

```kotlin
// for Composables
compareScreenshot(
    bitmap = activity.waitForComposableView().drawToBitmapWithElevation(),
    name = "your_unique_screenshot_name",
)
```
