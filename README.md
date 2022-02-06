# AndroidUiTestingUtils
A set of TestRules, ActivityScenarios and utils to facilitate UI Testing under certain configurations.
It currently supports the following: 
1. FontSizes
2. Locales

In the near future, there are plans to also support the following:
1. Orientation
2. Display
3. Dark mode
4. Reduce snapshot testing flakinesss

## Integration
To integrate AndroidUiTestingUtils into your project:

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
    androidTestImplementation 'com.github.sergio-sastre:AndroidUiTestingUtils:1.0.0-SNAPSHOT'
}
```

## What to keep in mind
1. For standard UI testing, use TestRules.
2. For snapshot/screenshot testing any UI component that *does not test the full Activity*, prefer ActivityScenarioConfigurator. That is because:
   - Some test rules, like FontSizeTestRule, execute the corresponding adb shell commands to change the configuration, and that change might not happen immediately.
   - ActivityScenarioConfigurator launches a default Activity with the given configuration. Therefore, such configuration changes apply to all UI elements
that are inflated with the Activity context!

**Known issue**: As of `AndroidUiTestingUtils:1.0.0-SNAPSHOT`, pseudolocales (i.e. en_XA and ar_XB) do not work. This will be fixed in the next version

## Usage
1. To use `ActivityScenarioConfigurator` you need to add the following activities to your `debug/manifest`
```xml
<application
   ...
   <activity android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$SnapshotConfiguredActivity"/>
   ...
</application>
```

2. To use `LocaleTestRule`, add this permission in your `debug.manifest:
```xml
<uses-permission android:name="android.permission.CHANGE_CONFIGURATION"
        tools:ignore="ProtectedPermissions"/>
```

### ActivityScenario example: Screenshot test with [Shot from pedrovgs](https://github.com/pedrovgs/Shot)
```kotlin
@Test
fun activityScenarioWithLocaleAndFontSize(){
    val activityScenario = 
           ActivityScenarioConfigurator.Builder()
                .setFontSize(fontScale)
                .setLocale(locale)
                .launchConfiguredActivity()
                .waitForActivity()

    val view = 
           waitForView {
                val layout = activity.inflate(layoutId)
                ...
           }

    compareScreenshot(
           view = view,
           name = testName
    )
              
}
```

Links to real examples coming soon :)
</br>

In order to test **Jetpack Compose** views with ActivityScenarioConfigurator, use `createEmptyComposeRule()`, for instance:
```kotlin

@get:Rule
val composeTestRule = createEmptyComposeRule()

@Test
fun composeWithFontSizeTest() {
    val activityScenario = 
           ActivityScenarioConfigurator.Builder()
                .setFontSize(fontScale)
                .setLocale(locale)
                .launchConfiguredActivity()
                .onActivity {
                     it.setContent {
                          myComposeView()
                     }
                }

    compareScreenshot(composeTestRule, name = testItem.testName)
}
```

### TestRule example

```kotlin

@get:Rule
val localeTestRule = LocaleTestRule(locale)

@get:Rule
val fontSizeTestRule = FontScaleRules.fontScaleTestRule(fontScale)

```

