package sergio.sastre.uitesting.utils.testrules.accessibility

import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.FontWeight
import sergio.sastre.uitesting.utils.testrules.Condition
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand

/**
 * Allows to set the font weight during a test.
 * @param weight The font weight to be set.
 *
 * WARNING: Only works on Instrumentation tests, not on Robolectric tests.
 */
class FontWeightTestRule(
    private val weight: FontWeight
) : TestRule {

    companion object {
        private val TAG = FontWeightTestRule::class.java.simpleName

        const val SLEEP_TO_WAIT_FOR_SETTING_MILLIS = 100
        const val MAX_RETRIES_TO_WAIT_FOR_SETTING = 100
    }

    private val timeOutInMillis =
        MAX_RETRIES_TO_WAIT_FOR_SETTING * SLEEP_TO_WAIT_FOR_SETTING_MILLIS

    override fun apply(base: Statement, description: Description): Statement {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            FontWeightStatement(
                baseStatement = base,
                description = description,
                weight = weight.value,
                timeOutInMillis = timeOutInMillis
            )
        } else {
            object : Statement() {
                override fun evaluate() {
                    Log.d(TAG, "Skipping ${this.javaClass.simpleName}. It can only be used on API 31+, and the current API is ${Build.VERSION.SDK_INT}")
                    base.evaluate()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private class FontWeightStatement(
        private val baseStatement: Statement,
        private val description: Description,
        private val weight: Int,
        private val timeOutInMillis: Int,
    ) : Statement() {

        private val fontWeightAdjustment
            get() = getInstrumentation().targetContext.resources.configuration.fontWeightAdjustment

        @Throws(Throwable::class)
        override fun evaluate() {
            val initialWeight = fontWeightAdjustment
            try {
                getInstrumentation().waitForExecuteShellCommand(
                    "settings put secure font_weight_adjustment $weight"
                )
                sleepUntil(
                    condition = weightMatches(weight),
                    expectedFontWeight = weight
                )
                baseStatement.evaluate()
            } catch (throwable: Throwable) {
                val testName = "${description.testClass.simpleName}\$${description.methodName}"
                val errorMessage =
                    "Test $testName failed on setting FontWeight to $weight"
                Log.e(TAG, errorMessage)
                throw throwable
            } finally {
                getInstrumentation().waitForExecuteShellCommand(
                    "settings put secure font_weight_adjustment $initialWeight"
                )
                sleepUntil(
                    condition = weightMatches(initialWeight),
                    expectedFontWeight = initialWeight
                )
            }
        }

        private fun weightMatches(fontWeight: Int): Condition {
            return object : Condition {
                override fun holds(): Boolean =
                    fontWeightAdjustment == fontWeight
            }
        }

        @Synchronized
        private fun sleepUntil(condition: Condition, expectedFontWeight: Int) {
            var iterations = 0
            var retries = 0
            val retriesCount = timeOutInMillis / SLEEP_TO_WAIT_FOR_SETTING_MILLIS
            while (!condition.holds()) {
                iterations++
                SystemClock.sleep(SLEEP_TO_WAIT_FOR_SETTING_MILLIS.toLong())
                val mustRetry = iterations % 10 == 0
                if (mustRetry) {
                    retries++
                    getInstrumentation().waitForExecuteShellCommand(
                        "settings put secure font_weight_adjustment $expectedFontWeight"
                    )
                    Log.d(
                        TAG,
                        "trying to set font weight to ${expectedFontWeight}, currently ${fontWeightAdjustment}, $retries retries"
                    )
                }
                if (iterations == retriesCount) {
                    throw AssertionError(
                        "Spent too long waiting trying to set font weight: $timeOutInMillis milliseconds"
                    )
                }
            }
        }
    }
}