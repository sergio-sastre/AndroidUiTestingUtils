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
                softButtons = paparazziConfig.deviceSystemUiVisibility.softButtons,
            ),
            showSystemUi =  paparazziConfig.deviceSystemUiVisibility.systemUi,
            supportsRtl = true,
            renderingMode = sharedTestAdapter.asRenderingMode(),
            maxPercentDifference = paparazziConfig.maxPercentageDiff,
            environment = sharedTestAdapter.asEnvironment(),
        )

    fun generatePaparazziTestRule(
        screenshotConfigForView: ScreenshotConfigForView,
    ): Paparazzi {
        return Paparazzi(
            deviceConfig = configAdapter.getDeviceConfigFor(screenshotConfigForView).copy(
                softButtons = paparazziConfig.deviceSystemUiVisibility.softButtons,
            ),
            showSystemUi =  paparazziConfig.deviceSystemUiVisibility.systemUi,
            supportsRtl = true,
            theme = screenshotConfigForView.theme ?: "android:Theme.Material.NoActionBar.Fullscreen",
            renderingMode = sharedTestAdapter.asRenderingMode(),
            maxPercentDifference = paparazziConfig.maxPercentageDiff,
            environment = sharedTestAdapter.asEnvironment(),
        )
    }
}
