package sergio.sastre.uitesting.utils

import androidx.compose.runtime.Composable
import org.junit.rules.TestRule

/**
 * TestRule that must be define for all screenshot testing libraries we want to support
 * library-agnostic screenshot tests
 */
interface ScreenshotTestRule : TestRule {
    fun snapshot(composable: @Composable () -> Unit)
    fun snapshot(name: String? = null, composable: @Composable () -> Unit)
    fun configure(config: LibraryConfig): ScreenshotTestRule
}
