package sergio.sastre.uitesting.utils.testrules.displaysize

import android.os.SystemClock
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
        fun smallDisplaySizeTestRule(): DisplaySizeTestRule = DisplaySizeTestRule(DisplaySize.SMALL)

        fun normalDisplaySizeTestRule(): DisplaySizeTestRule =
            DisplaySizeTestRule(DisplaySize.NORMAL)

        fun largeDisplaySizeTestRule(): DisplaySizeTestRule = DisplaySizeTestRule(DisplaySize.LARGE)

        fun largerDisplaySizeTestRule(): DisplaySizeTestRule =
            DisplaySizeTestRule(DisplaySize.LARGER)

        fun largestDisplaySizeTestRule(): DisplaySizeTestRule =
            DisplaySizeTestRule(DisplaySize.LARGEST)
    }

    private var timeOutInMillis = MAX_RETRIES_TO_WAIT_FOR_SETTING * SLEEP_TO_WAIT_FOR_SETTING_MILLIS

    private val displayScaleSetting: DisplayScaleSetting =
        DisplayScaleSetting(getInstrumentation().targetContext.resources)

    private var previousScale: Int = 0

    /**
     * Since the Display Size setting is changed via adb, it might take longer than expected to
     * take effect, and could be device dependent. One can use this method to adjust the default
     * time out which is [MAX_RETRIES_TO_WAIT_FOR_SETTING] * [SLEEP_TO_WAIT_FOR_SETTING_MILLIS]
     */
    fun withTimeOut(@IntRange(from = 0) inMillis: Int): DisplaySizeTestRule = apply {
        this.timeOutInMillis =  inMillis
    }

    override fun starting(description: Description?) {
        previousScale = getInstrumentation().targetContext.resources.configuration.densityDpi
    }

    override fun finished(description: Description?) {
        displayScaleSetting.resetDisplaySizeScale(previousScale)
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
            sleepUntil(scaleMatches(expectedDisplay))

            baseStatement.evaluate()

            scaleSetting.resetDisplaySizeScale(initialDisplay)
            sleepUntil(scaleMatches(initialDisplay))
        }

        private fun scaleMatches(densityDpi: Int): Condition {
            return object : Condition {
                override fun holds(): Boolean {
                    return scaleSetting.getDensityDpi() == densityDpi
                }
            }
        }

        private fun sleepUntil(condition: Condition) {
            var retries = 0
            while (!condition.holds()) {
                val retriesCount = timeOutInMillis / SLEEP_TO_WAIT_FOR_SETTING_MILLIS
                SystemClock.sleep(SLEEP_TO_WAIT_FOR_SETTING_MILLIS.toLong())
                if (retries == retriesCount) {
                    throw timeoutError(retries)
                }
                retries++
            }
        }

        private fun timeoutError(retries: Int): AssertionError {
            return AssertionError("Spent too long waiting trying to set display scale.$retries retries")
        }

        companion object {
            const val SLEEP_TO_WAIT_FOR_SETTING_MILLIS = 100
            const val MAX_RETRIES_TO_WAIT_FOR_SETTING = 100
        }
    }
}