package sergio.sastre.uitesting.utils.testrules.systemui.statusbar

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand

/**
 * A [TestRule] that uses Android's "Demo Mode" to provide a clean and consistent Status Bar
 * for screenshot testing.
 *
 * It sets:
 * - Clock time: configurable (default 12:30)
 * - Battery: 100%, not plugged in
 * - Network: Either Wifi with full signal or hides the Wifi icon
 * - Notifications: Hidden
 *
 * The Demo Mode is applied when the test starts and reapplied every time an Activity
 * enters the RESUMED stage to ensure it stays active throughout the test.
 *
 * WARNING: Demo Mode is ensured to work on Nexus and Pixel devices, but might not work on others
 * Moreover, it does only support status bar in English language (e.g. RTL and en_XA are not reflected)
 *
 * WARNING 2: Only works on Instrumentation tests, not on Robolectric tests.
 *
 * @param clockTime The time to display in the status bar.
 */
class StatusBarTestRule(
    private val clockTime: ClockTime = ClockTime(12, 30),
    private val showWifiIcon: Boolean = true,
) : TestRule {

    /**
     * Set a custom clock time using a string in "hh:mm" format (e.g., "12:30").
     */
    constructor(
        hhmmClock: String,
        showWifiIcon: Boolean = true
    ) : this(ClockTime.from(hhmmClock), showWifiIcon)

    private val TAG = javaClass.simpleName

    companion object {
        private const val BROADCAST_DEMO_COMMAND =
            "am broadcast -a com.android.systemui.demo -e command"
    }

    private val lifecycleCallback =
        androidx.test.runner.lifecycle.ActivityLifecycleCallback { _, stage ->
            if (stage == androidx.test.runner.lifecycle.Stage.RESUMED) {
                // Re-Apply Demo Mode when the activity is fully resumed and focused
                applyDemoMode()
            }
        }

    private val wifiAdbCommand
        get() = when (showWifiIcon) {
            true -> "$BROADCAST_DEMO_COMMAND network -e wifi show -e level 4"
            false -> "$BROADCAST_DEMO_COMMAND network -e wifi hide"
        }

    private fun applyDemoMode() {
        val instrumentation = getInstrumentation()
        try {
            instrumentation.waitForExecuteShellCommand("settings put global sysui_demo_allowed 1")
            instrumentation.waitForExecuteShellCommand("$BROADCAST_DEMO_COMMAND enter")
            instrumentation.waitForExecuteShellCommand("$BROADCAST_DEMO_COMMAND clock -e hhmm ${clockTime.toHhmmString()}")
            instrumentation.waitForExecuteShellCommand("$BROADCAST_DEMO_COMMAND battery -e level 100 -e plugged false")
            instrumentation.waitForExecuteShellCommand(wifiAdbCommand)
            instrumentation.waitForExecuteShellCommand("$BROADCAST_DEMO_COMMAND notifications -e visible false")

            // Allow SystemUI a moment to reflect the changes
            UiDevice.getInstance(instrumentation).waitForIdle()
            instrumentation.waitForIdleSync()
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to change the status bar by applying demo mode on RESUMED",
                e
            )
        }
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val monitor =
                    androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                try {
                    // Register callback to catch the activity launch
                    monitor.addLifecycleCallback(lifecycleCallback)

                    // Initial apply in case the activity is already running
                    applyDemoMode()

                    base.evaluate()
                } catch (throwable: Throwable) {
                    val testName = "${description.testClass.simpleName}\$${description.methodName}"
                    val errorMessage =
                        "Test $testName failed on setting StatusBar in Demo Mode"
                    Log.e(TAG, errorMessage)
                    throw throwable
                } finally {
                    monitor.removeLifecycleCallback(lifecycleCallback)
                    // Cleanup
                    val instrumentation = getInstrumentation()
                    instrumentation.waitForExecuteShellCommand("$BROADCAST_DEMO_COMMAND exit")
                    instrumentation.waitForExecuteShellCommand("settings put global sysui_demo_allowed 0")
                    // Allow SystemUI a moment to reflect the changes
                    UiDevice.getInstance(instrumentation).waitForIdle()
                    instrumentation.waitForIdleSync()
                }
            }
        }
    }
}