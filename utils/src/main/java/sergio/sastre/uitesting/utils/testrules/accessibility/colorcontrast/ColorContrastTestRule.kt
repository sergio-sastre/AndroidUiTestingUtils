package sergio.sastre.uitesting.utils.testrules.accessibility.colorcontrast

import android.os.Build
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand
import java.io.IOException

/**
 * Allows to set color contrast during a test.
 * It ALWAYS sets color contrast to DEFAULT at the end of the test.
 *
 * WARNING: Only works on Instrumentation tests, not on Robolectric tests.
 */
class ColorContrastTestRule(private val colorContrast: ColorContrast): TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            true -> getSetColorContrastStatement(base, colorContrast)
            false -> skipSetColorContrastStatement(base)
        }
    }

    private fun getSetColorContrastStatement(
        base: Statement,
        colorContrast: ColorContrast
    ): Statement =
        object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    setColorContrast(colorContrast)
                    base.evaluate()
                } finally {
                    setColorContrast(ColorContrast.DEFAULT)
                }
            }
        }

    private fun skipSetColorContrastStatement(base:Statement): Statement =
        object : Statement() {
            override fun evaluate() {
                Log.d(
                    ColorContrastTestRule::class.java.simpleName,
                    "Skipping ${this.javaClass.simpleName}. It can only be used on API ${Build.VERSION_CODES.VANILLA_ICE_CREAM}+, and the current API is ${Build.VERSION.SDK_INT}")
                base.evaluate()
            }
        }

    @Throws(IOException::class)
    private fun setColorContrast(colorContrast: ColorContrast) {
        getInstrumentation().waitForExecuteShellCommand("shell settings put secure contrast_level ${colorContrast.value}")
    }
}
