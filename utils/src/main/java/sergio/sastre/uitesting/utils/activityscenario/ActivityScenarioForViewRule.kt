package sergio.sastre.uitesting.utils.activityscenario

import android.app.Activity
import androidx.annotation.LayoutRes
import org.junit.rules.ExternalResource
import sergio.sastre.uitesting.utils.utils.inflate
import sergio.sastre.uitesting.utils.utils.inflateAndWaitForIdle
import sergio.sastre.uitesting.utils.utils.waitForActivity

class ActivityScenarioForViewRule(
    config: ViewConfigItem? = null,
) : ExternalResource() {

    val activityScenario =
        ActivityScenarioConfigurator.ForView().apply {
            config?.orientation?.also { orientation -> setInitialOrientation(orientation) }
            config?.locale?.also { locale -> setLocale(locale) }
            config?.uiMode?.also { uiMode -> setUiMode(uiMode) }
            config?.fontSize?.also { fontSize -> setFontSize(fontSize) }
            config?.displaySize?.also { displaySize -> setDisplaySize(displaySize) }
            config?.theme?.also { theme -> setTheme(theme) }
        }.launchConfiguredActivity()

    val activity: Activity by lazy { activityScenario.waitForActivity() }

    fun inflateAndWaitForIdle(@LayoutRes layoutId: Int, id: Int = android.R.id.content) =
        activity.inflateAndWaitForIdle(layoutId, id)

    override fun after() {
        activityScenario.close()
    }
}
