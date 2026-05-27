package sergio.sastre.uitesting.utils.testrules.accessibility.colorcontrast

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand
import java.io.IOException

/**
 * A [TestWatcher] that allows to set color contrast during a test.
 * It ALWAYS sets color contrast to the initial value at the end of the test.
 *
 * WARNING: Only works on Instrumentation tests, not on Robolectric tests.
 */
class ColorContrastTestWatcher(
    private val colorContrast: ColorContrast
) : TestWatcher() {

    private var initialColorContrast: ColorContrast? = null

    private fun getCurrentActivity(): Activity? {
        var activity: Activity? = null
        getInstrumentation().runOnMainSync {
            activity = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
                .firstOrNull()
        }
        return activity
    }

    override fun starting(description: Description?) {
        super.starting(description)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            try {
                if (initialColorContrast == null) {
                    initialColorContrast = getColorContrast()
                }
                apply()
            } catch (e: Exception) {
                Log.e(ColorContrastTestWatcher::class.java.simpleName, "Error setting color contrast", e)
            }
        }
    }

    private fun apply() {
        val activity = getCurrentActivity()
        if (activity != null) {
            ColorContrastHelper(activity).applyColorContrast(colorContrast)
        } else {
            try {
                setColorContrast(colorContrast)
            } catch (e: IOException) {
                Log.e(ColorContrastTestWatcher::class.java.simpleName, "Error setting color contrast", e)
            }
        }
    }

    override fun finished(description: Description?) {
        super.finished(description)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            initialColorContrast?.run {
                try {
                    setColorContrast(this)
                } catch (e: IOException) {
                    Log.e(ColorContrastTestWatcher::class.java.simpleName, "Error restoring color contrast", e)
                }
            }
        }
        initialColorContrast = null
    }

    @Throws(IOException::class)
    private fun getColorContrast(): ColorContrast {
        val contrastAsString = getInstrumentation().waitForExecuteShellCommand("settings get secure contrast_level")
        val contrastValue = contrastAsString.substringAfterLast("=").trim().toFloatOrNull()
        return ColorContrast.entries.firstOrNull { it.value == contrastValue } ?: ColorContrast.DEFAULT
    }

    @Throws(IOException::class)
    private fun setColorContrast(colorContrast: ColorContrast) {
        getInstrumentation().waitForExecuteShellCommand("settings put secure contrast_level ${colorContrast.value}")
    }
}
