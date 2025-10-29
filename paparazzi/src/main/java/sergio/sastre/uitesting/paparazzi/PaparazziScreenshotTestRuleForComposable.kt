package sergio.sastre.uitesting.paparazzi

import app.cash.paparazzi.Paparazzi
import org.junit.runner.Description
import org.junit.runners.model.Statement
import androidx.compose.runtime.Composable
import sergio.sastre.uitesting.paparazzi.config.PaparazziForComposableTestRuleBuilder
import sergio.sastre.uitesting.mapper.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable

class PaparazziScreenshotTestRuleForComposable(
    override val config: ScreenshotConfigForComposable = ScreenshotConfigForComposable(),
) : ScreenshotTestRuleForComposable(config) {

    private var paparazziConfig = PaparazziConfig()

    private val paparazziTestRule: Paparazzi by lazy {
        PaparazziForComposableTestRuleBuilder()
            .applyPaparazziConfig(paparazziConfig)
            .applyScreenshotConfig(config)
            .build()
    }

    override fun apply(base: Statement, description: Description): Statement =
        paparazziTestRule.apply(base, description)

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        paparazziTestRule.context.run {
            setFontWeight(config.fontWeight)
            setDisplaySize(config.displaySize)
        }
        paparazziTestRule.snapshot(name) { composable() }
    }

    override fun snapshot(composable: @Composable () -> Unit) {
        paparazziTestRule.context.run {
            setFontWeight(config.fontWeight)
            setDisplaySize(config.displaySize)
        }
        paparazziTestRule.snapshot { composable() }
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForComposable = apply {
        if (config is PaparazziConfig) {
            paparazziConfig = config
        }
    }
}
