package sergio.sastre.uitesting.utils.testrules.uiMode

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand

internal class AdbUiModeSetter(
    val base: Statement,
    val description: Description
): UiModeSetter {

    companion object {
        private const val UI_MODE_NIGHT_YES_COMMAND_VALUE = "yes"
        private const val UI_MODE_NIGHT_NO_COMMAND_VALUE = "no"
        private const val UI_MODE_NIGHT_AUTO_COMMAND_VALUE = "auto"

        private val TAG = AdbUiModeSetter::class.java.simpleName
    }
    override fun setUiModeDuringTestOnly(uiMode: UiMode) {
        val initialUiMode = getInstrumentation().targetContext.uiModeNight
        try {
            getInstrumentation().waitForExecuteShellCommand(uiModeCommand(uiMode))
            base.evaluate()
        } catch (throwable: Throwable) {
            val testName = "${description.testClass.simpleName}\$${description.methodName}"
            val errorMessage =
                "Test $testName failed on setting uiMode to ${uiMode.name}"
            Log.e(TAG, errorMessage)
            throw throwable
        } finally {
            getInstrumentation().waitForExecuteShellCommand(uiModeCommand(initialUiMode))
        }
    }

    private val Context.uiModeNight
        get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

    private fun uiModeCommand(uiMode: Int): String {
        val value = when (uiMode) {
            Configuration.UI_MODE_NIGHT_YES -> UI_MODE_NIGHT_YES_COMMAND_VALUE
            Configuration.UI_MODE_NIGHT_NO -> UI_MODE_NIGHT_NO_COMMAND_VALUE
            else -> UI_MODE_NIGHT_AUTO_COMMAND_VALUE
        }
        return "cmd uimode night $value"
    }

    private fun uiModeCommand(uiMode: UiMode): String {
        val value = when (uiMode) {
            UiMode.NIGHT -> UI_MODE_NIGHT_YES_COMMAND_VALUE
            UiMode.DAY -> UI_MODE_NIGHT_NO_COMMAND_VALUE
        }
        return "cmd uimode night $value"
    }
}
