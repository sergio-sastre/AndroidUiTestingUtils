package sergio.sastre.uitesting.utils.testrules.systemui.navigation

import android.app.Instrumentation
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand

/**
 * A [TestRule] that changes the system navigation mode (e.g., Three-button or Gestural)
 * for the duration of a test and restores it afterwards.
 *
 * WARNING: Only works on Instrumentation tests, not on Robolectric tests.
 *
 * @param navigation The target [Navigation] mode to set during the test.
 */
class NavigationModeTestRule(
    private val navigation: Navigation
) : TestRule {

    companion object {
        const val TIMEOUT_IN_MS = 10_000L
    }

    private val TAG = javaClass.simpleName

    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            val instrumentation = getInstrumentation()
            val targetMode = NavigationMode.from(navigation)
            val originalMode = instrumentation.getNavigationMode()

            try {
                instrumentation.setNavigationMode(targetMode)
                instrumentation.waitUntilUiMatches(navigation, TIMEOUT_IN_MS)
                base.evaluate()
            } catch (throwable: Throwable) {
                val testName = "${description.testClass.simpleName}\$${description.methodName}"
                val errorMessage =
                    "Test $testName failed on setting NavigationMode to ${navigation.name}"
                Log.e(TAG, errorMessage)
                throw throwable
            } finally {
                instrumentation.setNavigationMode(originalMode)
                UiDevice.getInstance(instrumentation).waitForIdle()
                instrumentation.waitForIdleSync()
            }
        }
    }

    /**
     * Reads the current navigation mode from system settings.
     */
    private fun Instrumentation.getNavigationMode(): NavigationMode {
        val currentNavigationMode =
            waitForExecuteShellCommand("settings get secure navigation_mode").toIntOrNull()
        return NavigationMode.entries.find { it.navModeValue == currentNavigationMode }
            ?: NavigationMode.GESTURAL
    }

    /**
     * Sets the navigation mode using shell commands (`cmd overlay`) and waits for the system setting to change.
     */
    private fun Instrumentation.setNavigationMode(targetMode: NavigationMode) {
        if (getNavigationMode() == targetMode) return

        NavigationMode.entries.filter { it != targetMode }.forEach {
            waitForExecuteShellCommand("cmd overlay disable ${it.adbValue}")
        }
        waitForExecuteShellCommand("cmd overlay enable ${targetMode.adbValue}")

        // Wait for system setting to change
        val startTime = System.currentTimeMillis()
        while (getNavigationMode() != targetMode && System.currentTimeMillis() - startTime < TIMEOUT_IN_MS) {
            Thread.sleep(100)
        }

        if (getNavigationMode() != targetMode) {
            throw IllegalStateException("Timed out waiting for system setting to change to: $targetMode")
        }
    }

    /**
     * Waits until the System UI reflects the requested navigation mode.
     * It checks for the presence or absence of the home, back, and recents buttons.
     */
    private fun Instrumentation.waitUntilUiMatches(navigation: Navigation, timeout: Long) {
        val device = UiDevice.getInstance(this)
        val startTime = System.currentTimeMillis()

        val home = By.res("com.android.systemui", "home")
        val back = By.res("com.android.systemui", "back")
        val recents = By.res("com.android.systemui", "recent_apps")

        while (System.currentTimeMillis() - startTime < timeout) {
            val isHomeVisible = device.hasObject(home)
            val isBackVisible = device.hasObject(back)
            val isRecentsVisible = device.hasObject(recents)

            val isMatching = when (navigation) {
                Navigation.THREE_BUTTON -> isHomeVisible && isBackVisible && isRecentsVisible
                Navigation.GESTURAL -> !isHomeVisible && !isBackVisible && !isRecentsVisible
            }

            if (isMatching) {
                device.waitForIdle()
                waitForIdleSync()
                return
            }
            Thread.sleep(100)
        }
        throw IllegalStateException("Timed out waiting for Navigation UI to reflect mode: $navigation")
    }

    private enum class NavigationMode(val adbValue: String, val navModeValue: Int) {
        THREE_BUTTON(Navigation.THREE_BUTTON.adbValue, Navigation.THREE_BUTTON.navModeValue),
        GESTURAL(Navigation.GESTURAL.adbValue, Navigation.GESTURAL.navModeValue),
        TWO_BUTTON("com.android.internal.systemui.navbar.twobutton", 1);

        companion object {
            fun from(navigation: Navigation) = when (navigation) {
                Navigation.THREE_BUTTON -> THREE_BUTTON
                Navigation.GESTURAL -> GESTURAL
            }
        }
    }
}