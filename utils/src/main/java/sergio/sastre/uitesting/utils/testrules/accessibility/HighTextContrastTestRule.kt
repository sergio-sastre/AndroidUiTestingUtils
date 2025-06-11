package sergio.sastre.uitesting.utils.testrules.accessibility

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand
import java.io.IOException

/**
 * Allows to enable/disable high text contrast accessibility during a test.
 *
 * WARNING: Only works on Instrumentation tests, not on Robolectric tests.
 */
class HighTextContrastTestRule: TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    enableHighTextContrast(true)
                    base.evaluate()
                } finally {
                    enableHighTextContrast(false)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun enableHighTextContrast(enable: Boolean = true) {
        val highTextContrastEnabledValue = when (enable) {
            true -> 1
            false -> 0
        }
        getInstrumentation().waitForExecuteShellCommand("settings put secure high_text_contrast_enabled $highTextContrastEnabledValue")
    }
}
