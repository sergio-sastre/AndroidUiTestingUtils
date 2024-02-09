# Cross-Library usage

_AndroidUiTestingUtils_ provides out-of-the-box support to write screenshot tests that can run across libraries for **Composables** and **Android Views**

To configure cross-library screenshot tests, you need to follow the steps in [this section](cross-library-usage.md#cross-library-screenshot-tests).

Once done, write your tests like this:

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

{% hint style="warning" %}
You must define a `testInstrumentationRunner` in build.gradle of type `androidx.test.runner.AndroidJUnitRunner` for Parameterized Cross-Library screenshot tests to work. For instance, `com.karumi.shot.ShotTestRunner`if using Shot.
{% endhint %}
