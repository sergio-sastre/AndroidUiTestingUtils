package sergio.sastre.uitesting.utils.testrules.fontsize

import android.os.SystemClock
import android.util.Log
import androidx.annotation.Discouraged
import androidx.annotation.IntRange

import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.testrules.Condition
import sergio.sastre.uitesting.utils.testrules.fontsize.FontSizeTestRule.FontScaleStatement.Companion.MAX_RETRIES_TO_WAIT_FOR_SETTING
import sergio.sastre.uitesting.utils.testrules.fontsize.FontSizeTestRule.FontScaleStatement.Companion.SLEEP_TO_WAIT_FOR_SETTING_MILLIS

/**
 * A TestRule to change FontSize of the device/emulator. It is done via adb
 *
 * Strongly based on code from espresso-support library, from Novoda
 * https://github.com/novoda/espresso-support/tree/master/core/src/main/java/com/novoda/espresso
 */
class FontSizeTestRule(
    private val fontSize: FontSize
) : TestWatcher(), TestRule {

    companion object {
        val TAG = FontSizeTestRule::class.java.simpleName
        fun smallFontScaleTestRule(): FontSizeTestRule = FontSizeTestRule(FontSize.SMALL)

        fun normalFontScaleTestRule(): FontSizeTestRule = FontSizeTestRule(FontSize.NORMAL)

        fun largeFontScaleTestRule(): FontSizeTestRule = FontSizeTestRule(FontSize.LARGE)

        fun hugeFontScaleTestRule(): FontSizeTestRule = FontSizeTestRule(FontSize.HUGE)
    }

    private var timeOutInMillis = MAX_RETRIES_TO_WAIT_FOR_SETTING * SLEEP_TO_WAIT_FOR_SETTING_MILLIS

    private val fontScaleSetting: FontScaleSetting = FontScaleSetting()

    private var previousScale: Float = 0.toFloat()

    /**
     * Since the Font Size setting might be changed via adb, it might take longer than expected to
     * take effect, and could be device dependent. One can use this method to adjust the default
     * time out which is [MAX_RETRIES_TO_WAIT_FOR_SETTING] * [SLEEP_TO_WAIT_FOR_SETTING_MILLIS]
     */
    @Discouraged(
        message = "Consider removing this method, since it will be removed in a future version. " +
                "This was initially built as a workaround for an issue that should not happen anymore. " +
                "If after all, you still need this, consider opening an issue."
    )
    fun withTimeOut(@IntRange(from = 0) inMillis: Int): FontSizeTestRule = apply {
        this.timeOutInMillis = inMillis
    }

    override fun starting(description: Description?) {
        previousScale = getInstrumentation().targetContext.resources.configuration.fontScale
    }

    override fun finished(description: Description?) {
        fontScaleSetting.set(FontSize.from(previousScale))
    }

    override fun apply(base: Statement, description: Description): Statement {
        return FontScaleStatement(base, fontScaleSetting, fontSize, timeOutInMillis)
    }

    private class FontScaleStatement(
        private val baseStatement: Statement,
        private val scaleSetting: FontScaleSetting,
        private val scale: FontSize,
        private val timeOutInMillis: Int,
    ) : Statement() {

        @Throws(Throwable::class)
        override fun evaluate() {
            val initialScale = scaleSetting.get()
            scaleSetting.set(scale)
            sleepUntil(scaleMatches(scale), scale)

            baseStatement.evaluate()

            scaleSetting.set(initialScale)
            sleepUntil(scaleMatches(initialScale), initialScale)
        }

        private fun scaleMatches(scale: FontSize): Condition {
            return object : Condition {
                override fun holds(): Boolean {
                    return scaleSetting.get() === scale
                }
            }
        }

        @Synchronized
        private fun sleepUntil(condition: Condition, expectedFontSize: FontSize) {
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
                    Log.d(TAG, "trying to set FontSize to ${expectedFontSize.name}, $retries retry")
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