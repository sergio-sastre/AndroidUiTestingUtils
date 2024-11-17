package sergio.sastre.uitesting.paparazzi.config

import app.cash.paparazzi.Paparazzi
import sergio.sastre.uitesting.mapper.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable

class PaparazziForComposableTestRuleBuilder {

    private var paparazziConfig: PaparazziConfig = PaparazziConfig()
    private var screenshotConfigForComposable: ScreenshotConfigForComposable =
        ScreenshotConfigForComposable()

    fun applyPaparazziConfig(
        paparazziConfig: PaparazziConfig,
    ): PaparazziForComposableTestRuleBuilder = apply {
        this.paparazziConfig = paparazziConfig
    }

    fun applyScreenshotConfig(
        screenshotConfigForComposable: ScreenshotConfigForComposable,
    ): PaparazziForComposableTestRuleBuilder = apply {
        this.screenshotConfigForComposable = screenshotConfigForComposable
    }

    fun build(): Paparazzi {
        val configAdapter = PaparazziScreenshotConfigAdapter(paparazziConfig)
        val sharedTestAdapter = PaparazziWrapperConfigAdapter(paparazziConfig)

        return Paparazzi(
            deviceConfig = configAdapter.getDeviceConfigFor(screenshotConfigForComposable).copy(
                softButtons = paparazziConfig.deviceSystemUiVisibility.softButtons,
            ),
            showSystemUi = paparazziConfig.deviceSystemUiVisibility.systemUi,
            supportsRtl = true,
            renderingMode = sharedTestAdapter.asRenderingMode(),
            maxPercentDifference = paparazziConfig.maxPercentageDiff,
            environment = sharedTestAdapter.asEnvironment(),
            renderExtensions = sharedTestAdapter.asRenderExtensions(),
            useDeviceResolution = paparazziConfig.useDeviceResolution,
            validateAccessibility = paparazziConfig.validateAccessibility,
        )
    }
}