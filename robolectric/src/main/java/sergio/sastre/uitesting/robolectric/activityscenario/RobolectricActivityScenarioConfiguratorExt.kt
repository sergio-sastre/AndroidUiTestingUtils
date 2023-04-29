package sergio.sastre.uitesting.robolectric.activityscenario

import android.app.Activity
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ActivityConfigItem

inline fun <reified T : Activity> robolectricActivityScenarioForActivityRule(
    deviceScreen: DeviceScreen? = null,
    config: ActivityConfigItem? = null,
): RobolectricActivityScenarioForActivityRule<T> = RobolectricActivityScenarioForActivityRule(
    T::class.java,
    deviceScreen,
    config,
)
