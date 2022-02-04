package sergio.sastre.uitesting.utils.testrules.fontsize

import android.os.SystemClock

import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.FontScale

class FontScaleTestRule(
    private val fontScaleSetting: FontScaleSetting,
    private val fontScale: FontScale
) : TestWatcher(), TestRule {
    private var previousScale: Float = 0.toFloat()

    override fun starting(description: Description?) {
        previousScale = getInstrumentation().targetContext.resources.configuration.fontScale
    }

    override fun finished(description: Description?) {
        fontScaleSetting.set(FontScale.from(previousScale))
    }

    override fun apply(base: Statement, description: Description): Statement {
        return FontScaleStatement(base, fontScaleSetting, fontScale)
    }

    private class FontScaleStatement constructor(
        private val baseStatement: Statement,
        private val scaleSetting: FontScaleSetting,
        private val scale: FontScale
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

        private fun scaleMatches(scale: FontScale): Condition {
            return object : Condition {
                override fun holds(): Boolean {
                    return scaleSetting.get() === scale
                }
            }
        }

        private fun sleepUntil(condition: Condition) {
            var retries = 0
            while (!condition.holds()) {
                SystemClock.sleep(SLEEP_TO_WAIT_FOR_SETTING_MILLIS.toLong())
                if (retries == MAX_RETRIES_TO_WAIT_FOR_SETTING) {
                    throw timeoutError(retries)
                }
                retries++
            }
        }

        private fun timeoutError(retries: Int): AssertionError {
            return AssertionError("Spent too long waiting trying to set scale.$retries retries")
        }

        companion object {
            private const val SLEEP_TO_WAIT_FOR_SETTING_MILLIS = 100
            private const val MAX_RETRIES_TO_WAIT_FOR_SETTING = 100
        }
    }
}