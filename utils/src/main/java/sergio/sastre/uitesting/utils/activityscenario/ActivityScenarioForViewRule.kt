package sergio.sastre.uitesting.utils.activityscenario

import android.app.Activity
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import org.junit.rules.ExternalResource
import sergio.sastre.uitesting.utils.utils.inflateAndWaitForIdle
import sergio.sastre.uitesting.utils.utils.waitForActivity

class ActivityScenarioForViewRule(
    config: ViewConfigItem? = null,
    @ColorInt backgroundColor: Int? = null,
) : ExternalResource() {

    val activityScenario =
        ActivityScenarioConfigurator.ForView().apply {
            config?.orientation?.also { orientation -> setInitialOrientation(orientation) }
            config?.locale?.also { locale -> setLocale(locale) }
            config?.uiMode?.also { uiMode -> setUiMode(uiMode) }
            config?.fontWeight?.also { fontWeight -> setFontWeight(fontWeight) }
            config?.fontSize?.also { fontSize -> setFontSize(fontSize) }
            config?.displaySize?.also { displaySize -> setDisplaySize(displaySize) }
            config?.theme?.also { theme -> setTheme(theme) }
        }.launchConfiguredActivity(backgroundColor)

    val activity: Activity by lazy { activityScenario.waitForActivity() }

    fun inflateAndWaitForIdle(@LayoutRes layoutId: Int, id: Int = android.R.id.content): View =
        activity.inflateAndWaitForIdle(layoutId, id)

    override fun after() {
        activityScenario.close()
    }
}
