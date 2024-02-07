package sergio.sastre.uitesting.utils.testrules.fontsize

import android.os.SystemClock
import android.util.Log

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.FontSizeScale
import sergio.sastre.uitesting.utils.testrules.Condition
import sergio.sastre.uitesting.utils.testrules.fontsize.FontSizeTestRule.FontScaleStatement.Companion.MAX_RETRIES_TO_WAIT_FOR_SETTING
import sergio.sastre.uitesting.utils.testrules.fontsize.FontSizeTestRule.FontScaleStatement.Companion.SLEEP_TO_WAIT_FOR_SETTING_MILLIS

/**
 * A TestRule to change FontSize of the device/emulator. It is done via adb from API 24+
 *
 * Strongly based on code from espresso-support library, from Novoda
 * https://github.com/novoda/espresso-support/tree/master/core/src/main/java/com/novoda/espresso
 *
 * WARNING: It's not compatible with Robolectric
 */
class FontSizeTestRule(
    private val fontSize: FontSizeScale
) : TestRule {

    companion object {
        private val TAG = FontSizeTestRule::class.java.simpleName

        fun smallFontScaleTestRule(): FontSizeTestRule = FontSizeTestRule(FontSize.SMALL)

        fun normalFontScaleTestRule(): FontSizeTestRule = FontSizeTestRule(FontSize.NORMAL)

        fun largeFontScaleTestRule(): FontSizeTestRule = FontSizeTestRule(FontSize.LARGE)

        fun hugeFontScaleTestRule(): FontSizeTestRule = FontSizeTestRule(FontSize.HUGE)
    }

    private var timeOutInMillis = MAX_RETRIES_TO_WAIT_FOR_SETTING * SLEEP_TO_WAIT_FOR_SETTING_MILLIS

    private val fontScaleSetting: FontScaleSetting = FontScaleSetting()

    override fun apply(base: Statement, description: Description): Statement {
        return FontScaleStatement(base, description, fontScaleSetting, fontSize, timeOutInMillis)
    }

    private class FontScaleStatement(
        private val baseStatement: Statement,
        private val description: Description,
        private val scaleSetting: FontScaleSetting,
        private val scale: FontSizeScale,
        private val timeOutInMillis: Int,
    ) : Statement() {

        @Throws(Throwable::class)
        override fun evaluate() {
            val initialScale = scaleSetting.get()
            try {
                scaleSetting.set(scale)
                sleepUntil(scaleMatches(scale), scale)

                baseStatement.evaluate()
            } catch (throwable: Throwable) {
                val testName = "${description.testClass.simpleName}\$${description.methodName}"
                val errorMessage =
                    "Test $testName failed on setting FontSize to ${scale.scale}"
                Log.e(TAG, errorMessage)
                throw throwable
            } finally {
                scaleSetting.set(initialScale)
                sleepUntil(scaleMatches(initialScale), initialScale)
            }
        }

        private fun scaleMatches(scale: FontSizeScale): Condition {
            return object : Condition {
                override fun holds(): Boolean {
                    return scaleSetting.get() === scale
                }
            }
        }

        @Synchronized
        private fun sleepUntil(condition: Condition, expectedFontSize: FontSizeScale) {
            var iterations = 0
            var retries = 0
            while (!condition.holds()) {
                iterations++
                val retriesCount = timeOutInMillis / SLEEP_TO_WAIT_FOR_SETTING_MILLIS
                SystemClock.sleep(SLEEP_TO_WAIT_FOR_SETTING_MILLIS.toLong())
                val mustRetry = iterations % 10 == 0
                if (mustRetry) {
                    retries++
                    scaleSetting.set(expectedFontSize)
                    Log.d(TAG, "trying to set font size scale to ${expectedFontSize.scale}, $retries retry")
                }
                if (iterations == retriesCount) {
                    throw timeoutError()
                }
            }
        }

        private fun timeoutError(): AssertionError {
            return AssertionError("Spent too long waiting trying to set font scale: $timeOutInMillis milliseconds")
        }

        companion object {
            const val SLEEP_TO_WAIT_FOR_SETTING_MILLIS = 100
            const val MAX_RETRIES_TO_WAIT_FOR_SETTING = 100
        }
    }
}