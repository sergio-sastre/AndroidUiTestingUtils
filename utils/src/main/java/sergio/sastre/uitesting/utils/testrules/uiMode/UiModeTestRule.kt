package sergio.sastre.uitesting.utils.testrules.uiMode

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.UiMode

/**
 * A TestRule to change the UiMode to day or night.
 *
 * It is strongly based on Adevinta/Barista DayNightRule.
 * https://github.com/AdevintaSpain/Barista/tree/master/library%2Fsrc%2Fmain%2Fjava%2Fcom%2Fadevinta%2Fandroid%2Fbarista%2Frule%2Ftheme
 */
class UiModeTestRule(private val mode: UiMode) : TestRule {

    companion object {
        private val TAG = UiModeTestRule::class.java.simpleName
    }

    private val defaultUiMode = UiMode.DAY.appCompatDelegateInt

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                var initialUiMode = defaultUiMode
                try {
                    Handler(Looper.getMainLooper()).post {
                        initialUiMode = AppCompatDelegate.getDefaultNightMode()
                        AppCompatDelegate.setDefaultNightMode(mode.appCompatDelegateInt)
                    }
                    base.evaluate()
                } catch (throwable: Throwable) {
                    val testName = "${description.testClass.simpleName}\$${description.methodName}"
                    val errorMessage =
                        "Test $testName failed on setting uiMode to ${mode.name}"
                    Log.e(TAG, errorMessage)
                    throw throwable
                } finally {
                    Handler(Looper.getMainLooper()).post {
                        AppCompatDelegate.setDefaultNightMode(initialUiMode)
                    }
                }
            }
        }
    }
}