package sergio.sastre.uitesting.paparazzi.config

import app.cash.paparazzi.Paparazzi
import sergio.sastre.uitesting.mapper.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView

class PaparazziForViewTestRuleBuilder {

    private var paparazziConfig: PaparazziConfig = PaparazziConfig()
    private var screenshotConfigForView: ScreenshotConfigForView = ScreenshotConfigForView()

    fun applyPaparazziConfig(
        paparazziConfig: PaparazziConfig,
    ): PaparazziForViewTestRuleBuilder = apply {
        this.paparazziConfig = paparazziConfig
    }

    fun applyScreenshotConfig(
        screenshotConfigForView: ScreenshotConfigForView,
    ): PaparazziForViewTestRuleBuilder = apply {
        this.screenshotConfigForView = screenshotConfigForView
    }

    fun build(): Paparazzi {
        val configAdapter = PaparazziScreenshotConfigAdapter(paparazziConfig)
        val sharedTestAdapter = PaparazziWrapperConfigAdapter(paparazziConfig)

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
            renderExtensions = sharedTestAdapter.asRenderExtensions(),
        )
    }
}