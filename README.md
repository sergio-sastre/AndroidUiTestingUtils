[![](https://jitpack.io/v/sergio-sastre/AndroidUiTestingUtils.svg)](https://jitpack.io/#sergio-sastre/AndroidUiTestingUtils)</br>
<a href="https://androidweekly.net/issues/issue-508">
<img src="https://androidweekly.net/issues/issue-508/badge">
</a>

# <p align="center">Android UI testing utils</p>

<p align="center">
<img width="130" src="https://user-images.githubusercontent.com/6097181/172724660-778176b0-a6b0-4aad-b6b4-7115ad4fc7f3.png">
</p>

A set of *TestRules*, *ActivityScenarios* and utils to facilitate UI & screenshot testing under
certain configurations, independent of the UI testing libraries you are using.
<br clear="left"/>
</br></br>
For screenshot testing, it supports **Jetpack Compose**, **android Views** (e.g. custom Views,
ViewHolders, etc.), **Activities** and **Fragments**, as well as [**Robolectric**](#robolectric-screenshot-tests-beta)
</br></br>
Currently, with this library you can easily change the following configurations in your UI
tests:

1. Locale (also [Pseudolocales](https://developer.android.com/guide/topics/resources/pseudolocales#:~:text=4%20or%20earlier%3A-,On%20the%20device%2C%20open%20the%20Settings%20app%20and%20tap%20Languages,language%20(see%20figure%203)) **en_XA** & **ar_XB**)
    1. App Locale (i.e. per-app language preference)
    2. System Locale
2. Font size
3. Orientation
4. Custom themes
5. Dark mode /Day-Night mode
6. Display size

🗞️ Moreover, it provides experimental support for **cross-library** & **shared screenshot
testing** i.e. same test running either on device or on JVM.
Find out more under [cross-library screenshot tests](#cross-library-screenshot-tests-beta)

You can find out why verifying our design under such configurations is important in this blog post:

🎨 [Design a pixel perfect Android app](https://sergiosastre.hashnode.dev/design-a-pixel-perfect-android-app-with-screenshot-testing)
</br></br>
For examples of usage of this library in combination with Shot and Dropshots, check the following
repo:

📸 [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground)
</br></br>
In the near future, there are plans to also support, among others:

1. Dynamic colors (via TestRule)
2. Reduce snapshot testing flakiness
3. Folding features
4. Accessibility features

## Sponsors

Thanks to [Screenshotbot](https://screenshotbot.io) for their support!
[<img align="left" width="100" src="https://user-images.githubusercontent.com/6097181/192350235-b3b5dc63-e7e7-48da-bdb6-851a130aaf8d.png">](https://screenshotbot.io)

By using Screenshotbot instead of the in-build record/verify modes provided by most screenshot
libraries, you'll give your colleages a better developer experience, since they will not be required
to manually record screenshots after every run, instead getting notifications on their Pull
Requests.
<br clear="left"/>

## Table of Contents

- [Integration](#integration)
    - [In-App Locale](#in-app-locale)
    - [Robolectric screenshot tests (BETA)](#robolectric-screenshot-tests-beta)
    - [Cross-library screenshot tests (BETA)](#cross-library-screenshot-tests-beta)
- [Usage](#usage)
    - [Screnshot testing examples](#screenshot-testing-examples)
        - [Activity](#activity)
        - [Android View](#android-view)
        - [Jetpack Compose](#jetpack-compose)
        - [Fragment](#fragment)
        - [Bitmap](#bitmap)
        - [Robolectric (BETA)](#robolectric-beta)
          - [Activity](#activity-rng)
          - [Android View](#android-view-rng)
          - [Jetpack Compose](#jetpack-compose-rng)
          - [Fragment](#fragment-rng)
        - [Cross-library (BETA)](#cross-library-beta)
    - [Utils](#utils)
    - [Reading on screenshot testing](#reading-on-screenshot-testing)
    - [Standard UI testing](#standard-ui-testing)
- [Code attributions](#code-attributions)
- [Contributing](#contributing)

## Integration

Add jitpack to your root `build.gradle` file:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

then in your `build.gradle`

```kotlin
compileSdkVersion 33
```

```groovy
dependencies {
    androidTestImplementation('com.github.sergio-sastre:AndroidUiTestingUtils:1.2.4') {
        // if necessary, add this to avoid compose version clashes
        exclude group: 'androidx.compose.ui'
    }
    // add this if excluding 'androidx.compose.ui' due to compose version clashes 
    androidTestImplementation "androidx.compose.ui:ui-test-junit4:your_compose_version"
}
```

### In-App Locale

AndroidUiTestingUtils also
supports [per-app language preferences](https://developer.android.com/guide/topics/resources/app-languages)
. In order to change the In-App Locale, you need to use the `InAppLocaleTestRule`. For that it is
necessary to add the following dependency in your `build.gradle`

```kotlin
androidTestImplementation 'androidx.appcompat:appcompat:1.6.0-alpha04' // or higher version!
```

Use this rule to test Activities with in-app Locales that differ from the System Locale

### Robolectric screenshot tests (BETA)
Robolectric supports screenshot testing via [Robolectric Native graphics (RNG)](https://github.com/robolectric/robolectric/releases/tag/robolectric-4.10) since 4.10.
Since `AndroidUiTestingUtils:2.0.0-beta02`, you can configure your robolectric screenshot tests similar to how you'd do it with on-device tests.

For that, add the following dependencies in your `build.gradle`:
```kotlin
testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:utils:2.0.0-beta02'
testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:robolectric:2.0.0-beta02'
```

You can find some examples in [this section](#robolectric-beta)

### Cross-library screenshot tests (BETA)

Since `AndroidUiTestingUtils:2.0.0-beta01`, there is support for running the same screenshot test
for your **Composables** (support for Android Views coming soon) with different libraries, without 
rewriting.
Currently, that's only possible with the following screenshot testing libraries <sup>1</sup>:

- [Paparazzi](https://github.com/cashapp/paparazzi)
- [Shot](https://github.com/pedrovgs/Shot)
- [Dropshots](https://github.com/dropbox/dropshots)

1. First of all, configure all the screenshot testing libraries you want your tests to support, as
   if you'd write them with those specific libraries. e.g. Paparazzi supported only in library modules.</br>
   Visit their respective Github pages for more info.</br></br>

2. After that, include the following dependencies in the `build.gradle` of the module including the
   tests.

```groovy
dependencies {
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:utils:2.0.0-beta02'

    // Shot support
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:shot:2.0.0-beta02'

    // Dropshots support
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:dropshots:2.0.0-beta02'

    // Paparazzi support
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:sharedtest-paparazzi:2.0.0-beta02'
    testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:paparazzi:2.0.0-beta02'
}
```

3. To enable shared tests (i.e same test running either on the JVM or on a device/emulator), you 
    have 2 options:
   1. Create and write your tests in a [share test module as described here](https://blog.danlew.net/2022/08/16/sharing-code-between-test-modules/) or...
   2. Add this in the `build.gradle` of the module where you'll write shared tests. Then write your screenshot tests under `src/sharedTest`.

```groovy
android {
    ...
    sourceSets {
        test {
            java.srcDir 'src/sharedTest/java'
        }
        androidTest {
            java.srcDir 'src/sharedTest/java'
        }
    }
}
```

4. Create the corresponding `SharedScreenshotTestRule`, for instance:

```kotlin
class CrossLibraryScreenshotTestRule(
    override val config: ScreenshotConfig,
) : SharedScreenshotTestRule(config) {

    override fun getJvmScreenshotTestRule(config: ScreenshotConfig): ScreenshotTestRule {
        // as of beta02 only paparazziScreenshotTestRule, Roborazzi from beta03
        return paparazziScreenshotTestRule
    }

    override fun getInstrumentedScreenshotTestRule(config: ScreenshotConfig): ScreenshotTestRule {
        // either dropshotsScreenshotTestRule or shotScreenshotTestRule
        return dropshotsScreenshotTestRule
    }
}
```

5. Finally, write your tests with the CrossLibraryScreenshotTestRule. For an example, see [this section](#cross-library-beta)

<sup>1</sup> Support for [Roborazzi](https://github.com/takahirom/roborazzi), Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android),
and ndpt [android-testify](https://github.com/ndtp/android-testify) is coming soon.

# Usage

## Screenshot testing examples

The examples use [pedrovgs/Shot](https://github.com/pedrovgs/Shot). It also works with any other
on-device screenshot testing library, like
Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android),
Dropbox [Dropshots](https://github.com/dropbox/dropshots) or with a custom screenshot testing
solution.

You can find more complete examples with Shot and Dropshots in
the [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground)
repo.

### Activity

The simplest way is to use the **ActivityScenarioForActivityRule**, to avoid the need for closing
the ActivityScenario.

```kotlin
@get:Rule
val rule =
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
        activity = rule.activity,
        name = "your_unique_screenshot_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use **
ActivityScenarioConfigurator.ForActivity()** directly in the test. Currently, this is the only means
to set

1. A TimeOut for the FontSize and DisplaySize TestRules
2. A InAppLocaleTestRule for per-app language preferences

Apart from that, this would be equivalent:

```kotlin
// Sets the Locale of the app under test only, i.e. the per-app language preference feature
@get:Rule
val inAppLocale = InAppLocaleTestRule("ar")

// Sets the Locale of the Android system
@get:Rule
val systemLocale = SystemLocaleTestRule("en")

@get:Rule
val fontSize = FontSizeTestRule(FontSize.HUGE).withTimeOut(inMillis = 15_000) // default is 10_000

@get:Rule
val displaySize = DisplaySizeTestRule(DisplaySize.LARGEST).withTimeOut(inMillis = 15_000)

@get:Rule
val uiMode = UiModeTestRule(UiMode.NIGHT)

@Test
fun snapActivityTest() {
    // Custom themes are not supported
    // AppLocale, SystemLocale, FontSize & DisplaySize are only supported via TestRules for Activities
    val activityScenario = ActivityScenarioConfigurator.ForActivity()
        .setOrientation(Orientation.LANDSCAPE)
        .launch(MyActivity::class.java)

    val activity = activityScenario.waitForActivity()

    compareScreenshot(activity = activity, name = "your_unique_screenshot_name")

    activityScenario.close()
}
```

> **Warning**</br>
> If using any TestRule with Ndtp [android-testify](https://github.com/ndtp/android-testify),
> use `launchActivity = false` for them to take effect:
> ```kotlin
> @get:Rule
> val activityTestRule =
>     ScreenshotRule(CoffeeDrinksComposeActivity::class.java, launchActivity = false)
>```
>

### Android View

The simplest way is to use the **ActivityScenarioForViewRule**, to avoid the need for closing the
ActivityScenario.

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

In case you don't want to/cannot use the rule, you can use **
ActivityScenarioConfigurator.ForView()**. This would be its equivalent:

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

If the View under test contains system Locale dependent code,
like `NumberFormat.getInstance(Locale.getDefault())`, the Locale formatting you've set
via `ActivityScenarioConfigurator.ForView().setLocale("my_locale")` will not work. That's because
NumberFormat is using the Locale of the Android system, and not that of the Activity we've
configured. Beware of using `instrumenation.targetContext` in your tests when using getString() for
the very same reason: use Activity's context instead. </br> To solve that issue, you can do one of
the following:

1. Use `NumberFormat.getInstance(anyViewInsideActivity.context.locales[0])` in your production code.
2. Use `SystemLocaleTestRule("my_locale")` in your tests instead
   of `ActivityScenarioConfigurator.ForView().setLocale("my_locale")`.

### Jetpack Compose

The simplest way is to use the **ActivityScenarioForComposableRule**, to avoid the need for:

1) calling createEmptyComposeRule()
2) closing the ActivityScenario.

```kotlin
@get:Rule
val rule = ActivityScenarioForComposableRule(
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
    rule.activityScenario
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

In case you don't want to/cannot use the rule, you can use **
ActivityScenarioConfigurator.ForComposable()** together with **createEmptyComposeRule()**. This
would be its equivalent:

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

If you are using a screenshot library that cannot take a composeTestRule as argument (e.g.
Dropshots),
you can still screenshot the Composable as follows:

```kotlin
// with ActivityScenarioForComposableRule
dropshots.assertSnapshot(
    view = activityScenarioForComposableRule.activity.waitForComposeView(),
    name = "your_unique_screenshot_name",
)
```

or

```kotlin
// withm ActivityScenarioConfigurator.ForComposable()
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

If the Composable under test contains system Locale dependent code,
like `NumberFormat.getInstance(Locale.getDefault())`, the Locale formatting you've set
via `ActivityScenarioConfigurator.ForComposable().setLocale("my_locale")` will not work. That's
because NumberFormat is using the Locale of the Android system, and not that of the Activity we've
configured, which is applied to the LocaleContext of our Composables. </br> To solve that issue, you
can do one of the following:

1. Use `NumberFormat.getInstance(LocaleContext.current.locales[0])` in your production code.
2. Use `SystemLocaleTestRule("my_locale")` in your tests instead
   of `ActivityScenarioConfigurator.ForComposable().setLocale("my_locale")`.

### Fragment

The simplest way is to use the **FragmentScenarioConfiguratorRule**

```kotlin
@get:Rule
val rule = fragmentScenarioConfiguratorRule<MyFragment>(
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
        fragment = rule.fragment,
        name = "your_unique_screenshot_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use the plain **
FragmentScenarioConfigurator**. This would be its equivalent:

```kotlin
@Test
fun snapFragment() {
    val fragmentScenario =
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
        fragment = fragmentScenario.waitForFragment(),
        name = "your_unique_screenshot_name",
    )

    fragmentScenario.close()
}
```

### Bitmap

Most screenshot testing libraries use `Canvas` with `Bitmap.Config.ARGB_8888` as default for
generating bitmaps (i.e. the screenshots) from the
Activities/Fragments/ViewHolders/Views/Dialogs/Composables...
That's because Canvas is supported in all Android versions.</br>
Nevertheless, such bitmaps generated using `Canvas` have some limitations, e.g. UI elements are
rendered without considering elevation.

Fortunately, such libraries let you pass the bitmap (i.e.the screenshot) as argument in their
record/verify methods.
In doing so, we can draw the views with elevation<sup>1</sup> to a bitmap with `PixelCopy`.

AndroidUiTestingUtils provides methods to easily generate bitmaps from the
Activities/Fragments/ViewHolders/Views/Dialogs/Composables:

1. `drawToBitmap(config = Bitmap.Config.ARGB_8888)` -> uses `Canvas` under the hood
2. `drawToBitmapWithElevation(config = Bitmap.Config.ARGB_8888)` -> uses `PixelCopy` under the hood

Differences between both might be specially noticeable in API 31:
<p align="center">
<img width="350" src="https://user-images.githubusercontent.com/6097181/211920600-6cfcdde3-1fd6-4b23-84d1-3eae587c811d.png">
</p>

> **Note**</br>
> If using `PixelCopy` with ViewHolders/Views/Dialogs/Composables, consider launching the container
> Activity with transparent background for a more realistic screenshot of the UI component.
> ```kotlin
> ActivityScenarioConfigurator.ForView() // or .ForComposable()
>       ...
>       .launchConfiguredActivity(backgroundColor = Color.TRANSPARENT)
> ```
> or
> ```kotlin
> ActivityScenarioForViewRule( // or ActivityScenarioForComposableRule()
>       viewConfig = ...,
>       backgroundColor = Color.TRANSPARENT,
> )
> ```
> Otherwise it uses the default Dark/Light Theme background colors (e.g. white and dark grey).

Using `PixelCopy` instead of `Canvas` comes with its own drawbacks though. In general, don't use
PixelCopy to draw views that don't fit on the screen. </br>

| Canvas                                                              |                                  PixelCopy                                   |
|---------------------------------------------------------------------|:----------------------------------------------------------------------------:|
| ✅ Can render elements beyond the screen,<br/> e.g. long ScrollViews | ❌ Cannot render elements beyond the screen,<br/> resizing if that's the case | 
| ❌ Ignores elevation<sup>2</sup> of UI elements while drawing        |        ✅ Considers elevation<sup>2</sup> of UI elements while drawing        |

<sup>1</sup> Robolectric 4.10 or lower cannot render shadows or elevation with RNG, as stated in [this issue](https://github.com/robolectric/robolectric/issues/8081)
<sup>2</sup> Elevation can be manifested in many ways: a UI layer on top of another or a shadow in a
CardView.

And using `PixelCopy` in your screenshot tests is as simple as this (example with Shot):

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

### Robolectric (BETA)
AndroidUiTesting includes some special `ActivityScenarioConfigurators` and `FragmentScenarioConfigurators` that are additionally safe-thread, 
what allows unit tests in parallel without unexpected behaviours.

It supports *Jetpack Compose**, **android Views** (e.g. custom Views,
ViewHolders, etc.), **Activities** and **Fragments**.

Check out some examples below.
It uses [Roborazzi](https://github.com/takahirom/roborazzi) as screenshot testing library.

### Activity (RNG)
Here with Junit4 test rule
```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapActivityTest {

    @get:Rule
    val robolectricActivityScenarioForActivityRule =
        robolectricActivityScenarioForActivityRule(
            config = ActivityConfigItem(
                systemLocale = "en",
                uiMode = UiMode.NIGHT,
                theme = R.style.Custom_Theme,
                orientation = Orientation.PORTRAIT,
                fontSize = FontSize.NORMAL,
                displaySize = DisplaySize.NORMAL,
            ),
            deviceScreen = DeviceScreen.Phone.PIXEL_4A,
        )

    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen` in the Rule
    @Test
    fun snapActivity() {
        robolectricActivityScenarioForActivityRule
            .rootView
            .captureRoboImage("path/MyActivity.png")
    }
}
```
or without Junit4 test rules
```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapActivityTest {

    @Config(sdk = [30]) // Do not use qualifiers if using `setDeviceScreen()
    @Test
    fun snapActivity() {
        val activityScenario =
            RobolectricActivityScenarioConfigurator.ForActivity()
                .setDeviceScreen(DeviceScreen.Phone.PIXEL_4A)
                .setSystemLocale("en")
                .setUiMode(UiMode.NIGHT)
                .setOrientation(Orientation.PORTRAIT)
                .setFontSize(FontSize.NORMAL)
                .setDisplaySize(DisplaySize.NORMAL)
                .launch(MyActivity::class.java)

        activityScenario
            .rootView
            .captureRoboImage("path/MyActivity.png")

        activityScenario.close()
    }
}
```

### Android View (RNG)
Here with Junit4 test rule
```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapViewHolderTest {

    @get:Rule
    val robolectricActivityScenarioForViewRule =
        RobolectricActivityScenarioForViewRule(
            config = ViewConfigItem(
                locale = "en_XA",
                uiMode = UiMode.NIGHT,
                theme = R.style.Custom_Theme,
                orientation = Orientation.PORTRAIT,
                fontSize = FontSize.NORMAL,
                displaySize = DisplaySize.NORMAL,
            ),
            deviceScreen = DeviceScreen.Phone.PIXEL_4A,
            backgroundColor = TRANSPARENT,
        )

    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen` in the Rule
    @Test
    fun snapViewHolder() {
        val activity = robolectricActivityScenarioForViewRule.activity
        val layout = robolectricActivityScenarioForViewRule.inflateAndWaitForIdle(R.layout.memorise_row)

        val viewHolder = waitForMeasuredViewHolder {
            MemoriseViewHolder(
                container = layout,
                itemEventListener = null,
                animationDelay = 0L,
            ).apply {
                bind(generateMemoriseItem(rightAligned = false, activity = activity))
            }
        }

        viewHolder
            .itemView
            .captureRoboImage("path/MemoriseViewHolder.png")
        }
    }
}
```
or without Junit4 test rules
```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapViewHolderTest {
    
    @Config(sdk = [30]) // Do not use qualifiers if using `setDeviceScreen()
    @Test
    fun snapViewHolder() {
        val activityScenario = 
            RobolectricActivityScenarioConfigurator.ForView()
                .setDeviceScreen(DeviceScreen.Phone.PIXEL_4A)
                .setLocale("en_XA")
                .setUiMode(UiMode.NIGHT)
                .setTheme(R.style.Custom_Theme)
                .setOrientation(Orientation.PORTRAIT)
                .setFontSize(FontSize.NORMAL)
                .setDisplaySize(DisplaySize.NORMAL)
                .launchConfiguredActivity(TRANSPARENT)

        val activity = activityScenario.waitForActivity()
        val layout = activity.inflateAndWaitForIdle(R.layout.memorise_row)

        val viewHolder = waitForMeasuredViewHolder {
            MemoriseViewHolder(
                container = layout,
                itemEventListener = null,
                animationDelay = 0L,
            ).apply {
                bind(generateMemoriseItem(rightAligned = false, activity = activity))
            }
        }

        viewHolder
            .itemView
            .captureRoboImage("path/MemoriseViewHolder.png")

        activityScenario.close()
    }
}
```

### Jetpack Compose (RNG)
Here with `RobolectricActivityScenarioForComposableRule` test rule
```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapComposableTest {

    @get:Rule
    val activityScenarioForComposableRule = 
        RobolectricActivityScenarioForComposableRule(
            config = ComposableConfigItem(
                fontSize = FontSize.SMALL,
                locale = "ar_XB",
                uiMode = UiMode.DAY,
                displaySize = DisplaySize.LARGE,
                orientation = Orientation.PORTRAIT,
            ),
            deviceScreen = DeviceScreen.Phone.PIXEL_4A,
            backgroundColor = TRANSPARENT,
        )

    @Config(sdk = [30]) // Do not use qualifiers if using `setDeviceScreen()
    @Test
    fun snapComposable() {
        activityScenarioForComposableRule
            .activityScenario
            .onActivity {
                it.setContent {
                    AppTheme { // this theme must use isSystemInDarkTheme() internally
                        yourComposable()
                    }
                }
            }

        activityScenarioForComposableRule
            .composeRule
            .onRoot()
            .captureRoboImage("path/MyComposable.png")
    }
}
```
or without `RobolectricActivityScenarioForComposableRule` test rule

```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapComposableTest {
    
    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Config(sdk = [30]) // Do not use qualifiers if using setDeviceScreen()
    @Test
    fun snapComposable() {
        val activityScenario =
            RobolectricActivityScenarioConfigurator.ForComposable()
                .setDeviceScreen(DeviceScreen.Phone.PIXEL_4A)
                .setFontSize(FontSize.SMALL)
                .setLocale("ar_XB")
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
                
        composeTestRule
            .onRoot()
            .captureRoboImage("path/MyComposable.png")

        activityScenario.close()
    }
}
```
### Fragment (RNG)
Here with Junit4 test rule
```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapFragmentTest {

    @get:Rule
    val robolectricFragmentScenarioConfiguratorRule =
        robolectricFragmentScenarioConfiguratorRule<MyFragment>(
            fragmentArgs = bundleOf("arg_key" to "arg_value"),
            config = FragmentConfigItem(
                locale = "en_XA",
                uiMode = UiMode.NIGHT,
                theme = R.style.Custom_Theme,
                orientation = Orientation.PORTRAIT,
                fontSize = FontSize.NORMAL,
                displaySize = DisplaySize.NORMAL,
            ),
            deviceScreen = DeviceScreen.Phone.PIXEL_4A,
        )

    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen` in the Rule
    @Test
    fun snapFragment() {
        robolectricFragmentScenarioConfiguratorRule
            .fragment
            .view!!
            .captureRoboImage("path/MyFragment.png")
    }
}
```
or without Junit4 test rules
```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapFragmentTest {

    @Config(sdk = [30]) // Do not use qualifiers if using `setDeviceScreen()
    @Test
    fun snapFragment() {
        val fragmentScenario =
            RobolectricFragmentScenarioConfigurator.ForFragment()
                .setDeviceScreen(DeviceScreen.Phone.PIXEL_4A)
                .setLocale("en_XA")
                .setUiMode(UiMode.NIGHT)
                .setTheme(R.style.Custom_Theme)
                .setOrientation(Orientation.PORTRAIT)
                .setFontSize(FontSize.NORMAL)
                .setDisplaySize(DisplaySize.NORMAL)
                .launchInContainer(
                    fragmentClass = MyFragment::class.java,
                    fragmentArgs = bundleOf("arg_key" to "arg_value"),
                )

        fragmentScenario
            .waitForFragment()
            .view!!
            .captureRoboImage("path/MyActivity.png")

        fragmentScenario.close()
    }
}
```

### Cross-library (BETA)

For cross-library screenshot tests, you need to follow the steps
in [this section](#cross-library-screenshot-tests-beta).
Once done, writing such tests is as easy as this:

```kotlin
// Only support for composables for now
@get:Rule
val screenshotRule =
    CrossLibraryTestRule(
        config = ScreenshotConfig(
            uiMode = UiMode.DAY,
            orientation = Orientation.LANDSCAPE,
            locale = "en", // only simple locales supported for now. e.g. en-US not supported yet
            fontScale = FontSize.NORMAL,
        ),
    )
        // Optional: Define special configurations for each library    
        .configure(
            ShotConfig(bitmapCaptureMethod = PixelCopy())
        ).configure(
            DropshotsConfig(resultValidator = ThresholdValidator(0.15f))
        ).configure(
            PaparazziConfig(deviceConfig = DeviceConfig.NEXUS_4)
        )


@Test
fun snapComposable() {
    screenshotRule.snapshot(name = "your_unique_screenshot_name") {
        MyComposable()
    }
}
```

## Utils

**Wait**

1. `waitForActivity`:
   Analog to the one defined in [pedrovgs/Shot](https://github.com/pedrovgs/Shot).
   It's also available in this library for compatibility with other screenshot testing frameworks
   like
   Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android)
   .</br></br>
2. `waitForFragment`: Analog to waitForActivity but for Fragment.</br></br>

3. `activity.waitForComposeView`: Returns the root Composable in the activity as a ComposeView. You
   can call later `drawToBitmap` or `drawToBitmapWithElevation` on it to screenshot test its
   corresponding bitmap.</br></br>

4. `waitForMeasuredView/Dialog/ViewHolder(exactWidth, exactHeight)`: Inflates the layout in the main
   thread, sets its width and height to those given, and waits till the thread is idle,
   returning the inflated view. Comes in handy with libraries that do not support, to take a
   screenshot with a given width/height, like Dropshots.</br></br>

> **Warning**</br>
> Prefer `waitForMeasuredView` over `waitForView` (which is discouraged), specially if using
> Dropshots:
> <p align="center">
> <img width="750" src="https://user-images.githubusercontent.com/6097181/211920753-35ee8f0b-d661-4623-8619-418c3972f1c2.png">
> </p>

**Inflate or measure**</br>

5. `activity.inflate(R.layout_of_your_view)`: Use it to inflate android Views with the activity's
   context configuration. In doing so, the configuration becomes effective in the view. It also adds
   the view to the Activity's root.</br></br>
6. `activity.inflateAndWaitForIdle(R.layout_of_your_view)`: Like activity.inflate, but waits till
   the view is Idle to return it.
   Do not wrap it with waitForMeasuredView{} or it will throw an exception.</br></br>
7. `MeasureViewHelpers`: Analog to the `ViewHelpers` defined in
   Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android)
   . In most cases, you don't need to use it directly but
   via `waitForMeasuredView(exactWidth, exactHeight)`, which calls `MeasuredViewHelpers` under the
   hood.

## Reading on screenshot testing

- [An introduction to snapshot testing on Android in 2021 📸](https://sergiosastre.hashnode.dev/an-introduction-to-snapshot-testing-on-android-in-2021)
- [The secrets of effectively snapshot testing on Android 🔓](https://sergiosastre.hashnode.dev/the-secrets-of-effectively-snapshot-testing-on-android)
- [UI tests vs. snapshot tests on Android: which one should I write? 🤔](https://sergiosastre.hashnode.dev/ui-tests-vs-snapshot-tests-on-android-which-one-should-i-write)
- [Design a pixel perfect Android app 🎨](https://sergiosastre.hashnode.dev/design-a-pixel-perfect-android-app-with-screenshot-testing)

## Standard UI testing

For standard UI testing, you can use the same approach as for snapshot testing Activities. The
following TestRules and methods
are provided:

```kotlin
// Sets the Locale of the app under test only, i.e. the per-app language preference feature
@get:Rule
val inAppLocale = InAppLocaleTestRule("en")

// Sets the Locale of the Android system
@get:Rule
val systemLocale = SystemLocaleTestRule("en")

@get:Rule
val fontSize = FontSizeTestRule(FontSize.HUGE).withTimeOut(inMillis = 15_000) // default is 10_000

@get:Rule
val displaySize = DisplaySizeTestRule(DisplaySize.LARGEST).withTimeOut(inMillis = 15_000)

@get:Rule
val uiMode = UiModeTestRule(UiMode.NIGHT)

activity.rotateTo(Orientation.LANDSCAPE)
```

> **Warning**</br>
> When using *DisplaySizeTestRule* and *FontSizeTesRule* together in the same test, make
> sure your emulator has enough RAM and VM heap to avoid Exceptions when running the tests. The
> recommended configuration is the following:
> - RAM: 4GB
> - VM heap: 1GB

# Code attributions

This library has been possible due to the work others have done previously. Most TestRules are based
on code written by others:

- SystemLocaleTestRule
  -> [Screengrab](https://github.com/fastlane/fastlane/tree/master/screengrab/screengrab-lib/src/main/java/tools.fastlane.screengrab/locale)
- FontSizeTestRule
  -> [Novoda/espresso-support](https://github.com/novoda/espresso-support/tree/master/core/src/main/java/com/novoda/espresso)
- UiModeTestRule -> [AdevintaSpain/Barista](https://github.com/AdevintaSpain/Barista)
- Orientation change for activities
  -> [Shopify/android-testify](https://github.com/Shopify/android-testify/)
- MeasureViewHelpers -> a copy of ViewHelpers from
  Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android)

# Contributing

1. Create an issue in this repo
2. Fork the
   repo [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground)
3. In that repo, add an example and test where the bug is reproducible/ and showcasing the new
   feature.
4. Once pushed, add a link to the PR in the issue created in this repo and add @sergio-sastre as a
   reviewer.
5. Once reviewed and approved, create an issue in this repo.
6. Fork this repo and add the approved code from the other repo to this one (no example or test
   needed). Add @sergio-sastre as a reviewer.
7. Once approved, I will merge the code in both repos, and you will be added as a contributor
   to [Android UI testing utils](https://github.com/sergio-sastre/AndroidUiTestingUtils) as well
   as [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground)
   .

I'll try to make the process easier in the future if I see many issues/feature requests incoming :)

</br></br>
<a href="https://www.flaticon.com/free-icons/ninja" title="ninja icons">Android UI testing utils
logo modified from one by Freepik - Flaticon</a>

