package sergio.sastre.uitesting.utils.crosslibrary.testrules.providers

import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView

interface ScreenshotLibraryTestRuleForViewProvider {

    companion object ScreenshotTestRuleClassPath {
        const val PAPARAZZI = "sergio.sastre.uitesting.paparazzi.PaparazziScreenshotTestRuleForView"
        const val SHOT = "sergio.sastre.uitesting.shot.ShotScreenshotTestRuleForView"
        const val ANDROID_TESTIFY = "sergio.sastre.uitesting.android_testify.screenshotscenario.ScreenshotScenarioRuleForView"
        const val DROPSHOTS = "sergio.sastre.uitesting.dropshots.DropshotsScreenshotTestRuleForView"
        const val ROBORAZZI = "sergio.sastre.uitesting.roborazzi.RoborazziScreenshotTestRuleForView"
    }

    val config: ScreenshotConfigForView

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
        config: ScreenshotConfigForView,
    ): ScreenshotTestRuleForView {
        return Class.forName(className)
            .getConstructor(ScreenshotConfigForView::class.java)
            .newInstance(config) as ScreenshotTestRuleForView
    }
}
