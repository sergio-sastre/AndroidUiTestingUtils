package sergio.sastre.uitesting.roborazzi

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioForComposableRule
import sergio.sastre.uitesting.roborazzi.config.RoborazziSharedTestAdapter
import sergio.sastre.uitesting.sharedtest.roborazzi.RoborazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable
import sergio.sastre.uitesting.utils.utils.waitForActivity
import java.io.File

class RoborazziScreenshotTestRuleForComposable(
    override val config: ScreenshotConfigForComposable = ScreenshotConfigForComposable(),
) : ScreenshotTestRuleForComposable(config) {

    private val activityScenarioRule: RobolectricActivityScenarioForComposableRule by lazy {
        RobolectricActivityScenarioForComposableRule(
            config = config.toComposableConfig(),
            deviceScreen = roborazziAdapter.asDeviceScreen(),
            backgroundColor = roborazziConfig.backgroundColor,
        )
    }

    private val roborazziAdapter: RoborazziSharedTestAdapter by lazy {
        RoborazziSharedTestAdapter(roborazziConfig)
    }

    private var roborazziConfig: RoborazziConfig = RoborazziConfig()

    private val filePathGenerator: FilePathGenerator = FilePathGenerator()

    override fun apply(base: Statement?, description: Description?): Statement =
        activityScenarioRule.apply(base, description)

    override fun snapshot(composable: @Composable () -> Unit) {
        snapshot(null, composable)
    }

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        activityScenarioRule.activityScenario
            .onActivity {
                it.setContent { composable.invoke() }
            }
            .waitForActivity()

        activityScenarioRule
            .composeRule
            .onRoot()
            .captureRoboImage(
                filePath = filePathGenerator.invoke(roborazziConfig.filePath, name),
                roborazziOptions = roborazziAdapter.asRoborazziOptions(),
            )
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForComposable = apply {
        if (config is RoborazziConfig) {
            roborazziConfig = config
        }
    }
}
