# AndroidUiTestingUtils
A set of `TestRule`s, `ActivityScenario`s and utils to facilitate UI testing/snapshot testing under certain configurations, independent of the framework you are using.
It currently supports the following: 
1. FontSizes
2. Locales

In the near future, there are plans to also support the following, among others:
1. Orientation
2. Display
3. Dark mode
4. Reduce snapshot testing flakiness

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
1. For **standard UI testing**, use `TestRule`s.
2. For **snapshot/screenshot testing** any UI component that *does not test the full Activity*, prefer `ActivityScenarioConfigurator` to `TestRule`s. That is because:
   - Some `TestRule`s, like `FontSizeTestRule`, execute the corresponding adb shell commands to change the configuration, and that change might not happen immediately, slowing down your test execution.
   - `ActivityScenarioConfigurator` launches a default Activity with the given configuration. Therefore, such configuration changes apply to all UI elements
that are inflated with the Activity context! Check the examples in [Road-to-Effective-Snapshot-Testing](https://github.com/sergio-sastre/Road-To-Effective-Snapshot-Testing) to see how to snapshot test any view using the Activity context.


**Known issue**: As of `AndroidUiTestingUtils:1.0.0-SNAPSHOT`, pseudolocales (i.e. en_XA and ar_XB) do not work with LocaleTestRule. This will be fixed in the next version

## Usage
1. To use `ActivityScenarioConfigurator` you need to add the following activities to your `debug/manifest`
```xml
<application
   ...
   <activity android:name="sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator$SnapshotConfiguredActivity"/>
   ...
</application>
```

2. To use `LocaleTestRule`, add this permission in your `debug.manifest`:
```xml
<uses-permission android:name="android.permission.CHANGE_CONFIGURATION"
        tools:ignore="ProtectedPermissions"/>
```
### Code examples
#### Screenshot test examples with [Shot from pedrovgs](https://github.com/pedrovgs/Shot)
1. ActivityScenario
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

2. Jetpack Compose + ActivityScenario</br>
In order to test **Jetpack Compose** views with `ActivityScenarioConfigurator`, use `createEmptyComposeRule()`, for instance:
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

    compareScreenshot(composeTestRule, name = testName)
}
```

#### TestRule example

```kotlin

@get:Rule
val localeTestRule = LocaleTestRule(locale)

@get:Rule
val fontSizeTestRule = FontScaleRules.fontScaleTestRule(fontScale)

@Test
fun yourTest() {
   // here your UI or snapshot Test
   ...
}

```

