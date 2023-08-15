package sergio.sastre.uitesting.utils.crosslibrary.testrules.implementations

import androidx.compose.runtime.Composable
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable
import sergio.sastre.uitesting.utils.crosslibrary.testrules.providers.ScreenshotLibraryTestRuleForComposableProvider

abstract class ScreenshotLibraryTestRuleForComposable(
    override val config: ScreenshotConfigForComposable,
) : ScreenshotTestRuleForComposable(config), ScreenshotLibraryTestRuleForComposableProvider {

    private val factory: ScreenshotTestRuleForComposable by lazy {
        getScreenshotLibraryTestRule(config)
    }

    abstract fun getScreenshotLibraryTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRuleForComposable

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
