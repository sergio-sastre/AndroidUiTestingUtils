package sergio.sastre.uitesting.utils.testrules.systemui

import android.graphics.Bitmap
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.testrules.systemui.navigation.Navigation
import sergio.sastre.uitesting.utils.testrules.systemui.navigation.NavigationModeTestRule
import sergio.sastre.uitesting.utils.testrules.systemui.statusbar.ClockTime
import sergio.sastre.uitesting.utils.testrules.systemui.statusbar.StatusBarTestRule
import sergio.sastre.uitesting.utils.utils.drawFullScreenToBitmap as fullScreenToBitmap

/**
 * A [TestRule] that combines [StatusBarTestRule] and [NavigationModeTestRule]
 * to provide a consistent System UI for testing.
 *
 * @param navigation The target [Navigation] mode.
 * @param clockTime The time to display in the status bar.
 */
class SystemUiTestRule(
    private val navigation: Navigation = Navigation.GESTURAL,
    private val clockTime: ClockTime = ClockTime(12, 30),
) : TestRule {

    override fun apply(base: Statement, description: Description): Statement =
        RuleChain.outerRule(StatusBarTestRule(clockTime))
            .around(NavigationModeTestRule(navigation))
            .apply(base, description)

    /**
     * Captures a screenshot of the entire screen.
     * Safe to use for screenshot testing as this rule ensures the System UI state is reproducible.
     */
    fun drawFullScreenToBitmap(): Bitmap = fullScreenToBitmap()
}
