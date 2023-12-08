package sergio.sastre.uitesting.utils.testrules.animations

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand
import java.io.IOException

class DisableAnimationsRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    setAnimations(enable = false)
                    base.evaluate()
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
