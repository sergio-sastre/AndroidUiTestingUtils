package sergio.sastre.uitesting.utils.testrules.fontsize

import android.os.SystemClock
import androidx.annotation.IntRange

import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.testrules.Condition
import sergio.sastre.uitesting.utils.testrules.displaysize.DisplaySizeTestRule
import sergio.sastre.uitesting.utils.testrules.fontsize.FontSizeTestRule.FontScaleStatement.Companion.MAX_RETRIES_TO_WAIT_FOR_SETTING
import sergio.sastre.uitesting.utils.testrules.fontsize.FontSizeTestRule.FontScaleStatement.Companion.SLEEP_TO_WAIT_FOR_SETTING_MILLIS

/**
 * A TestRule to change FontSize of the device/emulator. It is done:
 * - API < 25 : modifying resources.configuration
 * - API 25 + : via adb (slower)
 *
 * Strongly based on code from espresso-support library, from Novoda
 * https://github.com/novoda/espresso-support/tree/master/core/src/main/java/com/novoda/espresso
 */
class FontSizeTestRule(
    private val fontSize: FontSize
) : TestWatcher(), TestRule {

    companion object {
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
            sleepUntil(scaleMatches(scale))

            baseStatement.evaluate()

            scaleSetting.set(initialScale)
            sleepUntil(scaleMatches(initialScale))
        }

        private fun scaleMatches(scale: FontSize): Condition {
            return object : Condition {
                override fun holds(): Boolean {
                    return scaleSetting.get() === scale
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
            return AssertionError("Spent too long waiting trying to set font scale.$retries retries")
        }

        companion object {
            const val SLEEP_TO_WAIT_FOR_SETTING_MILLIS = 100
            const val MAX_RETRIES_TO_WAIT_FOR_SETTING = 100
        }
    }
}