package sergio.sastre.uitesting.utils.crosslibrary.testrules

import androidx.compose.runtime.Composable
import org.junit.rules.TestWatcher
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable

abstract class ScreenshotTestRuleForComposable(
    open val config: ScreenshotConfigForComposable,
): TestWatcher() {
    abstract fun snapshot(composable: @Composable () -> Unit)
    abstract fun snapshot(name: String? = null, composable: @Composable () -> Unit)
    abstract fun configure(config: LibraryConfig): ScreenshotTestRuleForComposable
}
