package sergio.sastre.uitesting.utils.crosslibrary.testrules.providers

import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable

interface ScreenshotLibraryTestRuleForComposableProvider {

    companion object ScreenshotTestRuleClassPath {
        const val PAPARAZZI = "sergio.sastre.uitesting.paparazzi.PaparazziScreenshotTestRuleForComposable";
        const val SHOT = "sergio.sastre.uitesting.shot.ShotScreenshotTestRuleForComposable";
        const val DROPSHOTS = "sergio.sastre.uitesting.dropshots.DropshotsScreenshotTestRuleForComposable";
        const val ANDROID_TESTIFY = "sergio.sastre.uitesting.android_testify.AndroidTestifyScreenshotTestRuleForComposable";
        const val ROBORAZZI = "sergio.sastre.uitesting.roborazzi.RoborazziScreenshotTestRuleForComposable";
    }

    val config: ScreenshotConfigForComposable

    val dropshotsScreenshotTestRule
        get() = getScreenshotTestRuleClassForName(DROPSHOTS, config)

    val paparazziScreenshotTestRule
        get() = getScreenshotTestRuleClassForName(PAPARAZZI, config)

    val roborazziScreenshotTestRule
        get() = getScreenshotTestRuleClassForName(ROBORAZZI, config)

    val shotScreenshotTestRule
        get() = getScreenshotTestRuleClassForName(SHOT, config)

    val androidTestifyScreenshotTestRule
        get() = getScreenshotTestRuleClassForName(ANDROID_TESTIFY, config)

    fun getScreenshotTestRuleClassForName(
        className: String,
        config: ScreenshotConfigForComposable,
    ): ScreenshotTestRuleForComposable {
        return Class.forName(className)
            .getConstructor(ScreenshotConfigForComposable::class.java)
            .newInstance(config) as ScreenshotTestRuleForComposable
    }
}
