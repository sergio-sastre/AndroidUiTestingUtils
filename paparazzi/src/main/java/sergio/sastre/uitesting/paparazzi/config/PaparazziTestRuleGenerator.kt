package sergio.sastre.uitesting.paparazzi.config

import app.cash.paparazzi.Paparazzi
import sergio.sastre.uitesting.sharedtest.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfig

internal class PaparazziTestRuleGenerator(
    private val screenshotConfig: ScreenshotConfig,
    private val paparazziConfig: PaparazziConfig,
) {
    private val configAdapter = PaparazziScreenshotConfigAdapter(paparazziConfig)

    private val sharedTestAdapter = PaparazziSharedTestAdapter(paparazziConfig)

    fun generatePaparazziTestRule(): Paparazzi =
        Paparazzi(
            deviceConfig = configAdapter.getDeviceConfigFor(screenshotConfig).copy(
                softButtons = paparazziConfig.softButtons,
            ),
            renderingMode = sharedTestAdapter.asRenderingMode(),
            maxPercentDifference = paparazziConfig.maxPercentageDiff,
            environment = sharedTestAdapter.asEnvironment(),
        )
}
