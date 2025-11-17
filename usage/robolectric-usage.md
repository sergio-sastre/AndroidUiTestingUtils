# Robolectric usage

_AndroidUiTesting_ includes some special `ActivityScenarioConfigurators` and `FragmentScenarioConfigurators` that are additionally **safe-thread**, what allows to run unit tests in parallel without unexpected behaviours.

Check out some examples below. It uses [Roborazzi](https://github.com/takahirom/roborazzi) as screenshot testing library.

## Activity

Here is with Junit4 test rule

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapActivityTest {

    @get:Rule
    val robolectricScreenshotRule =
        robolectricActivityScenarioForActivityRule(
            config = ActivityConfigItem(
                systemLocale = "en",
                uiMode = UiMode.NIGHT,
                theme = R.style.Custom_Theme,
                orientation = Orientation.PORTRAIT,
                fontSize = FontSize.NORMAL,
                displaySize = DisplaySize.NORMAL,
                fontWeight = FontWeight.BOLD,
            ),
            deviceScreen = DeviceScreen.Phone.PIXEL_4A,
        )

    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen` in the Rule
    @Test
    fun snapActivity() {
        robolectricScreenshotRule
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
                .setFontWeight(FontWeight.BOLD)
                .launch(MyActivity::class.java)

        activityScenario
            .rootView
            .captureRoboImage("path/MyActivity.png")

        activityScenario.close()
    }
}
```

## Fragment

Here is with Junit4 test rule

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapFragmentTest {

    @get:Rule
    val robolectricScreenshotRule =
        robolectricFragmentScenarioConfiguratorRule<MyFragment>(
            fragmentArgs = bundleOf("arg_key" to "arg_value"),
            config = FragmentConfigItem(
                locale = "en_XA",
                uiMode = UiMode.NIGHT,
                theme = R.style.Custom_Theme,
                orientation = Orientation.PORTRAIT,
                fontSize = FontSize.NORMAL,
                displaySize = DisplaySize.NORMAL,
                fontWeight = FontWeight.BOLD,
            ),
            deviceScreen = DeviceScreen.Phone.PIXEL_4A,
        )

    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen` in the Rule
    @Test
    fun snapFragment() {
        robolectricScreenshotRule
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
                .setFontWeight(FontWeight.BOLD)
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

## Android View

Here is with Junit4 test rule

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapViewHolderTest {

    @get:Rule
    val robolectricScreenshotRule =
        RobolectricActivityScenarioForViewRule(
            config = ViewConfigItem(
                locale = "en_XA",
                uiMode = UiMode.NIGHT,
                theme = R.style.Custom_Theme,
                orientation = Orientation.PORTRAIT,
                fontSize = FontSize.NORMAL,
                displaySize = DisplaySize.NORMAL,
                fontWeight = FontWeight.BOLD,
            ),
            deviceScreen = DeviceScreen.Phone.PIXEL_4A,
            backgroundColor = TRANSPARENT,
        )

    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen` in the Rule
    @Test
    fun snapViewHolder() {
        val activity = robolectricScreenshotRule.activity
        val layout =
            robolectricScreenshotRule.inflateAndWaitForIdle(R.layout.memorise_row)

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
                .setFontWeight(FontWeight.BOLD)
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

## Jetpack Compose

Here is with `RobolectricActivityScenarioForComposableRule` test rule

```kotlin
@RunWith(RobolectricTestRunner::class) // or ParameterizedRobolectricTestRunner for parameterized test
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SnapComposableTest {

    @get:Rule
    val robolectricScreenshotRule =
        RobolectricActivityScenarioForComposableRule(
            config = ComposableConfigItem(
                locale = "ar_XB",
                uiMode = UiMode.DAY,
                orientation = Orientation.PORTRAIT,
                fontSize = FontSize.SMALL,
                displaySize = DisplaySize.LARGE,
                fontWeight = FontWeight.BOLD,
            ),
            deviceScreen = DeviceScreen.Phone.PIXEL_4A,
            backgroundColor = TRANSPARENT,
        )

    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen` in the Rule
    @Test
    fun snapComposable() {
        robolectricScreenshotRule
            .activityScenario
            .onActivity {
                it.setContent {
                    AppTheme { // this theme must use isSystemInDarkTheme() internally
                        yourComposable()
                    }
                }
            }

        robolectricScreenshotRule
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

and then capture image like this

```kotlin
@Test
fun snapComposable() {
    robolectricScreenshotRule
       .captureRoboImage("path/MyComposable.png"){
          AppTheme { // this theme must use isSystemInDarkTheme() internally
             yourComposable()
          }
       }
}
```

You can also use _AndroidUiTestingUtils_ without `RobolectricActivityScenarioForComposableRule` test rule as follows

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
                .setFontWeight(FontWeight.BOLD)
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

## Multiple Devices & Configs combined

_AndroidUiTestingUtils_ also helps generate all parameters of a set of UiStates under a given set of devices and configurations. For that, use the corresponding type depending on what you are testing:

* _Activity_: `TestDataForActivity<MyEnum>`
* _Fragment_: `TestDataForFragment<MyEnum>`
* _Composable_: `TestDataForComposable<MyEnum>`
* _View_ (e.g. Dialogs, ViewHolders): `TestDataForView<MyEnum>`

Here is an example with Views

```kotlin
@RunWith(ParameterizedRobolectricTestRunner::class)
class MultipleDevicesAndConfigsTest(
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
   }

    // Passed config & device to the scenario
    @get:Rule
    val robolectricScreenshotRule = RobolectricActivityScenarioForViewRule(
        config = testItem.config,
        deviceScreen = testItem.device,
    )

    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen`in the rule
    @Test
    fun snapView() {
       val layout = robolectricScreenshotRule.inflateAndWaitForIdle(R.layout.my_view)

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
