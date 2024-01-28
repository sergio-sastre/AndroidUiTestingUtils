# Android UI Testing Utils

[![](https://jitpack.io/v/sergio-sastre/AndroidUiTestingUtils.svg)](https://jitpack.io/#sergio-sastre/AndroidUiTestingUtils)\
[![](https://androidweekly.net/issues/issue-508/badge)](https://androidweekly.net/issues/issue-508)

<div align="center">

<img src="https://user-images.githubusercontent.com/6097181/172724660-778176b0-a6b0-4aad-b6b4-7115ad4fc7f3.png" alt="" width="130">

</div>

A set of _TestRules_, _ActivityScenarios_ and utils to facilitate UI & screenshot testing under certain configurations, independent of the UI testing libraries you are using.\
\
\
For screenshot testing, it supports:

* **Jetpack Compose**
* **android Views** (e.g. custom Views, ViewHolders, etc.)
* **Activities**
* **Fragments**
* [**Robolectric**](./#robolectric-screenshot-tests)
* [**Cross-library** & **Shared screenshot testing**](./#cross-library-screenshot-tests) i.e. same test running either on device or on JVM.\


This library enables you to easily change the following configurations in your UI tests:

1. Locale (also [Pseudolocales](https://developer.android.com/guide/topics/resources/pseudolocales) **en\_XA** & **ar\_XB**)
   1. App Locale (i.e. per-app language preference)
   2. System Locale
2. Font size
3. Orientation
4. Custom themes
5. Dark mode / Day-Night mode
6. Display size

Wondering why verifying our design under these configurations is important? I've got you covered:

🎨 [Design a pixel perfect Android app](https://sergiosastre.hashnode.dev/design-a-pixel-perfect-android-app-with-screenshot-testing)\
\


### Executable examples

For examples of usage of this library in combination with Shot, Dropshots & Roborazzi, check the following repo:

📸 [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground)\
\


### Future development

In the near future, there are plans to also support, among others:

1. Dynamic colors
2. Reduce snapshot testing flakiness
3. Folding features
4. Accessibility features

### Sponsors

Thanks to [Screenshotbot](https://screenshotbot.io) for their support! [![](https://user-images.githubusercontent.com/6097181/192350235-b3b5dc63-e7e7-48da-bdb6-851a130aaf8d.png)](https://screenshotbot.io)

By using Screenshotbot instead of the in-build record/verify modes provided by most screenshot libraries, you'll give your colleages a better developer experience, since they will not be required to manually record screenshots after every run, instead getting notifications on their Pull Requests.\


### Table of Contents

* [Integration](./#integration)
  * [Application modules](./#application-modules)
  * [In-App Locale](./#in-app-locale)
  * [System Locale](./#system-locale)
  * [Robolectric screenshot tests](./#robolectric-screenshot-tests)
  * [Cross-library screenshot tests](./#cross-library-screenshot-tests)
    * [Basic configuration](./#basic-configuration)
    * [Shared tests](./#shared-tests)
* [Usage](./#usage)
  * [Screenshot testing examples](./#screenshot-testing-examples)
    * [Activity](./#activity)
    * [Android View](./#android-view)
    * [Jetpack Compose](./#jetpack-compose)
    * [Fragment](./#fragment)
    * [Bitmap](./#bitmap)
    * [Android-Testify](./#android-testify)
      * [Activity](./#activity-testify)
      * [Android View](./#android-view-testify)
      * [Jetpack Compose](./#jetpack-compose-testify)
      * [Fragment](./#fragment-testify)
    * [Robolectric](./#robolectric)
      * [Activity](./#activity-rng)
      * [Android View](./#android-view-rng)
      * [Jetpack Compose](./#jetpack-compose-rng)
      * [Fragment](./#fragment-rng)
      * [Multiple devices and configs](./#multiple-devices-and-configs-all-combined)
    * [Cross-library](./#cross-library)
  * [Utils](./#utils)
  * [Reading on screenshot testing](./#reading-on-screenshot-testing)
  * [Standard UI testing](./#standard-ui-testing)
* [Code attributions](./#code-attributions)
* [Contributing](./#contributing)

### Integration

Add jitpack to your root `build.gradle` file:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    androidTestImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:utils:<version>')
}
```

For compatibility purposes, choose the `version` that better fits your project

| AndroidUiTestingUtils | CompileSDK | Kotlin | Compose Compiler |
| --------------------- | ---------- | ------ | ---------------- |
| 2.2.0                 | 34         | 1.9.x  | 1.5.x            |
| < 2.2.0               | 33+        | 1.8.x  | 1.4.x            |

> Note The Kotlin and Compose compiler versions need to be compatible. Check the compatibility map [here](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)

#### Application modules

If you get any error due to "Activity not found" in your application module, add the following to the `androidTest/manifest`

```xml
<activity android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$PortraitSnapshotConfiguredActivity"/>
<activity android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$LandscapeSnapshotConfiguredActivity"
    android:screenOrientation="landscape"/>
```

#### In-App Locale

AndroidUiTestingUtils also supports [per-app language preferences](https://developer.android.com/guide/topics/resources/app-languages) in **instrumentation tests**. In order to change the In-App Locale, you need to use the `InAppLocaleTestRule`. For that it is necessary to add the following dependency in your `build.gradle`

```kotlin
androidTestImplementation 'androidx.appcompat:appcompat:1.6.0-alpha04' // or higher version!
```

Use this rule to test Activities with in-app Locales that differ from the System Locale.

#### System Locale

To change the System Locale via SystemLocaleTestRule (i.e. necessary for snapshot testing Activities only), you also need to add the following permission to your `androidTest/manifest` . For multi-module apps, do this in the app module.

```xml
<!-- Required to change the System Locale via SystemLocaleTestRule (e.g. for snapshot testing Activities) -->
<uses-permission android:name="android.permission.CHANGE_CONFIGURATION"
    tools:ignore="ProtectedPermissions" />
```

#### Robolectric screenshot tests

Robolectric supports screenshot testing via [Robolectric Native graphics (RNG)](https://github.com/robolectric/robolectric/releases/tag/robolectric-4.10) since 4.10. With AndroidUiTestingUtils, you can configure your robolectric screenshot tests similar to how you'd do it with on-device tests! Moreover, it offers some utility methods to generate Robolectric screenshot tests for different screen sizes and configurations!

For that, add the following dependencies in your `build.gradle`:

```kotlin
// version 2.0.0+
testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:utils:<version>'
testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:robolectric:<version>'
```

If you get any error due to "Activity not found" in your application module, add the following to your `debug/manifest`

```xml
<activity android:name="sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioConfigurator$SnapshotConfiguredActivity"/>
```

You can find some examples in [this section](./#robolectric) as well as executable screenshot tests in the repo [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground) to try it on your own!

#### Cross-library screenshot tests

This library provides support for running the **very same screenshot tests for your Composables or Android Views across different libraries**, without rewriting them!

I wrote about why you might need it and how AndroidUiTestingUtils supports this in these 2 blog posts:

* 🌎 [A World Beyond Libraries: Cross-Library screenshot tests on Android](https://sergiosastre.hashnode.dev/a-world-beyond-libraries-cross-library-screenshot-tests-on-android)
* 📚 [Write Once, Test Everywhere: Cross-Library Screenshot Testing with AndroidUiTestingUtils 2.0.0](https://sergiosastre.hashnode.dev/write-once-test-everywhere-cross-library-screenshot-testing-with-androiduitestingutils)

Currently, it provides out-of-the-box support for the following screenshot testing libraries 1:

* [Paparazzi](https://github.com/cashapp/paparazzi)
* [Shot](https://github.com/pedrovgs/Shot)
* [Dropshots](https://github.com/dropbox/dropshots)
* [Roborazzi](https://github.com/takahirom/roborazzi)
* [Android-Testify](https://github.com/ndtp/android-testify)

You can also add support for your own solution / another library by implementing `ScreenshotTestRuleForComposable` or `ScreenshotTestRuleForView` interfaces and use that implementation in `ScreenshotLibraryTestRuleForComposable` or `ScreenshotLibraryTestRuleForView` respectively, as we'll see below.

1 Support for Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android) and QuickBird Studios [snappy](https://github.com/QuickBirdEng/kotlin-snapshot-testing) is on the roadmap.\
\


**Basic configuration**

This section covers the basics: how to configure cross-library screenshot test that will run with the library of your choice. The main benefit is, that to switch to another library you won't need to rewrite all your tests!\
It's possible to configure several libraries though1. For shared screenshot tests (i.e. on-device + JVM), check [the next section](./#shared-tests)\
\


1. First of all, configure the screenshot testing library you want your tests to support, as if you'd write them with that specific library. Visit its respective Github page for more info.\
   \
   It's recommended to use the AndroidUiTestingUtils version that corresponds to the screenshot library version you're using:\


| AndroidUiTestingUtils | Roborazzi     | Paparazzi | Dropshots | Shot  | Android-Testify |
| --------------------- | ------------- | --------- | --------- | ----- | --------------- |
| _2.2.0_               | 1.8.0-alpha-6 | _1.3.2_   | 0.4.1     | 6.0.0 | 2.0.0           |
| 2.1.0                 | 1.8.0-alpha-6 | 1.3.1     | 0.4.1     | 6.0.0 | 2.0.0           |
| 2.0.1                 | 1.7.0-rc-1    | 1.3.1     | 0.4.1     | 6.0.0 | -               |
| 2.0.0                 | 1.5.0-rc-1    | 1.3.1     | 0.4.1     | 6.0.0 | -               |

> Note If having troubles with Paparazzi, beware of the [release notes of 1.3.2](https://github.com/cashapp/paparazzi/releases/tag/1.3.2)

2. After that, include the following dependencies in the `build.gradle` of the module that will include your cross-library screenshot tests.

```
dependencies {
    debugImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:utils:<version>')

    // NOTE: From here down, add only those for the libraries you're planning to use

    // For Shot support
    debugImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:shot:<version>')

    // For Dropshots support
    debugImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:dropshots:<version>')
    
    // For Android-Testify support
    debugImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:android-testify:<version>')

    // For Paparazzi support:
    //   2.2.0 -> AGP 8.1.1+
    // < 2.2.0 -> AGP 8.0.0+
    debugImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:mapper-paparazzi:<version>')
    testImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:paparazzi:<version>')

    // For Roborazzi support
    debugImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:mapper-roborazzi:<version>')
    testImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:robolectric:<version>')
    testImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:roborazzi:<version>')
}
```

If using Roborazzi, enable robolectric native graphics through gradle as well

```groovy
android {
    testOptions {
        ...
        unitTests {
            ...
            all {
                // NOTE: Only necessary if adding Roborazzi
                systemProperty 'robolectric.graphicsMode', 'NATIVE'
            }
        }
    }
}
```

If using Android-Testify, you need to define the annotation it will use to identify screenshot tests to execute.

```groovy
testify {
    ...
    screenshotAnnotation 'sergio.sastre.uitesting.utils.crosslibrary.annotations.CrossLibraryScreenshot'
}
```

3. Create the corresponding `ScreenshotLibraryTestRuleForComposable` or `ScreenshotLibraryTestRuleForView`, for instance:

```kotlin
class MyLibraryScreenshotTestRule(
    override val config: ScreenshotConfigForComposable,
) : ScreenshotLibraryTestRuleForComposable(config) {

    override fun getScreenshotLibraryTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRule {
        // The TestRule that uses the desired library. Could also be 
        // roborazziScreenshotTestRule, dropshotsScreenshotTestRule...
        // or even your own rule
        return paparazziScreenshotTestRule
    }
}
```

Executing your screenshot tests with another library will just require that you change the ScreenshotLibraryTestRule accordingly!

4. Finally, write your tests with that `MyLibraryScreenshotTestRule`. Put them under the corresponding folder, i.e `unitTest` (e.g. Roborazzi & Paparazzi) or `androidTest`(e.g. Dropshots, Shot, Android-Testify). For an example, see [this section](./#cross-library).\
   \


Want to try it out? Check out these executable examples:

* [Cross-library screenshot test example](https://github.com/sergio-sastre/Android-screenshot-testing-playground/blob/master/lazycolumnscreen/crosslibrary/src/sharedTest/java/com/example/road/to/effective/snapshot/testing/lazycolumnscreen/crosslibrary/CoffeeDrinkAppBarComposableTest.kt)
* [Parameterized Cross-library screenshot test example](https://github.com/sergio-sastre/Android-screenshot-testing-playground/blob/master/lazycolumnscreen/crosslibrary/src/sharedTest/java/com/example/road/to/effective/snapshot/testing/lazycolumnscreen/crosslibrary/parameterized/CoffeeDrinkListComposableParameterizedTest.kt)

**Shared tests**

If instead of using just one screenshot testing library at once, you want to enable shared tests (i.e same test running either on the JVM or on a device/emulator),you have to share resources between unitTests and AndroidTests.\
The easiest way is to add this in the `build.gradle` of the module where you'll write shared tests and then write your screenshot tests under `src/sharedTest`,

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

> **Warning**\
> Android Studio will show errors if sharedTests are defined in an application module. Consider creating an extra library module for testing the UI of your application module.

Now follow steps 1. & 2. as in the [Basic configuration](./#basic-configuration) section for each library. After that:

3. Create the corresponding `SharedScreenshotLibraryTestRuleForComposable` or `SharedScreenshotLibraryTestRuleForView`, for instance:

```kotlin
class CrossLibraryScreenshotTestRule(
    override val config: ScreenshotConfigForComposable,
) : SharedScreenshotLibraryTestRuleForComposable(config) {

    override fun getJvmScreenshotTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRule {
        return paparazziScreenshotTestRule // or roborazziScreenshotTestRule, or your own rule
    }

    override fun getInstrumentedScreenshotTestRule(config: ScreenshotConfigForComosable): ScreenshotTestRule {
        return dropshotsScreenshotTestRule // or shotScreenshotTestRule, androidTestifyScreenshotTestRule, or your own rule
    }
}
```

4. Finally, write your tests with the `CrossLibraryScreenshotTestRule`. For an example, see [this section](./#cross-library).\
   \
   Put them under the `sharedTest` folder we've just defined.

1 It's very likely you'll configure your screenshot tests to run with maximum 1 on-device (e.g. either Shot, Dropshots or Android-Testify) and/or 1 JVM library (e.g.. either Paparazzi or Roborazzi). In that case, this is enough.\
But if you wish to configure many on-device or many JVM libraries at the same time, and dynamically pick the one your screenshot tests run with, you'll need some extra configuration. For that you can use a custom gradle property passed via command line e.g. `-PscreenshotLibrary=shot`. Check these links for advice on how to configure the gradle file and the `SharedScreenshotTestRule` to get it working:\


* [build.gradle](https://github.com/sergio-sastre/Android-screenshot-testing-playground/blob/master/lazycolumnscreen/crosslibrary/build.gradle)
* [CrossLibraryScreenshotTestRule.kt](https://github.com/sergio-sastre/Android-screenshot-testing-playground/blob/master/lazycolumnscreen/crosslibrary/src/sharedTest/java/com/example/road/to/effective/snapshot/testing/lazycolumnscreen/crosslibrary/utils/CrossLibraryScreenshotTestRule.kt)

## Usage

### Screenshot testing examples

The examples use [pedrovgs/Shot](https://github.com/pedrovgs/Shot). It also works with any other on-device screenshot testing library that supports ActivityScenarios, like Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android), Dropbox [Dropshots](https://github.com/dropbox/dropshots) or with a custom screenshot testing solution. For Android-Testify, which does not support ActivityScenario but the deprecated ActivityTestRule, take a look at the [Activity-Testify](./) section.

You can find more complete examples with Shot, Dropshots, Roborazzi and Android-Testify in the [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground) repo.

#### Activity

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
        name = "your_unique_screenshot_name",
    )
}
```

In case you don't want to/cannot use the rule, you can use \*\* ActivityScenarioConfigurator.ForActivity()\*\* directly in the test. Currently, this is the only means to set

1. An InAppLocaleTestRule for per-app language preferences

Apart from that, this would be equivalent:

```kotlin
// Sets the Locale of the app under test only, i.e. the per-app language preference feature
@get:Rule
val inAppLocale = InAppLocaleTestRule("ar")

// Sets the Locale of the Android system
@get:Rule
val systemLocale = SystemLocaleTestRule("en")

@get:Rule
val fontSize = FontSizeTestRule(FontSize.HUGE)

@get:Rule
val displaySize = DisplaySizeTestRule(DisplaySize.LARGEST)

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

#### Android View

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

In case you don't want to/cannot use the rule, you can use \*\* ActivityScenarioConfigurator.ForView()\*\*. This would be its equivalent:

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

If the View under test contains system Locale dependent code, like `NumberFormat.getInstance(Locale.getDefault())`, the Locale formatting you've set via `ActivityScenarioConfigurator.ForView().setLocale("my_locale")` will not work. That's because NumberFormat is using the Locale of the Android system, and not that of the Activity we've configured. Beware of using `instrumenation.targetContext` in your tests when using getString() for the very same reason: use Activity's context instead.\
To solve that issue, you can do one of the following:

1. Use `NumberFormat.getInstance(anyViewInsideActivity.context.locales[0])` in your production code.
2. Use `SystemLocaleTestRule("my_locale")` in your tests instead of `ActivityScenarioConfigurator.ForView().setLocale("my_locale")`.

#### Jetpack Compose

The simplest way is to use the **ActivityScenarioForComposableRule**, to avoid the need for:

1. calling createEmptyComposeRule()
2. closing the ActivityScenario.

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

In case you don't want to/cannot use the rule, you can use \*\* ActivityScenarioConfigurator.ForComposable()\*\* together with **createEmptyComposeRule()**. This would be its equivalent:

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

If you are using a screenshot library that cannot take a composeTestRule as argument (e.g. Dropshots), you can still screenshot the Composable as follows:

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

If the Composable under test contains system Locale dependent code, like `NumberFormat.getInstance(Locale.getDefault())`, the Locale formatting you've set via `ActivityScenarioConfigurator.ForComposable().setLocale("my_locale")` will not work. That's because NumberFormat is using the Locale of the Android system, and not that of the Activity we've configured, which is applied to the LocaleContext of our Composables.\
To solve that issue, you can do one of the following:

1. Use `NumberFormat.getInstance(LocaleContext.current.locales[0])` in your production code.
2. Use `SystemLocaleTestRule("my_locale")` in your tests instead of `ActivityScenarioConfigurator.ForComposable().setLocale("my_locale")`.

#### Fragment

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

In case you don't want to/cannot use the rule, you can use the plain \*\* FragmentScenarioConfigurator\*\*. This would be its equivalent:

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

#### Bitmap

Most screenshot testing libraries use `Canvas` with `Bitmap.Config.ARGB_8888` as default for generating bitmaps (i.e. the screenshots) from the Activities/Fragments/ViewHolders/Views/Dialogs/Composables... That's because Canvas is supported in all Android versions.\
Nevertheless, such bitmaps generated using `Canvas` have some limitations, e.g. UI elements are rendered without considering elevation.

Fortunately, such libraries let you pass the bitmap (i.e.the screenshot) as argument in their record/verify methods. In doing so, we can draw the views with elevation1 to a bitmap with `PixelCopy`.

AndroidUiTestingUtils provides methods to easily generate bitmaps from the Activities/Fragments/ViewHolders/Views/Dialogs/Composables:

1. `drawToBitmap(config = Bitmap.Config.ARGB_8888)` -> uses `Canvas` under the hood
2. `drawToBitmapWithElevation(config = Bitmap.Config.ARGB_8888)` -> uses `PixelCopy` under the hood

and one extra to fully screenshot a scrollable view: 3. `drawFullScrollableToBitmap(config = Bitmap.Config.ARGB_8888)` -> uses `Canvas` under the hood

Differences between Bitmaps generated via `Canvas` and `Pixel Copy` might be specially noticeable in API 31:

<div align="center">

<img src="https://user-images.githubusercontent.com/6097181/211920600-6cfcdde3-1fd6-4b23-84d1-3eae587c811d.png" alt="" width="350">

</div>

> **Note**\
> If using `PixelCopy` with ViewHolders/Views/Dialogs/Composables, consider launching the container Activity with transparent background for a more realistic screenshot of the UI component.
>
> ```kotlin
> ActivityScenarioConfigurator.ForView() // or .ForComposable()
>       ...
>       .launchConfiguredActivity(backgroundColor = Color.TRANSPARENT)
> ```
>
> or
>
> ```kotlin
> ActivityScenarioForViewRule( // or ActivityScenarioForComposableRule()
>       viewConfig = ...,
>       backgroundColor = Color.TRANSPARENT,
> )
> ```
>
> Otherwise it uses the default Dark/Light Theme background colors (e.g. white and dark grey).

Using `PixelCopy` instead of `Canvas` comes with its own drawbacks though. In general, don't use PixelCopy to draw views that don't fit on the screen.\


| Canvas                                                                   |                                     PixelCopy                                     |
| ------------------------------------------------------------------------ | :-------------------------------------------------------------------------------: |
| <p>✅ Can render elements beyond the screen,<br>e.g. long ScrollViews</p> | <p>❌ Cannot render elements beyond the screen,<br>resizing if that's the case</p> |
| ❌ Ignores elevation2 of UI elements while drawing                        |                ✅ Considers elevation2 of UI elements while drawing                |

1 Robolectric 4.10 or lower cannot render shadows or elevation with RNG, as stated in [this issue](https://github.com/robolectric/robolectric/issues/8081) 2 Elevation can be manifested in many ways: a UI layer on top of another or a shadow in a CardView.

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

#### Android-Testify

Android-Testify does not support ActivityScenarios but the deprecated ActivityTestRules. Moreover, Android-Testify:

1. Requires your Activity under test to be `open` and create your own Test Activity inheriting from it and TestifyResourcesOverride1 to support SystemLocale and FontSize.
2. Does not support UiMode (Light/Dark mode), DisplaySize or custom themes.

The TestRules defined in [Standard UI testing](./#standard-ui-testing) are compatible with Android-Testify and they do not require that you make your Activities open. However, for faster Screenshot Tests and better readability, AndroidUiTesting also provides some wrappers around Android-Testify as well as some extension methods. Add the following dependency in your gradle file to access them:

```groovy
androidTestImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:android-testify:<version>') // version 2.1.0+
```

In the following subsections you'll find screenshot test examples with such wrappers: `ScreenshotRuleWithConfigurationForView`, `ScreenshotRuleWithConfigurationForFragment` and `ComposableScreenshotRuleWithConfiguration`

### Activity (Testify)

Use the TestRules defined in [Standard UI testing](./#standard-ui-testing) section with Android-Testify ScreenshotRule

### Android View (Testify)

for a View Holder, for instance

```kotlin
@get:Rule
var screenshotRule = 
    ScreenshotRuleWithConfigurationForView(
        exactness = 0.85f,
        config = ViewConfigItem(
            uiMode = UiMode.DAY,
            locale = "en",
            orientation = Orientation.PORTRAIT
        ),
    )

@ScreenshotInstrumentation
@Test
fun snapMemoriseViewHolderHappyPath() {
    screenshotRule
        .setTargetLayoutId(R.layout.memorise_row)
        .setViewModifications { targetLayout ->
            MemoriseViewHolder(
                container = targetLayout,
                itemEventListener = null,
                animationDelay = 0L
            ).apply {
                bind(
                    generateMemoriseItem(
                        rightAligned = false,
                        activity = screenshotRule.activity
                    )
                )
            }
        }
        .setScreenshotFirstView() // this is to screenshot the view only
        .withExperimentalFeatureEnabled(GenerateDiffs)
        .assertSame(name = "nameOfMyScreenshot")
}
```

### Jetpack Compose (Testify)

```kotlin
@get:Rule
val screenshotRule = 
    ComposableScreenshotRuleWithConfiguration(
        exactness = 0.85f,
        config = ComposableConfigItem(
            locale = "en",
            uiMode = UiMode.DAY,
            fontSize = FontSize.NORMAL,
            displaySize = DisplaySize.NORMAL,
            orientation = Orientation.PORTRAIT
        )
    )

@ScreenshotInstrumentation
@Test
fun snapComposable() {
    screenshotRule
        .setCompose { myComposable() }
        .withExperimentalFeatureEnabled(GenerateDiffs)
        .assertSame(name = "nameOfMyScreenshot")
}
```

### Fragment (Testify)

```kotlin
@get:Rule
val screenshotRule =
    ScreenshotRuleWithConfigurationForFragment(
        exactness = 0.85f,
        fragmentClass = MyFragment::class.java,
        fragmentArgs = bundleOf("key" to "value"),
        config = FragmentConfigItem(
            locale = "ar_XB",
            theme = R.style.myCustomTheme,
            uiMode = UiMode.NIGHT,
            fontSize = FontSize.SMALL,
            displaySize = DisplaySize.SMALL,
            orientation = Orientation.LANDSCAPE
        ),
    )

@ScreenshotInstrumentation
@Test
fun snapFragment() {
    screenshotRule
        .withExperimentalFeatureEnabled(GenerateDiffs)
        .assertSame(name = "nameOfMyScreenshot")
}
```

#### Robolectric

AndroidUiTesting includes some special `ActivityScenarioConfigurators` and `FragmentScenarioConfigurators` that are additionally safe-thread, what allows unit tests in parallel without unexpected behaviours.

It supports _Jetpack Compose_\*, **android Views** (e.g. custom Views, ViewHolders, etc.), **Activities** and **Fragments**.

Check out some examples below. It uses [Roborazzi](https://github.com/takahirom/roborazzi) as screenshot testing library.

#### Activity (RNG)

Here with Junit4 test rule

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
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
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
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

#### Android View (RNG)

Here with Junit4 test rule

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
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
        val layout =
            robolectricActivityScenarioForViewRule.inflateAndWaitForIdle(R.layout.memorise_row)

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
```

or without Junit4 test rules

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
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

#### Jetpack Compose (RNG)

Here with `RobolectricActivityScenarioForComposableRule` test rule

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
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

    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen` in the Rule
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

The snapComposable method can be simplified further if adding the following dependency

```groovy
testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:roborazzi:<version>' // version 2.2.0+
```

and then

```kotlin
@Test
fun snapComposable() {
    activityScenarioForComposableRule
       .captureRoboImage("path/MyComposable.png"){
          AppTheme { // this theme must use isSystemInDarkTheme() internally
             yourComposable()
          }
       }
}
```

You can also use _AndroidUiTestingUtils_ without `RobolectricActivityScenarioForComposableRule` test rule

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
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

#### Fragment (RNG)

Here with Junit4 test rule

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
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
            .requireView()
            .captureRoboImage("path/MyFragment.png")
    }
}
```

or without Junit4 test rules

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
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
            .requireView()
            .captureRoboImage("path/MyActivity.png")

        fragmentScenario.close()
    }
}
```

#### Multiple Devices and Configs all combined

_AndroidUiTestingUtils_ also helps generate all parameters of a set of UiStates under a given set of devices and configurations. For that, use the correpsonding type depending on what you are testing:

* _Activity_: `TestDataForActivity<MyEnum>`
* _Fragment_: `TestDataForFragment<MyEnum>`
* _Composable_: `TestDataForComposable<MyEnum>`
* _View_ (e.g. Dialogs, ViewHolders): `TestDataForView<MyEnum>`

Here an example with Views

```kotlin
@RunWith(ParameterizedRobolectricTestRunner::class)
class MultipleDevicesAndConfigsMemoriseTest(
    private val testItem: TestDataForView<UiStateEnum>
) {
    
    // Define possible uiStates
    enum class UiStateEnum(val value: MyUiState) {
        UI_STATE_1(myUiState1),
        UI_STATE_2(myUiState2),
    }

    // Here we generates all possible combinations
    // i.e. 2 UiStates x 2 Devices x 2 Configs = 8 test
    companion object {
    @JvmStatic
    @ParameterizedRobolectricTestRunner.Parameters
    fun testItemProvider(): Array<TestDataForView<UiStateEnum>> =
       TestDataForViewCombinator(
          uiStates = MyEnum.values()
       )
       .forDevices(
          DeviceScreen.Phone.PIXEL_4A,
          DeviceScreen.Tablet.MEDIUM_TABLET,
       )
       .forConfigs(
          ViewConfigItem(uiMode = DAY, fontSize = SMALL),
          ViewConfigItem(uiMode = NIGHT, locale = "ar"),
       )
       .combineAll()

    // Passed config & device to the scenario
    @get:Rule
    val rule = RobolectricActivityScenarioForViewRule(
        config = testItem.config,
        deviceScreen = testItem.device,
    )

    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen`in the rule
    @Test
    fun snapView() {
       val layout = rule.inflateAndWaitForIdle(R.layout.my_view)

       val view = waitForMeasuredView {
         layout.bind(item = testItem.uiState.value)
       }

       // testItem.screenshotId generates a unique ID for the screenshot
       // based on the MyEnum.name, configuration & device name
       // e.g:
       // 1. UI_STATE_1_DAY_FONT_SMALL_PIXEL_4A
       // 2. UI_STATE_1_DAY_FONT_SMALL_MEDIUM_TABLET
       // 3. UI_STATE_1_AR_NIGHT_PIXEL_4A
       // 4. UI_STATE_1_AR_NIGHT_MEDIUM_TABLET
       // 5. UI_STATE_2_DAY_FONT_SMALL_PIXEL_4A
       // 6,7,8. UI_STATE_2...
       view.captureRoboImage("path/${testItem.screenshotId}.png")
    }
}
```

#### Cross-library

Supports Android Views as well as Composables.

To configrue cross-library screenshot tests, you need to follow the steps in [this section](./#cross-library-screenshot-tests). Once done, write your tests like this:

1. Define a ScreenshotTestRule with the default configuration, which can be overriden in your tests

```kotlin
fun defaultCrossLibraryScreenshotTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRule =
    CrossLibraryTestRule(config)
        // Optional: Define special configurations for each library you're using  
        .configure(
            ShotConfig(bitmapCaptureMethod = PixelCopy())
        ).configure(
            DropshotsConfig(
                bitmapCaptureMethod = PixelCopy(),
                resultValidator = ThresholdValidator(0.15f)
            )
        ).configure(
            AndroidTestifyConfig(bitmapCaptureMethod = PixelCopy())
        ).configure(
            PaparazziConfig(deviceConfig = DeviceConfig.NEXUS_4)
        ).configure(
            RoborazziConfig(
                filePath = File(userTestFilePath()).path,
                deviceScreen = DeviceScreen.Phone.NEXUS_4,
            )
        )
```

2. Write a screenshot test

```kotlin
@RunWith(CrossLibraryScreenshotTestRunner::class)
class MyCrossLibraryScreenshotTest {
    @get:Rule
    val screenshotRule =
        defaultCrossLibraryScreenshotTestRule(
            config = ScreenshotConfigForComposable(
                uiMode = UiMode.DAY,
                orientation = Orientation.LANDSCAPE,
                locale = "en",
                fontScale = FontSize.NORMAL,
                displaySize = DisplaySize.NORMAL,
            ),
        )
        // Optional: Override the default config for the desired libraries
        .configure(
            PaparazziConfig(deviceConfig = DeviceConfig.PIXEL_XL)
        )

    @CrossLibraryScreenshot // required for Android-Testify
    @Test
    fun snapComposable() {
        screenshotRule.snapshot(name = "your_unique_screenshot_name") {
            MyComposable()
        }
    }
}
```

3. or a parameterized screenshot test

```kotlin
import org.junit.runners.Parameterized // annotation @Parameterized.Parameters

@RunWith(ParameterizedCrossLibraryScreenshotTestRunner::class)
class MyParameterizedCrossLibraryScreenshotTest(
    private val testItem: MyTestItemEnum,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun testItemProvider(): Array<MyTestItemEnum> = MyTestItemEnum.values()
    }
    
    @get:Rule
    val screenshotRule =
        defaultCrossLibraryScreenshotTestRule(config = testItem.config)
        // Optional: Override the default config for the desired libraries
        .configure(
            PaparazziConfig(deviceConfig = DeviceConfig.PIXEL_XL)
        )

    @CrossLibraryScreenshot // required for Android-Testify
    @Test
    fun snapComposable() {
        screenshotRule.snapshot(name = testItem.name) {
            MyComposable()
        }
    }
}
```

> **Warning**\
> You must define a `testInstrumentationRunner` in build.gradle of type `androidx.test.runner.AndroidJUnitRunner` for Parameterized Cross-Library screenshot tests to work. For instance, `com.karumi.shot.ShotTestRunner`if using Shot.

### Utils

**Wait**

1. `waitForActivity`: Analog to the one defined in [pedrovgs/Shot](https://github.com/pedrovgs/Shot). It's also available in this library for compatibility with other screenshot testing frameworks like Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android) .\
   \

2. `waitForFragment`: Analog to waitForActivity but for Fragment.\
   \

3. `activity.waitForComposeView`: Returns the root Composable in the activity as a ComposeView. You can call later `drawToBitmap` or `drawToBitmapWithElevation` on it to screenshot test its corresponding bitmap.\
   \

4. `waitForMeasuredView/Dialog/ViewHolder(exactWidth, exactHeight)`: Inflates the layout in the main thread, sets its width and height to those given, and waits till the thread is idle, returning the inflated view. Comes in handy with libraries that do not support, to take a screenshot with a given width/height, like Dropshots.\
   \


> **Warning**\
> Prefer `waitForMeasuredView` over `waitForView` (which is discouraged), specially if using Dropshots:
>
> <img src="https://user-images.githubusercontent.com/6097181/211920753-35ee8f0b-d661-4623-8619-418c3972f1c2.png" alt="" data-size="original">

**Inflate or measure**\


5. `activity.inflate(R.layout_of_your_view)`: Use it to inflate android Views with the activity's context configuration. In doing so, the configuration becomes effective in the view. It also adds the view to the Activity's root.\
   \

6. `activity.inflateAndWaitForIdle(R.layout_of_your_view)`: Like activity.inflate, but waits till the view is Idle to return it. Do not wrap it with waitForMeasuredView{} or it will throw an exception.\
   \

7. `MeasureViewHelpers`: Analog to the `ViewHelpers` defined in Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android) . In most cases, you don't need to use it directly but via `waitForMeasuredView(exactWidth, exactHeight)`, which calls `MeasuredViewHelpers` under the hood.

### Reading on screenshot testing

* [An introduction to snapshot testing on Android in 2021 📸](https://sergiosastre.hashnode.dev/an-introduction-to-snapshot-testing-on-android-in-2021)
* [The secrets of effectively snapshot testing on Android 🔓](https://sergiosastre.hashnode.dev/the-secrets-of-effectively-snapshot-testing-on-android)
* [UI tests vs. snapshot tests on Android: which one should I write? 🤔](https://sergiosastre.hashnode.dev/ui-tests-vs-snapshot-tests-on-android-which-one-should-i-write)
* [Design a pixel perfect Android app 🎨](https://sergiosastre.hashnode.dev/design-a-pixel-perfect-android-app-with-screenshot-testing)

### Standard UI testing

For standard UI testing, you can use the same approach as for snapshot testing Activities. The following TestRules and methods are provided:

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

@get:Rule
val disableAnimations = DisableAnimationsTestRule("en")
```

> **Warning**\
> When using _DisplaySizeTestRule_ and _FontSizeTesRule_ together in the same test, make sure your emulator has enough RAM and VM heap to avoid Exceptions when running the tests. The recommended configuration is the following:
>
> * RAM: 4GB
> * VM heap: 1GB

## Code attributions

This library has been possible due to the work others have done previously. Most TestRules are based on code written by others:

* SystemLocaleTestRule -> [Screengrab](https://github.com/fastlane/fastlane/tree/master/screengrab/screengrab-lib/src/main/java/tools.fastlane.screengrab/locale)
* FontSizeTestRule -> [Novoda/espresso-support](https://github.com/novoda/espresso-support/tree/master/core/src/main/java/com/novoda/espresso)
* UiModeTestRule -> [AdevintaSpain/Barista](https://github.com/AdevintaSpain/Barista)
* Orientation change for activities -> [Shopify/android-testify](https://github.com/Shopify/android-testify/)
* MeasureViewHelpers -> a copy of ViewHelpers from Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android)

Moreover, to enable cross-library screenshot tests this library uses the following screenshot testing libraries under the hood:

* [Paparazzi](https://github.com/cashapp/paparazzi)
* [Shot](https://github.com/pedrovgs/Shot)
* [Dropshots](https://github.com/dropbox/dropshots)
* [Roborazzi](https://github.com/takahirom/roborazzi)

## Contributing

1. Create an issue in this repo
2. Fork the repo [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground)
3. In that repo, add an example and test where the bug is reproducible/ and showcasing the new feature.
4. Once pushed, add a link to the PR in the issue created in this repo and add @sergio-sastre as a reviewer.
5. Once reviewed and approved, create an issue in this repo.
6. Fork this repo and add the approved code from the other repo to this one (no example or test needed). Add @sergio-sastre as a reviewer.
7. Once approved, I will merge the code in both repos, and you will be added as a contributor to [Android UI testing utils](https://github.com/sergio-sastre/AndroidUiTestingUtils) as well as [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground) .

I'll try to make the process easier in the future if I see many issues/feature requests incoming :)

\
\
[Android UI testing utils logo modified from one by Freepik - Flaticon](https://www.flaticon.com/free-icons/ninja)
