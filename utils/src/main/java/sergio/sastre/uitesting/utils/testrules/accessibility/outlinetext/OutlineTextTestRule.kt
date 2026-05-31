package sergio.sastre.uitesting.utils.testrules.accessibility.outlinetext

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.testrules.accessibility.outlinetext.OutlineText.ENABLED
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand
import java.io.IOException

/**
 * Allows to enable/disable high text contrast accessibility during a test.
 *
 * WARNING: Only works on Instrumentation tests, not on Robolectric tests.
 */
class OutlineTextTestRule(val outlineText: OutlineText = ENABLED) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                val preTestOutlineText = getCurrentOutlineText()
                try {
                    setOutlineText(outlineText)
                    base.evaluate()
                } finally {
                    setOutlineText(preTestOutlineText)
                }
            }
        }
    }

    private fun getCurrentOutlineText(): OutlineText {
        val outlineText =
            getInstrumentation().waitForExecuteShellCommand("settings get secure high_text_contrast_enabled")
        val outlineTextValue = outlineText.substringAfterLast("=").toInt()
        return OutlineText.entries.find { it.value == outlineTextValue } ?: OutlineText.DISABLED
    }


    @Throws(IOException::class)
    private fun setOutlineText(enabled: OutlineText) {
        getInstrumentation().waitForExecuteShellCommand("settings put secure high_text_contrast_enabled ${enabled.value}")
    }
}