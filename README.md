# AndroidUiTestingUtils
A set of *TestRules*, *ActivityScenarios* and utils to facilitate UI testing & screenshot testing under certain configurations, independent of the UI testing framework you are using.
For screenshot testing, it supports **Jetpack Compose**, **android Views** (e.g. custom Views, ViewHolders, etc.) and **Activities**.
</br></br>
Currently, with this library you can easily change the following configurations in your instrumented tests:
1. Locale (also Pseudolocales **en_XA** & **ar_XB**)
2. FontSize
3. Orientation
4. Dark Mode /Day-Night Mode

In the near future, there are plans to also support, among others:
1. Display
2. FragmentScenario
3. Reduce snapshot testing flakiness

## Table of Contents
- [Integration](#integration)
- [Usage](#usage)
    - [Configuration](#configuration)
    - [Screnshot testing examples](#screenshot-testing-examples)
        - [Activity](#activity)
        - [Android View](#android-view)
        - [Jetpack Compose](#jetpack-compose)
        - [Fragment](#fragment)
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
Add a dependency to `build.gradle`
```groovy
dependencies {
    androidTestImplementation 'com.github.sergio-sastre:AndroidUiTestingUtils:1.0.0'
}
```

# Usage
## Configuration
First, you need to add the following permission and activities to your `debug/manifest`
```xml
<!-- Required to change the Locale -->
<uses-permission
        android:name="android.permission.CHANGE_CONFIGURATION"
        tools:ignore="ProtectedPermissions" />
   ...
<!-- Required for ActivityScenarios -->
<application
   ...
   <activity android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$PortraitSnapshotConfiguredActivity"/>
   <activity android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$LandscapeSnapshotConfiguredActivity"
      android:screenOrientation="landscape"/>
   ...
</application>
```
## Screenshot testing examples
The examples use [pedrovgs/Shot](https://github.com/pedrovgs/Shot). It'd also work with Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android)
### Activity
```kotlin
@get:Rule
val locale = LocaleTestRule("en")

@get:Rule
val fontSize = FontSizeTestRule(FontSize.HUGE)
   
@Test
fun snapActivityTest() {
    // Locale and FontSize are only supported via TestRules for Activities
    val activity = ActivityScenarioConfigurator.ForActivity()
        .setOrientation(Orientation.LANDSCAPE)
        .setUiMode(UiMode.NIGHT)
        .launch(YourActivity::class.java)
        .waitForActivity()
    
    compareScreenshot(activity = activity, name = "your_unique_test_name")
}
```
### Android View
```kotlin
// example for ViewHolder
@Test
fun snapViewHolderTest() {
    val activity =
        ActivityScenarioConfigurator.ForView()
            .setFontSize(FontSize)
            .setLocale(testItem.locale)
            .setInitialOrientation(Orientation.PORTRAIT)
            .launchConfiguredActivity()
            .waitForActivity()

    val layout = waitForView {
        // handy method to inflate views with activities context -> inherits context configuration 
        activity.inflate(R.layout.your_view_holder_layout)
    }

    // handy method to asynchronously wait for layout inflation 
    val viewHolder = waitForView {
        YourViewHolder(layout).apply {
            // bind data to ViewHolder here
        }
    }
    
    compareScreenshot(
        holder = viewHolder,
        heightInPx = layout.height,
        name = "your_unique_test_name"
    )
}
```
### Jetpack Compose
```kotlin
// needs an EmptyComposeRule to be compatible with ActivityScenario
@get:Rule
val composeTestRule = createEmptyComposeRule()

@Test
fun snapComposableTest() {
    ActivityScenarioConfigurator.ForComposable()
        .setFontSize(FontSize.SMALL)
        .setLocale("de")
        .setInitialOrientation(Orientation.PORTRAIT)
        .setUiMode(UiMode.DAY)
        .launchConfiguredActivity()
        .onActivity {
            it.setContent {
                AppTheme { // uses isSystemInDarkTheme() internally
                    yourComposable()
                }
            }
        }
        .waitForActivity()

    compareScreenshot(rule = composeTestRule, name = "your_unique_test_name")
}
```
### Fragment
As of version 1.0.0, it is not supported, but will be added in the next releases. For now, you can circumvent it by creating a custom empty Activity containing the fragment under test, and do like in the example to snapshot test Activities. Keep in mind that you need to define an additional empty Activity for landscape mode to support landscape orientation.

### Utils
1. waitForActivity:
   This method is analog to the one defined in [pedrovgs/Shot](https://github.com/pedrovgs/Shot). It's also available in this library for compatibility with other screenshot testing frameworks like Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android).

2. waitForView: Inflates the layout in the main thread and waits till the inflation happens, returning the inflated view. You will need to inflate layouts with the activity context created from the ActivityScenario of this library for the configurations to become effective.

3. activity.inflate(R.layout_of_your_view): Use it to inflate android Views that need the activity context created from the ActivityScenario of this library for the configurations to become effective.

### Reading on screenshot testing
- [An introduction to snapshot testing on Android in 2021](https://sergiosastre.hashnode.dev/an-introduction-to-snapshot-testing-on-android-in-2021)
- [The secrets of effectively snapshot testing on Android](https://sergiosastre.hashnode.dev/the-secrets-of-effectively-snapshot-testing-on-android)
- [UI tests vs. snapshot tests on Android: which one should I write? 🤔](https://sergiosastre.hashnode.dev/ui-tests-vs-snapshot-tests-on-android-which-one-should-i-write)

## Standard UI testing
For standard UI testing, you can use the same approach as for snapshot testing Activities. In case you do not want to use ActivityScenario for your tests, the following TestRules and methods are provided:
```kotlin
@get:Rule
val locale = LocaleTestRule("en")

@get:Rule
val fontSize = FontSizeTestRule(FontSize.HUGE)

@get:Rule
val uiMode = DayNightRule(UiMode.NIGHT)

activity.rotateTo(Orientation.LANDSCAPE)
```
# Code attributions
This library has been possible due to the work others have done previously. Most TestRules are based on code written by others:
- LocaleTestRule -> [Screengrab](https://github.com/fastlane/fastlane/tree/master/screengrab/screengrab-lib/src/main/java/tools.fastlane.screengrab/locale)
- FontSizeTestRule -> [Novoda/espresso-support](https://github.com/novoda/espresso-support/tree/master/core/src/main/java/com/novoda/espresso)
- UiModeTestRule -> [AdevintaSpain/Barista](https://github.com/AdevintaSpain/Barista)
- Orientation change for activities -> [Shopify/android-testify](https://github.com/Shopify/android-testify/)

# Contributing
1. Create an issue in this repo
2. Fork the repo [Road to effective snapshot testing](https://github.com/sergio-sastre/RoadToEffectiveSnapshotTesting)
3. In that repo, add an example and test where the bug is reproducible/ and showcasing the new feature.
4. Once pushed, add a link to the PR in the issue created in this repo and add @sergio-sastre as a reviewer.
5. Once reviewed and approved, create an issue in this repo.
6. Fork this repo and add the approved code from the other repo to this one (no example or test needed). Add @sergio-sastre as a reviewer.
7. Once approved, I will merge the code in both repos, and you will be added as a contributor to [Android UI testing utils](https://github.com/sergio-sastre/AndroidUiTestingUtils) as well as [Road to effective snapshot testing](https://github.com/sergio-sastre/RoadToEffectiveSnapshotTesting). 

I'll try to make the process easier in the future if I see many issues/feature requests incoming :)

