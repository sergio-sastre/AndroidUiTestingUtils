package sergio.sastre.uitesting.android_testify

import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentActivity
import dev.testify.ScreenshotRule
import dev.testify.core.TestifyConfiguration
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator.LandscapeSnapshotConfiguredActivity
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator.PortraitSnapshotConfiguredActivity
import sergio.sastre.uitesting.utils.activityscenario.ViewConfigItem
import sergio.sastre.uitesting.utils.common.Orientation

class ScreenshotRuleWithConfigurationForView(
    exactness: Float? = null,
    enableReporter: Boolean = false,
    initialTouchMode: Boolean = false,
    config: ViewConfigItem? = null,
    @ColorInt activityBackgroundColor: Int? = null
) : ScreenshotRule<FragmentActivity>(
    activityClass = getActivityClassFor(config?.orientation),
    enableReporter = enableReporter,
    initialTouchMode = initialTouchMode,
    configuration = TestifyConfiguration(exactness = exactness)
) {

    init {
        ActivityScenarioConfigurator.ForView()
            .apply {
                config?.uiMode?.run { setUiMode(this) }
                config?.locale?.run { setLocale(this) }
                config?.fontSize?.run { setFontSize(this) }
                config?.displaySize?.run { setDisplaySize(this) }
                config?.theme?.run { setTheme(this) }
                activityBackgroundColor?.run { setActivityBackgroundColor(this) }
            }
    }
}

@Suppress("UNCHECKED_CAST")
private fun getActivityClassFor(orientation: Orientation?): Class<FragmentActivity> =
    when (orientation == Orientation.LANDSCAPE) {
        true -> LandscapeSnapshotConfiguredActivity::class.java
        false -> PortraitSnapshotConfiguredActivity::class.java
    } as Class<FragmentActivity>
