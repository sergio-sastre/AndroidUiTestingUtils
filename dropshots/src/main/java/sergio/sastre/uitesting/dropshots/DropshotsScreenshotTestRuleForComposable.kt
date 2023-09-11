package sergio.sastre.uitesting.dropshots

import androidx.compose.runtime.Composable
import com.dropbox.dropshots.Dropshots
import org.junit.rules.RuleChain
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForComposableRule
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable

class DropshotsScreenshotTestRuleForComposable(
    override val config: ScreenshotConfigForComposable = ScreenshotConfigForComposable(),
) : ScreenshotTestRuleForComposable(config) {

    private val activityScenarioForComposableRule by lazy {
        ActivityScenarioForComposableRule(
            config = config.toComposableConfig(),
            backgroundColor = dropshotsConfig.backgroundColor,
        )
    }

    private val dropshotsRule: ScreenshotTaker by lazy {
        ScreenshotTaker(
            Dropshots(
                resultValidator = dropshotsConfig.resultValidator,
                imageComparator = dropshotsConfig.imageComparator,
            )
        )
    }

    private var dropshotsConfig: DropshotsConfig = DropshotsConfig()

    override fun snapshot(composable: @Composable () -> Unit) {
        takeSnapshot(null, composable)
    }

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        takeSnapshot(name, composable)
    }

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        RuleChain
            .outerRule(dropshotsRule)
            .around(activityScenarioForComposableRule)
            .apply(base, description)

    private fun takeSnapshot(name: String?, composable: @Composable () -> Unit) {
        val composeView =
            activityScenarioForComposableRule
                .setContent(composable)
                .composeView

        dropshotsRule.assertSnapshot(
            view= composeView,
            bitmapCaptureMethod = dropshotsConfig.bitmapCaptureMethod,
            name = name,
            filePath = dropshotsConfig.filePath,
        )
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForComposable = apply {
        if (config is DropshotsConfig) {
            dropshotsConfig = config
        }
    }
}
