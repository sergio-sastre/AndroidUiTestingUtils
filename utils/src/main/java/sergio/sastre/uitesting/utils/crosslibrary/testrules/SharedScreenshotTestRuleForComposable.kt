package sergio.sastre.uitesting.utils.crosslibrary.testrules

import androidx.compose.runtime.Composable
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import java.util.*

abstract class SharedScreenshotTestRuleForComposable(
    override val config: ScreenshotConfigForComposable,
) : ScreenshotTestRuleForComposable(config) {

    companion object ScreenshotTestRuleClassPath {
        const val PAPARAZZI = "sergio.sastre.uitesting.paparazzi.PaparazziScreenshotTestRuleForComposable";
        const val SHOT = "sergio.sastre.uitesting.shot.ShotScreenshotTestRuleForComposable";
        const val DROPSHOTS = "sergio.sastre.uitesting.dropshots.DropshotsScreenshotTestRuleForComposable";
        const val ROBORAZZI = "sergio.sastre.uitesting.roborazzi.RoborazziScreenshotTestRuleForComposable";
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
        config: ScreenshotConfigForComposable,
    ): ScreenshotTestRuleForComposable {
        return Class.forName(className)
            .getConstructor(ScreenshotConfigForComposable::class.java)
            .newInstance(config) as ScreenshotTestRuleForComposable
    }

    private val factory: ScreenshotTestRuleForComposable by lazy {
        if (isRunningOnJvm()) {
            getJvmScreenshotTestRule(config)
        } else {
            getInstrumentedScreenshotTestRule(config)
        }
    }

    abstract fun getJvmScreenshotTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRuleForComposable

    abstract fun getInstrumentedScreenshotTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRuleForComposable

    private fun isRunningOnJvm(): Boolean =
        System.getProperty("java.runtime.name")
            ?.lowercase(Locale.getDefault())
            ?.contains("android") != true

    override fun apply(base: Statement?, description: Description?): Statement {
        return factory.apply(base, description)
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForComposable {
        return factory.configure(config)
    }

    override fun snapshot(composable: @Composable () -> Unit) {
        factory.snapshot { composable() }
    }

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        factory.snapshot(name) { composable() }
    }
}
