package sergio.sastre.uitesting.paparazzi.config

import app.cash.paparazzi.Paparazzi
import sergio.sastre.uitesting.sharedtest.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView

internal class PaparazziTestRuleGenerator(
    private val paparazziConfig: PaparazziConfig,
) {
    private val configAdapter = PaparazziScreenshotConfigAdapter(paparazziConfig)

    private val sharedTestAdapter = PaparazziSharedTestAdapter(paparazziConfig)

    fun generatePaparazziTestRule(
        screenshotConfigForComposable: ScreenshotConfigForComposable,
    ): Paparazzi =
        Paparazzi(
            deviceConfig = configAdapter.getDeviceConfigFor(screenshotConfigForComposable).copy(
                softButtons = paparazziConfig.softButtons,
            ),
            renderingMode = sharedTestAdapter.asRenderingMode(),
            maxPercentDifference = paparazziConfig.maxPercentageDiff,
            environment = sharedTestAdapter.asEnvironment(),
        )

    fun generatePaparazziTestRule(
        screenshotConfigForView: ScreenshotConfigForView,
    ): Paparazzi {
        // TODO
        //val theme = screenshotConfigForView.theme ?: "android:Theme.Material.NoActionBar.Fullscreen"
        return Paparazzi(
            deviceConfig = configAdapter.getDeviceConfigFor(screenshotConfigForView).copy(
                softButtons = paparazziConfig.softButtons,
            ),
            //theme = ,
            renderingMode = sharedTestAdapter.asRenderingMode(),
            maxPercentDifference = paparazziConfig.maxPercentageDiff,
            environment = sharedTestAdapter.asEnvironment(),
        )
    }

}
