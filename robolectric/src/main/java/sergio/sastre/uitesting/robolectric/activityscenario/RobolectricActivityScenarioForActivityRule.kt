package sergio.sastre.uitesting.robolectric.activityscenario

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.test.core.app.ActivityScenario
import org.junit.rules.ExternalResource
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ActivityConfigItem
import sergio.sastre.uitesting.utils.utils.waitForActivity

class RobolectricActivityScenarioForActivityRule<T : Activity> private constructor() :
    ExternalResource() {

    lateinit var activityScenario: ActivityScenario<T>
        private set

    val activity: Activity by lazy { activityScenario.waitForActivity() }

    val rootView: View
        get() = this.activity.window.decorView

    /**
     * Returns an ActivityScenario whose activity is configured with the given parameters
     *
     * WARNING: Do not use together with @Config(qualifiers = "my_qualifiers") to avoid
     * any misbehaviour
     */
    constructor(
        clazz: Class<T>,
        intent: Intent,
        activityOptions: Bundle? = null,
        deviceScreen: DeviceScreen? = null,
        config: ActivityConfigItem? = null,
    ) : this() {
        activityScenario =
            RobolectricActivityScenarioConfigurator.ForActivity()
                .applyDeviceScreen(deviceScreen)
                .applyConfig(config)
                .launch<T>(clazz, intent, activityOptions)
    }

    /**
     * Returns an ActivityScenario whose activity is configured with the given parameters
     *
     * WARNING: Do not use together with @Config(qualifiers = "my_qualifiers") to avoid
     * any misbehaviour
     */
    constructor(
        clazz: Class<T>,
        deviceScreen: DeviceScreen? = null,
        config: ActivityConfigItem? = null,
    ) : this() {
        activityScenario =
            RobolectricActivityScenarioConfigurator.ForActivity()
                .applyDeviceScreen(deviceScreen)
                .applyConfig(config)
                .launch(clazz)
    }

    override fun apply(base: Statement?, description: Description?): Statement {
        return super.apply(base, description)
    }

    private fun RobolectricActivityScenarioConfigurator.ForActivity.applyConfig(
        config: ActivityConfigItem? = null,
    ): RobolectricActivityScenarioConfigurator.ForActivity = apply {
        config?.orientation?.also { orientation -> setOrientation(orientation) }
        config?.systemLocale?.also { locale -> setSystemLocale(locale) }
        config?.uiMode?.also { uiMode -> setUiMode(uiMode) }
        config?.fontSize?.also { fontSize -> setFontSize(fontSize) }
        config?.displaySize?.also { displaySize -> setDisplaySize(displaySize) }
    }

    private fun RobolectricActivityScenarioConfigurator.ForActivity.applyDeviceScreen(
        deviceScreen: DeviceScreen? = null,
    ): RobolectricActivityScenarioConfigurator.ForActivity = apply {
        deviceScreen?.also { screen -> setDeviceScreen(screen) }
    }

    override fun after() {
        activityScenario.close()
    }
}
