# Android-Testify usage

## Android-Testify 3.x.x

Since Android-Testify 3.0.0, it supports _ActivityScenarios_! That means, you could use AndroidUiTestingUtils _ActivityScenarios_ for Views, Composable, etc. together with Android-Testify 3.x.x.&#x20;

Check some examples in [this repo](https://github.com/sergio-sastre/Android-screenshot-testing-playground).

## Android-Testify 2.x.x

Android-Testify 2.x.x does not support _ActivityScenario_ but the deprecated _ActivityTestRule._ So, in order to generate screenshot under different configurations, you'd need to resort to the TestRules described in [this section](../testrules-and-other-utils.md#testrules).\
\
Nevertheless, _AndroidUiTestingUtils_ provides optimized ScreenshotTestRules to leverage Android-Testify tests for **Fragments**, **Android Views** and **Jetpack Compose**. \
These are used in the upcoming examples.\
\
In order to access them, add the following dependencies

```groovy
// Available from version 2.1.0+
androidTestImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:utils:<version>'
androidTestImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:android-testify:<version>'
```

## Activity

Use _AndroidUiTesting_ [TestRules ](../testrules-and-other-utils.md#testrules)with the standard Android-Testify ScreenshotRule

## Fragment

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
        .waitForIdleSync()
        .assertSame(name = "nameOfMyScreenshot")
}
```

## Android View

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
        .waitForIdleSync()
        .assertSame(name = "nameOfMyScreenshot")
}
```

## Jetpack Compose

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
