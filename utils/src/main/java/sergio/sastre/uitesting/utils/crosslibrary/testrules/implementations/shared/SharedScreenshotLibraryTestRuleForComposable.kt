package sergio.sastre.uitesting.utils.crosslibrary.testrules.implementations.shared

import androidx.compose.runtime.Composable
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable
import sergio.sastre.uitesting.utils.crosslibrary.testrules.providers.ScreenshotLibraryTestRuleForComposableProvider
import java.util.*

abstract class SharedScreenshotLibraryTestRuleForComposable(
    override val config: ScreenshotConfigForComposable,
) : ScreenshotTestRuleForComposable(config), ScreenshotLibraryTestRuleForComposableProvider {

    private val factory: ScreenshotTestRuleForComposable by lazy {
        if (isRunningOnJvm()) {
            getJvmScreenshotLibraryTestRule(config)
        } else {
            getInstrumentedScreenshotLibraryTestRule(config)
        }
    }

    abstract fun getJvmScreenshotLibraryTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRuleForComposable

    abstract fun getInstrumentedScreenshotLibraryTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRuleForComposable

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
