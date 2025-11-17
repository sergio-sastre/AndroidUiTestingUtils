# Instrumentation setup

## Application modules

If you get any error due to "Activity not found" in your application module, add the following to the `androidTest/manifest`

```xml
<activity android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$PortraitSnapshotConfiguredActivity"/>
<activity android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$LandscapeSnapshotConfiguredActivity"
    android:screenOrientation="landscape"/>
```

## System Locale

To change the System Locale via _SystemLocaleTestRule_ (i.e. necessary for snapshot testing Activities only), you also need to add the following permission to your `androidTest/manifest`. For multi-module apps, do this in the app module.

```xml
<!-- Required to change the System Locale via SystemLocaleTestRule (e.g. for snapshot testing Activities) -->
<uses-permission android:name="android.permission.CHANGE_CONFIGURATION"
    tools:ignore="ProtectedPermissions" />
```

And add the TestRule to your tests

```kotlin
@get:Rule
val systemLocale = SystemLocaleTestRule("en")
```

## In-App Locale

_AndroidUiTestingUtils_ also supports [per-app language preferences](https://developer.android.com/guide/topics/resources/app-languages) in **instrumentation tests**. In order to change the In-App Locale, you need to use the `InAppLocaleTestRule`. For that it is necessary to add the following dependency in your `build.gradle`

```kotlin
androidTestImplementation 'androidx.appcompat:appcompat:1.6.0-alpha04' // or higher version!
```

Use this rule to test Activities with in-app Locales that differ from the System Locale. You''ll also need to pass an `ActivityScenarioRule` or `ActivityScenarioForActivityRule` to apply the inAppLocale properly regardless the API level.

```kotlin
@get:Rule
val inAppLocale = InAppLocaleTestRule(
    locale = "en",
    activityScenarioRule = activityScenarioRule
)
```
