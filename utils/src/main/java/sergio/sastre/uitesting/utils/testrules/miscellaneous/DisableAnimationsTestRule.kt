package sergio.sastre.uitesting.utils.testrules.miscellaneous

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand
import java.io.IOException

class DisableAnimationsTestRule : TestRule {

    companion object {
        private val TAG = DisableAnimationsTestRule::class.java.simpleName
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    setAnimations(enable = false)
                    base.evaluate()
                } catch (throwable: Throwable) {
                    val testName = "${description.testClass.simpleName}\$${description.methodName}"
                    val errorMessage =
                        "Test $testName failed on disabling animations"
                    Log.e(TAG, errorMessage)
                    throw throwable
                } finally {
                    setAnimations(enable = true)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun setAnimations(enable: Boolean = true) {
        val animationEnabledValue = when (enable) {
            true -> 1
            false -> 0
        }
        getInstrumentation().run {
            waitForExecuteShellCommand("settings put global transition_animation_scale $animationEnabledValue")
            waitForExecuteShellCommand("settings put global window_animation_scale $animationEnabledValue")
            waitForExecuteShellCommand("settings put global animator_duration_scale $animationEnabledValue")
        }
    }
}
