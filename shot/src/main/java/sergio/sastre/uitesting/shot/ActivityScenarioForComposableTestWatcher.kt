package sergio.sastre.uitesting.shot

import androidx.activity.ComponentActivity
import androidx.test.core.app.ActivityScenario
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfig
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator

internal class ActivityScenarioForComposableTestWatcher(
    private val screenshotConfig: ScreenshotConfig = ScreenshotConfig(),
) : TestWatcher() {

    val scenario: ActivityScenario<out ComponentActivity> by lazy {
        ActivityScenarioConfigurator.ForComposable()
            .setLocale(screenshotConfig.locale)
            .setInitialOrientation(screenshotConfig.orientation)
            .setUiMode(screenshotConfig.uiMode)
            .setFontSize(screenshotConfig.fontScale)
            .launchConfiguredActivity()
    }

    override fun finished(description: Description?) {
        super.finished(description)
        scenario.close()
    }
}
