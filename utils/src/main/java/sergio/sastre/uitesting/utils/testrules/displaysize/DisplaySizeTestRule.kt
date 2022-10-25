package sergio.sastre.uitesting.utils.testrules.displaysize

import android.os.SystemClock
import android.util.Log
import androidx.annotation.IntRange

import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.testrules.Condition
import sergio.sastre.uitesting.utils.testrules.displaysize.DisplaySizeTestRule.DisplaySizeStatement.Companion.MAX_RETRIES_TO_WAIT_FOR_SETTING
import sergio.sastre.uitesting.utils.testrules.displaysize.DisplaySizeTestRule.DisplaySizeStatement.Companion.SLEEP_TO_WAIT_FOR_SETTING_MILLIS

/**
 * A TestRule to change Display size of the device/emulator via adb
 */
class DisplaySizeTestRule(
    private val displaySize: DisplaySize,
) : TestWatcher(), TestRule {

    companion object {
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

    private var previousDensityDpi: Int = 0

    /**
     * Since the Display Size setting is changed via adb, it might take longer than expected to
     * take effect, and could be device dependent. One can use this method to adjust the default
     * time out which is [MAX_RETRIES_TO_WAIT_FOR_SETTING] * [SLEEP_TO_WAIT_FOR_SETTING_MILLIS]
     */
    fun withTimeOut(@IntRange(from = 0) inMillis: Int): DisplaySizeTestRule = apply {
        this.timeOutInMillis = inMillis
    }

    override fun starting(description: Description?) {
        previousDensityDpi = getInstrumentation().targetContext.resources.configuration.densityDpi
    }

    override fun finished(description: Description?) {
        displayScaleSetting.resetDisplaySizeScale(previousDensityDpi)
    }

    override fun apply(base: Statement, description: Description): Statement {
        return DisplaySizeStatement(base, displayScaleSetting, displaySize, timeOutInMillis)
    }

    private class DisplaySizeStatement(
        private val baseStatement: Statement,
        private val scaleSetting: DisplayScaleSetting,
        private val scale: DisplaySize,
        private val timeOutInMillis: Int,
    ) : Statement() {

        @Throws(Throwable::class)
        override fun evaluate() {
            val initialDisplay = scaleSetting.getDensityDpi()
            val expectedDisplay = (initialDisplay * scale.value.toFloat()).toInt()
            scaleSetting.setDisplaySizeScale(scale)
            sleepUntil(scaleMatches(expectedDisplay), expectedDisplay)

            baseStatement.evaluate()

            scaleSetting.resetDisplaySizeScale(initialDisplay)
            sleepUntil(scaleMatches(initialDisplay), initialDisplay)
        }

        private fun scaleMatches(densityDpi: Int): Condition {
            return object : Condition {
                override fun holds(): Boolean {
                    return scaleSetting.getDensityDpi() == densityDpi
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
                    Log.d("DisplaySizeTestRule", "trying to set DisplaySize to $expectedDisplay, $retries retry")
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