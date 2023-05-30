package sergio.sastre.uitesting.paparazzi

import android.content.Context
import android.content.res.Configuration
import app.cash.paparazzi.Paparazzi
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfig
import androidx.compose.runtime.Composable
import sergio.sastre.uitesting.paparazzi.config.PaparazziTestRuleGenerator
import sergio.sastre.uitesting.sharedtest.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRule

class PaparazziScreenshotTestRule(
    override val config: ScreenshotConfig = ScreenshotConfig(),
) : ScreenshotTestRule(config) {

    private var paparazziConfig = PaparazziConfig()

    private val paparazziTestRule: Paparazzi by lazy {
        PaparazziTestRuleGenerator(config, paparazziConfig).generatePaparazziTestRule()
    }

    @Suppress("DEPRECATION")
    private fun Context.setDisplaySize(displaySize: DisplaySize) =
        this.apply {
            val density = resources.configuration.densityDpi
            val config = Configuration(resources.configuration)
            config.densityDpi = (density * displaySize.value.toFloat()).toInt()
            resources.updateConfiguration(config, resources.displayMetrics)
        }

    override fun apply(base: Statement, description: Description): Statement =
        paparazziTestRule.apply(base, description)

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        paparazziTestRule.context.setDisplaySize(config.displaySize)
        paparazziTestRule.snapshot(name) { composable() }
    }

    override fun snapshot(composable: @Composable () -> Unit) {
        paparazziTestRule.context.setDisplaySize(config.displaySize)
        paparazziTestRule.snapshot { composable() }
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRule = apply {
        if (config is PaparazziConfig) {
            paparazziConfig = config
        }
    }
}
