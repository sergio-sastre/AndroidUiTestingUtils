package sergio.sastre.uitesting.utils.activityscenario

import android.app.Activity
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import org.junit.rules.ExternalResource
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.utils.waitForActivity

class ActivityScenarioForComposableRule(
    config: ComposableConfigItem? = null,
): ExternalResource() {

    private val emptyComposeRule = createEmptyComposeRule()

    val composeRule : ComposeTestRule by lazy {
        activityScenario.waitForActivity()
        emptyComposeRule
    }

    val activityScenario =
        ActivityScenarioConfigurator.ForComposable().apply {
            config?.orientation?.also { orientation -> setInitialOrientation(orientation) }
            config?.locale?.also { locale -> setLocale(locale) }
            config?.uiMode?.also { uiMode -> setUiMode(uiMode) }
            config?.fontSize?.also { fontSize -> setFontSize(fontSize) }
            config?.displaySize?.also { displaySize -> setDisplaySize(displaySize) }
        }.launchConfiguredActivity()

    val activity: Activity by lazy { activityScenario.waitForActivity() }

    override fun apply(base: Statement?, description: Description?): Statement {
        // we need to call super, otherwise after() will not be called
        val externalResourceStatement = super.apply(base, description)
        return emptyComposeRule.apply(externalResourceStatement, description)
    }

    override fun after() {
        activityScenario.close()
    }
}
