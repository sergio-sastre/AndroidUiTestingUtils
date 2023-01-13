[![](https://jitpack.io/v/sergio-sastre/AndroidUiTestingUtils.svg)](https://jitpack.io/#sergio-sastre/AndroidUiTestingUtils)</br>
<a href="https://androidweekly.net/issues/issue-508">
<img src="https://androidweekly.net/issues/issue-508/badge">
</a>

# Android UI testing utils

<p align="left">
<img width="130" src="https://user-images.githubusercontent.com/6097181/172724660-778176b0-a6b0-4aad-b6b4-7115ad4fc7f3.png">
</p>

A set of *TestRules*, *ActivityScenarios* and utils to facilitate UI & screenshot testing under
certain configurations, independent of the UI testing framework you are using.
<br clear="left"/>
</br></br>
For screenshot testing, it supports **Jetpack Compose**, **android Views** (e.g. custom Views,
ViewHolders, etc.), **Activities** and **Fragments**.
</br></br>
Currently, with this library you can easily change the following configurations in your instrumented
tests:

1. Locale (also Pseudolocales **en_XA** & **ar_XB**)
    1. App Locale (i.e. per-app language preference)
    2. System Locale
2. Font size
3. Orientation
4. Custom themes
5. Dark mode /Day-Night mode
6. Display size

You can find out why verifying our design under such configurations is important in this blog post:

- [Design a pixel perfect Android app 🎨](https://sergiosastre.hashnode.dev/design-a-pixel-perfect-android-app-with-screenshot-testing)

For examples of usage of this library in combination with Shot and Dropshots, check the following repo:
- [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground)

In the near future, there are plans to also support, among others:

1. **framework-agnostic** & **shared screenshot testing** i.e. same test running either on device or on JVM
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
- [Usage](#usage)
  - [Configuration](#configuration)
    - [ActivityScenarios](#activityscenarios)
    - [Pseudolocales](#pseudolocales)
    - [System Locale](#system-locale)
    - [App Locale](#app-locale)
  - [Screnshot testing examples](#screenshot-testing-examples)
    - [Activity](#activity)
    - [Android View](#android-view)
    - [Jetpack Compose](#jetpack-compose)
    - [Fragment](#fragment)
    - [Bitmap](#bitmap)
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
Set the comple SdkVersion and add a dependency to `build.gradle`

```kotlin
compileSdkVersion 33
```

```groovy
dependencies {
    androidTestImplementation('com.github.sergio-sastre:AndroidUiTestingUtils:1.2.3') {
        // if necessary, add this to avoid compose version clashes
        exclude group: 'androidx.compose.ui'
    }
    // add this if excluding 'androidx.compose.ui' due to compose version clashes 
    androidTestImplementation "androidx.compose.ui:ui-test-junit4:your_compose_version"
}
```

# Usage

## Configuration

### ActivityScenarios

Add the following permission and activities to your `debug/manifest`

```xml
<!-- Required for ActivityScenarios only -->
<application...
    <activity
        android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$PortraitSnapshotConfiguredActivity" 
    />
    <activity
        android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$LandscapeSnapshotConfiguredActivity"
        android:screenOrientation="landscape" 
    />
...
</application>
```

### Pseudolocales
To enable pseudolocales **en_XA** & **ar_XB** for your screenshot tests, add this to your
build.gradle.

```groovy
android {
    ...
    buildTypes {
        ...
        debug {
            pseudoLocalesEnabled true
        }
    }
}
```
### System Locale
To change the System Locale , you also need to add the following permission to your `debug/manifest`.
For multi-module apps, do this in the app module.

```xml
<!-- Required to change the Locale via SystemLocaleTestRule (e.g. for snapshot testing Activities) -->
<uses-permission android:name="android.permission.CHANGE_CONFIGURATION"
    tools:ignore="ProtectedPermissions" />
```
### App Locale
To change the App Locale, which is possible via `LocaleTestRule`, you need to add the following dependency in your `app/build.gradle`
```kotlin
androidTestImplementation 'androidx.appcompat:appcompat:1.6.0-alpha04' //or higher version!
```
**Warning**: `LocaleTestRule` does ONLY work with **ActivityScenarioConfigurator.ForActivity()**, i.e. it
does not work with **ActivityScenarioForActivityRule**. However, for **Fragments**, **Views** and **Composables** call the
`setLocale("my_locale")` of their corresponding Fragment/ActivityScenarioConfigurator or the `ConfigItem(locale = "myLocale")` of their corresponding TestRule e.g. to achieve it:
```kotlin
ActivityScenarioConfigurator.ForView().setLocale("my_locale")
```
or

```kotlin
@get:Rule
val rule =
    ActivityScenarioForViewRule(
        config = ViewConfigItem(locale = "my_locale")
    )
```

## Screenshot testing examples

The examples use [pedrovgs/Shot](https://github.com/pedrovgs/Shot). It also works with any other
on-device screenshot testing library, like
Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android),
Dropbox [Dropshots](https://github.com/dropbox/dropshots) or with a custom screenshot testing
solution.

You can find more complete examples with Shot and Dropshots in the [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground) repo.

### Activity

The simplest way is to use the **ActivityScenarioForActivityRule**, to avoid the need for closing the ActivityScenario.

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
        name = "your_unique_test_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use **ActivityScenarioConfigurator.ForActivity()** directly in the test. Currently, this is the only means to set
1. A TimeOut for the FontSize and DisplaySize TestRules
2. A LocaleTestRule for per-app language preferences

Apart from that, this would be equivalent:

```kotlin
// Sets the Locale of the app under test only, i.e. the per-app language preference feature
@get:Rule
val locale = LocaleTestRule("ar")

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

    compareScreenshot(activity = activity, name = "your_unique_test_name")

    activityScenario.close()
}
```

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
        )
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
        name = "your_unique_test_name",
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
            .launchConfiguredActivity()

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
        name = "your_unique_test_name",
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
        )
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
        name = "your_unique_test_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use **ActivityScenarioConfigurator.ForComposable()** together with **createEmptyComposeRule()**. This
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
        .launchConfiguredActivity()
        .onActivity {
            it.setContent {
                AppTheme { // this theme must use isSystemInDarkTheme() internally
                    yourComposable()
                }
            }
        }

    activityScenario.waitForActivity()

    compareScreenshot(rule = composeTestRule, name = "your_unique_test_name")

    activityScenario.close()
}
```

If you are using a screenshot library that cannot take a composeTestRule as argument (e.g. Dropshots),
you can still screenshot the Composable as follows:
```kotlin
// with ActivityScenarioForComposableRule
dropshots.assertSnapshot(
   view = activityScenarioForComposableRule.activity.waitForComposeView(),
   name = "your_unique_test_name",
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
   name = "your_unique_test_name",
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
        name = "your_unique_test_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use the plain **FragmentScenarioConfigurator**. This would be its equivalent:

```kotlin
@Test
fun snapFragment() {
    val fragmentScenarioConfigurator =
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
        fragment = fragmentScenarioConfigurator.waitForFragment(),
        name = "your_unique_test_name",
    )

    fragmentScenarioConfigurator.close()
}
```

### Bitmap

Most screenshot testing libraries use `Canvas` with `Bitmap.Config.ARGB_8888` as default for generating bitmaps (i.e. the screenshots) from the Activities/Fragments/ViewHolders/Views/Dialogs/Composables...
That's because Canvas is supported in all Android versions.</br>
Nevertheless, such bitmaps generated using `Canvas` have some limitations, e.g. UI elements are rendered without considering elevation.

Fortunately, such libraries let you pass the bitmap (i.e.the screenshot) as argument in their record/verify methods.
In doing so, we can draw the views with elevation to a bitmap with `PixelCopy`.

AndroidUiTestingUtils provides methods to easily generate bitmaps from the Activities/Fragments/ViewHolders/Views/Dialogs/Composables:
1. `drawToBitmap(config = Bitmap.Config.ARGB_8888)` -> uses `Canvas` under the hood
2. `drawToBitmapWithElevation(config = Bitmap.Config.ARGB_8888)` -> uses `PixelCopy` under the hood

Differences between both might be specially noticeable in API 31:
<p align="center">
<img width="350" src="https://user-images.githubusercontent.com/6097181/211920600-6cfcdde3-1fd6-4b23-84d1-3eae587c811d.png">
</p>

> **Note**</br>
> If using `PixelCopy` with ViewHolders/Views/Dialogs/Composables, consider launching the container Activity with transparent background for a more realistic screenshot of the UI component.
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
| ❌ Ignores elevation<sup>1</sup> of UI elements while drawing        |        ✅ Considers elevation<sup>1</sup> of UI elements while drawing        |

<sup>1</sup> Elevation can be manifested in many ways: a UI layer on top of another or a shadow in a CardView.

And using `PixelCopy` in your screenshot tests is as simple as this (example with Shot):
```kotlin
// for UI Components like Activities/Fragments/ViewHolders/Views/Dialogs
compareScreenshot(
    bitmap = uiComponent.drawToBitmapWithElevation(),
    name = "your_unique_test_name",
)
```
```kotlin
// for Composables
compareScreenshot(
    bitmap = activity.waitForComposableView().drawToBitmapWithElevation(),
    name = "your_unique_test_name",
)
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

3. `activity.waitForComposeView`: Returns the root Composable in the activity as a ComposeView. You can call later `drawToBitmap` or `drawToBitmapWithElevation` on it to screenshot test its corresponding bitmap.</br></br>

4. `waitForMeasuredView/Dialog/ViewHolder(exactWidth, exactHeight)`: Inflates the layout in the main thread, sets its width and height to those given, and waits till the thread is idle,
      returning the inflated view. Comes in handy with libraries that do not support, to take a screenshot with a given width/height, like Dropshots.</br></br>

> **Warning**</br>
> Prefer `waitForMeasuredView` over `waitForView` (which is discouraged), specially if using Dropshots:
> <p align="center">
> <img width="750" src="https://user-images.githubusercontent.com/6097181/211920753-35ee8f0b-d661-4623-8619-418c3972f1c2.png">
> </p>

**Inflate or measure**</br>
5. `activity.inflate(R.layout_of_your_view)`: Use it to inflate android Views with the activity's
   context configuration. In doing so, the configuration becomes effective in the view. It also adds
   the view to the Activity's root.</br></br>
6. `activity.inflateAndWaitForIdle(R.layout_of_your_view)`: Like activity.inflate, but waits till the view is Idle to return it.
   Do not wrap it with waitForMeasuredView{} or it will throw an exception.</br></br>
7. `MeasureViewHelpers`: Analog to the `ViewHelpers` defined in Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android). In most cases, you don't need to use it directly but via `waitForMeasuredView(exactWidth, exactHeight)`, which calls `MeasuredViewHelpers` under the hood.

## Reading on screenshot testing

- [An introduction to snapshot testing on Android in 2021 📸](https://sergiosastre.hashnode.dev/an-introduction-to-snapshot-testing-on-android-in-2021)
- [The secrets of effectively snapshot testing on Android 🔓](https://sergiosastre.hashnode.dev/the-secrets-of-effectively-snapshot-testing-on-android)
- [UI tests vs. snapshot tests on Android: which one should I write? 🤔](https://sergiosastre.hashnode.dev/ui-tests-vs-snapshot-tests-on-android-which-one-should-i-write)
- [Design a pixel perfect Android app 🎨](https://sergiosastre.hashnode.dev/design-a-pixel-perfect-android-app-with-screenshot-testing)

## Standard UI testing

For standard UI testing, you can use the same approach as for snapshot testing Activities. The following TestRules and methods
are provided:

```kotlin
// Sets the Locale of the app under test only, i.e. the per-app language preference feature
@get:Rule
val locale = LocaleTestRule("en")

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

