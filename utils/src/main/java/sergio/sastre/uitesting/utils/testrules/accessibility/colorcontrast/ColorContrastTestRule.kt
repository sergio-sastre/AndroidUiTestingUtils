package sergio.sastre.uitesting.utils.testrules.accessibility.colorcontrast

import android.os.Build
import android.util.Log
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.testrules.accessibility.colorcontrast.ColorContrastSetting.getColorContrast
import sergio.sastre.uitesting.utils.testrules.accessibility.colorcontrast.ColorContrastSetting.setColorContrast
import java.io.IOException

/**
 * A [TestWatcher] that allows to set color contrast during a test.
 * It ALWAYS sets color contrast to the initial value at the end of the test.
 *
 * @param renderingOffsetInMillis fills the gap between the moment the Color Contrast takes effect
 * till the UI renders the changes. Increase it if Color Contrast does not take effect with the default value
 *
 * WARNING: Only works on Instrumentation tests, not on Robolectric tests.
 */
class ColorContrastTestRule(
    private val colorContrast: ColorContrast,
    private val renderingOffsetInMillis: Long = 800,
) : TestRule {

    private val TAG = ColorContrastTestRule::class.java.simpleName

    private var initialColorContrast: ColorContrast? = null

    val isColorContrastAvailable =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    override fun apply(base: Statement, description: Description?): Statement? =
        when (isColorContrastAvailable) {
            true -> getSetColorContrastStatement(base, colorContrast)
            false -> skipSetColorContrastStatement(base)
        }

    private fun skipSetColorContrastStatement(base: Statement): Statement =
        object : Statement() {
            override fun evaluate() {
                Log.d(
                    TAG,
                    "Skipping ${this.javaClass.simpleName}. It can only be used on API ${Build.VERSION_CODES.VANILLA_ICE_CREAM}+, and the current API is ${Build.VERSION.SDK_INT}"
                )
                base.evaluate()
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
                    if (initialColorContrast == null) {
                        initialColorContrast = getColorContrast()
                    }
                    ColorContrastHelper(colorContrast, renderingOffsetInMillis)
                        .applyColorContrast()
                    base.evaluate()
                } catch (e: IOException) {
                    Log.e(TAG, "Error setting color contrast", e)
                } finally {
                    initialColorContrast?.run { setColorContrast(this) }
                    initialColorContrast = null
                }
            }
        }
}
