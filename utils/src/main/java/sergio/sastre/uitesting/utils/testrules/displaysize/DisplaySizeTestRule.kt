package sergio.sastre.uitesting.utils.testrules.displaysize

import android.os.SystemClock
import android.util.Log
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.junit.rules.TestRule
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.testrules.Condition
import sergio.sastre.uitesting.utils.testrules.displaysize.DisplaySizeTestRule.DisplaySizeStatement.Companion.MAX_RETRIES_TO_WAIT_FOR_SETTING
import sergio.sastre.uitesting.utils.testrules.displaysize.DisplaySizeTestRule.DisplaySizeStatement.Companion.SLEEP_TO_WAIT_FOR_SETTING_MILLIS

/**
 * A TestRule to change Display size of the device/emulator via adb
 *
 * WARNING: It's not compatible with Robolectric
 */
class DisplaySizeTestRule(
    private val displaySize: DisplaySize,
) : TestRule {

    companion object {
        private val TAG = DisplaySizeTestRule::class.java.simpleName

        fun smallDisplaySizeTestRule(): DisplaySizeTestRule = DisplaySizeTestRule(
            DisplaySize.SMALL
        )

        fun normalDisplaySizeTestRule(): DisplaySizeTestRule =
            DisplaySizeTestRule(DisplaySize.NORMAL)

        fun largeDisplaySizeTestRule(): DisplaySizeTestRule = DisplaySizeTestRule(
            DisplaySize.LARGE
        )

        fun largerDisplaySizeTestRule(): DisplaySizeTestRule =
            DisplaySizeTestRule(DisplaySize.LARGER)

        fun largestDisplaySizeTestRule(): DisplaySizeTestRule =
            DisplaySizeTestRule(DisplaySize.LARGEST)
    }

    private var timeOutInMillis = MAX_RETRIES_TO_WAIT_FOR_SETTING * SLEEP_TO_WAIT_FOR_SETTING_MILLIS

    private val displayScaleSetting: DisplayScaleSetting = DisplayScaleSetting()

    override fun apply(base: Statement, description: Description): Statement {
        return DisplaySizeStatement(base, description, displayScaleSetting, displaySize, timeOutInMillis)
    }

    private class DisplaySizeStatement(
        private val baseStatement: Statement,
        private val description: Description,
        private val scaleSetting: DisplayScaleSetting,
        private val scale: DisplaySize,
        private val timeOutInMillis: Int,
    ) : Statement() {

        @Throws(Throwable::class)
        override fun evaluate() {
            val initialDisplay = scaleSetting.densityDpi
            try {
                val expectedDisplay = (initialDisplay * scale.value.toFloat()).toInt()
                scaleSetting.setDisplaySizeScale(scale)
                sleepUntil(densityDpiMatches(expectedDisplay), expectedDisplay)

                baseStatement.evaluate()

                scaleSetting.resetDisplaySizeScale(initialDisplay)
                sleepUntil(densityDpiMatches(initialDisplay), initialDisplay)
            } catch (throwable: Throwable){
                val testName = "${description.testClass.simpleName}\$${description.methodName}"
                val errorMessage =
                    "Test $testName failed on setting DisplaySize to ${scale.name}"
                Log.e(TAG, errorMessage)
                throw throwable
            } finally {
                scaleSetting.resetDisplaySizeScale(initialDisplay)
                sleepUntil(densityDpiMatches(initialDisplay), initialDisplay)
            }
        }

        private fun densityDpiMatches(densityDpi: Int): Condition {
            return object : Condition {
                override fun holds(): Boolean {
                    return scaleSetting.densityDpi == densityDpi
                }
            }
        }

        @Synchronized
        private fun sleepUntil(condition: Condition, expectedDisplay: Int) {
            var iterations = 0
            var retries = 0
            while (!condition.holds()) {
                iterations++
                val iterationsCount = timeOutInMillis / SLEEP_TO_WAIT_FOR_SETTING_MILLIS
                SystemClock.sleep(SLEEP_TO_WAIT_FOR_SETTING_MILLIS.toLong())
                val mustRetry = iterations % 10 == 0
                if (mustRetry) {
                    retries++
                    scaleSetting.setDisplaySizeScale(expectedDisplay)
                    Log.d(TAG, "trying to set DisplaySize to $expectedDisplay, $retries retry")
                }
                if (iterations == iterationsCount) {
                    throw timeoutError()
                }
            }
        }

        private fun timeoutError(): AssertionError {
            return AssertionError("Spent too long waiting trying to set display scale: $timeOutInMillis milliseconds")
        }

        companion object {
            const val SLEEP_TO_WAIT_FOR_SETTING_MILLIS = 100
            const val MAX_RETRIES_TO_WAIT_FOR_SETTING = 100
        }
    }
}