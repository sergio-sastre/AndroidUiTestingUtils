package sergio.sastre.uitesting.utils.crosslibrary.testrules

import androidx.compose.runtime.Composable
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfig
import java.util.*

abstract class SharedScreenshotTestRule(
    override val config: ScreenshotConfig,
) : ScreenshotTestRule(config) {

    companion object ScreenshotTestRuleClassPath {
        const val PAPARAZZI = "sergio.sastre.uitesting.paparazzi.PaparazziScreenshotTestRule";
        const val SHOT = "sergio.sastre.uitesting.shot.ShotScreenshotTestRule";
        const val DROPSHOTS = "sergio.sastre.uitesting.dropshots.DropshotsScreenshotTestRule";
        const val ROBORAZZI = "sergio.sastre.uitesting.roborazzi.RoborazziScreenshotTestRule";
    }

    val dropshotsScreenshotTestRule
        get() = getScreenshotTestRuleClassForName(DROPSHOTS, config)

    val paparazziScreenshotTestRule
        get() = getScreenshotTestRuleClassForName(PAPARAZZI, config)

    val roborazziScreenshotTestRule
        get() = getScreenshotTestRuleClassForName(ROBORAZZI, config)

    val shotScreenshotTestRule
        get() = getScreenshotTestRuleClassForName(SHOT, config)

    fun getScreenshotTestRuleClassForName(
        className: String,
        config: ScreenshotConfig,
    ): ScreenshotTestRule {
        return Class.forName(className)
            .getConstructor(ScreenshotConfig::class.java)
            .newInstance(config) as ScreenshotTestRule
    }

    private val factory: ScreenshotTestRule by lazy {
        if (isRunningOnJvm()) {
            getJvmScreenshotTestRule(config)
        } else {
            getInstrumentedScreenshotTestRule(config)
        }
    }

    abstract fun getJvmScreenshotTestRule(config: ScreenshotConfig): ScreenshotTestRule

    abstract fun getInstrumentedScreenshotTestRule(config: ScreenshotConfig): ScreenshotTestRule

    private fun isRunningOnJvm(): Boolean =
        System.getProperty("java.runtime.name")
            ?.lowercase(Locale.getDefault())
            ?.contains("android") != true

    override fun apply(base: Statement?, description: Description?): Statement {
        return factory.apply(base, description)
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRule {
        return factory.configure(config)
    }

    override fun snapshot(composable: @Composable () -> Unit) {
        factory.snapshot { composable() }
    }

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        factory.snapshot(name) { composable() }
    }
}
