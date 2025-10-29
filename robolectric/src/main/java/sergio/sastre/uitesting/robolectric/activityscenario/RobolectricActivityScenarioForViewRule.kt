package sergio.sastre.uitesting.robolectric.activityscenario

import android.app.Activity
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import org.junit.rules.ExternalResource
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ViewConfigItem
import sergio.sastre.uitesting.utils.utils.inflateAndWaitForIdle
import sergio.sastre.uitesting.utils.utils.waitForActivity

/**
 * Returns an ActivityScenario whose activity is configured with the given parameters
 *
 * WARNING: Do not use together with @Config(qualifiers = "my_qualifiers") to avoid
 * any misbehaviour
 */
class RobolectricActivityScenarioForViewRule(
    deviceScreen: DeviceScreen? = null,
    config: ViewConfigItem? = null,
    @ColorInt backgroundColor: Int? = null,
) : ExternalResource() {

    val activityScenario =
        RobolectricActivityScenarioConfigurator.ForView()
            .apply {
                deviceScreen?.also { screen -> setDeviceScreen(screen) }
            }.apply {
                config?.orientation?.also { orientation -> setInitialOrientation(orientation) }
                config?.locale?.also { locale -> setLocale(locale) }
                config?.uiMode?.also { uiMode -> setUiMode(uiMode) }
                config?.fontWeight?.also { fontWeight -> setFontWeight(fontWeight) }
                config?.fontSize?.also { fontSize -> setFontSize(fontSize) }
                config?.displaySize?.also { displaySize -> setDisplaySize(displaySize) }
                config?.theme?.also { theme -> setTheme(theme) }
            }.launchConfiguredActivity(backgroundColor)

    val activity: Activity by lazy { activityScenario.waitForActivity() }

    fun inflateAndWaitForIdle(@LayoutRes layoutId: Int, id: Int = android.R.id.content) =
        activity.inflateAndWaitForIdle(layoutId, id)

    override fun after() {
        activityScenario.close()
    }
}
