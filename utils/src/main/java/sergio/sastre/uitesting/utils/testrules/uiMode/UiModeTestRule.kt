package sergio.sastre.uitesting.utils.testrules.uiMode

import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand

/**
 * A TestRule to change the UiMode to day or night.
 * It is done via adb from API 29+, so that it also supports the deprecated ActivityTestRule
 * from that version on.
 *
 * It is strongly based on Adevinta/Barista DayNightRule.
 * https://github.com/AdevintaSpain/Barista/tree/master/library%2Fsrc%2Fmain%2Fjava%2Fcom%2Fadevinta%2Fandroid%2Fbarista%2Frule%2Ftheme
 *
 * WARNING: It's not compatible with Robolectric
 */
class UiModeTestRule(private val mode: UiMode) : TestRule {

    companion object {
        private val TAG = UiModeTestRule::class.java.simpleName
    }

    private val systemUiMode
        get() = getInstrumentation().targetContext.resources?.configuration?.uiMode ?: defaultUiMode

    private val defaultUiMode = UiMode.DAY.appCompatDelegateInt

    private var initialUiMode = defaultUiMode

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                initialUiMode = systemUiMode
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        changeUiModeFromApi29(mode)
                    } else {
                        changeUiModeBeforeApi29(mode)
                    }
                    base.evaluate()
                } catch (throwable: Throwable) {
                    val testName = "${description.testClass.simpleName}\$${description.methodName}"
                    val errorMessage =
                        "Test $testName failed on setting uiMode to ${mode.name}"
                    Log.e(TAG, errorMessage)
                    throw throwable
                } finally {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        resetUiModeFromApi29(initialUiMode)
                    } else {
                        resetUiModeBeforeApi29(initialUiMode)
                    }
                }
            }
        }
    }

    private fun changeUiModeBeforeApi29(uiMode: UiMode) {
        Handler(Looper.getMainLooper()).post {
            initialUiMode = AppCompatDelegate.getDefaultNightMode()
            AppCompatDelegate.setDefaultNightMode(uiMode.appCompatDelegateInt)
        }
    }

    private fun resetUiModeBeforeApi29(uiModeInt: Int) {
        Handler(Looper.getMainLooper()).post {
            AppCompatDelegate.setDefaultNightMode(uiModeInt)
        }
    }

    private fun changeUiModeFromApi29(uiMode: UiMode) {
        initialUiMode = systemUiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        val uiModeValue = when (uiMode) {
            UiMode.NIGHT -> "yes"
            UiMode.DAY -> "no"
        }
        getInstrumentation().waitForExecuteShellCommand("cmd uimode night $uiModeValue")
    }

    private fun resetUiModeFromApi29(uiModeInt: Int?){
        val uiModeValue = when (uiModeInt) {
            Configuration.UI_MODE_NIGHT_YES -> "yes"
            Configuration.UI_MODE_NIGHT_NO -> "no"
            else -> "auto"
        }
        getInstrumentation().waitForExecuteShellCommand("cmd uimode night $uiModeValue")
    }
}
