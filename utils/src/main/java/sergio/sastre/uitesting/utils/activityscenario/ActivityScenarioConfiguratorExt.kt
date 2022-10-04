package sergio.sastre.uitesting.utils.activityscenario

import android.app.Activity

inline fun <reified T : Activity> activityScenarioForActivityRule(
    config: ActivityConfigItem? = null,
): ActivityScenarioForActivityRule<T> = ActivityScenarioForActivityRule(
    T::class.java,
    config,
)
