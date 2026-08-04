package sergio.sastre.uitesting.utils.testrules.systemui

import android.graphics.Bitmap
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.testrules.systemui.navigation.NavigationModeTestRule
import sergio.sastre.uitesting.utils.testrules.systemui.statusbar.StatusBarTestRule
import sergio.sastre.uitesting.utils.utils.drawFullScreenToBitmap as fullScreenToBitmap

/**
 * A [TestRule] that combines [StatusBarTestRule] and [NavigationModeTestRule]
 * to provide a consistent System UI for testing.
 *
 * WARNING: the statusBarConfig uses Demo Mode via adb, which is ensured to work on Nexus and Pixel devices, but might not work on others
 * Moreover, it does only support status bar in English language (e.g. RTL and en_XA are not reflected)
 *
 * WARNING 2: Only works on Instrumentation tests, not on Robolectric tests.
 *
 * @param navigationConfig Configuration for the system navigation bar.
 * @param statusBarConfig Configuration for the system status bar.
 */
class SystemUiTestRule(
    private val navigationConfig: NavigationConfig = NavigationConfig(),
    private val statusBarConfig: StatusBarConfig = StatusBarConfig(),
) : TestRule {

    override fun apply(base: Statement, description: Description): Statement =
        RuleChain.outerRule(
            StatusBarTestRule(
                clockTime = statusBarConfig.clockTime,
                showWifiIcon = statusBarConfig.showWifiIcon,
            )
        )
            .around(
                NavigationModeTestRule(
                    navigation = navigationConfig.mode,
                    customThreeButtonIdentifiers = navigationConfig.customThreeButtonIdentifiers,
                )
            )
            .apply(base, description)

    /**
     * Captures a screenshot of the entire screen.
     * Safe to use for screenshot testing as this rule ensures the System UI state is reproducible.
     */
    fun drawFullScreenToBitmap(): Bitmap = fullScreenToBitmap()
}
