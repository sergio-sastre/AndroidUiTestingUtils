package sergio.sastre.uitesting.android_testify.screenshotscenario

import androidx.compose.runtime.Composable
import dev.testify.core.TestifyConfiguration
import dev.testify.scenario.ScreenshotScenarioRule
import org.junit.rules.RuleChain
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.android_testify.AndroidTestifyConfig
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForComposableRule
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable
import sergio.sastre.uitesting.utils.testrules.animations.DisableAnimationsRule

class ScreenshotScenarioRuleForComposable(
    override val config: ScreenshotConfigForComposable = ScreenshotConfigForComposable(),
) : ScreenshotTestRuleForComposable(config) {

    private val screenshotRule: ScreenshotScenarioRule by lazy {
        ScreenshotScenarioRule(
            configuration = TestifyConfiguration(
                exactness = androidTestifyConfig.exactness,
                hideCursor = androidTestifyConfig.hideCursor,
                hidePasswords = androidTestifyConfig.hidePasswords,
                hideScrollbars = androidTestifyConfig.hideScrollbars,
                hideSoftKeyboard = androidTestifyConfig.hideSoftKeyboard,
                hideTextSuggestions = androidTestifyConfig.hideTextSuggestions,
                useSoftwareRenderer = androidTestifyConfig.useSoftwareRenderer,
                exclusionRects = androidTestifyConfig.exclusionRects,
                exclusionRectProvider = androidTestifyConfig.exclusionRectProvider,
                pauseForInspection = androidTestifyConfig.pauseForInspection,
                compareMethod = androidTestifyConfig.compareMethod,
            ),
            enableReporter = androidTestifyConfig.enableReporter,
        )
    }

    // Use only for the context and inflate methods, because for ScreenshotRule it would crash...
    private val activityScenarioRule by lazy {
        ActivityScenarioForComposableRule(
            backgroundColor = androidTestifyConfig.backgroundColor,
            config = config.toComposableConfig()
        )
    }

    private var androidTestifyConfig: AndroidTestifyConfig = AndroidTestifyConfig()

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        when (androidTestifyConfig.animationsDisabled) {
            true -> RuleChain
                .outerRule(DisableAnimationsRule())
                .around(activityScenarioRule)
                .around(screenshotRule)
                .apply(base, description)
            false -> RuleChain
                .outerRule(activityScenarioRule)
                .around(screenshotRule)
                .apply(base, description)
        }

    override fun snapshot(composable: @Composable () -> Unit) {
        screenshotRule
            .withScenario(activityScenarioRule.activityScenario)
            .setScreenshotViewProvider {
                activityScenarioRule.setContent { composable() }.composeView
            }
            .setBitmapCaptureMethod(androidTestifyConfig.bitmapCaptureMethod)
            .generateDiffs(androidTestifyConfig.generateDiffs)
            .waitForIdleSync()
            .assertSame(null)
    }

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        screenshotRule
            .withScenario(activityScenarioRule.activityScenario)
            .setScreenshotViewProvider {
                activityScenarioRule.setContent { composable() }.composeView
            }
            .setBitmapCaptureMethod(androidTestifyConfig.bitmapCaptureMethod)
            .generateDiffs(androidTestifyConfig.generateDiffs)
            .waitForIdleSync()
            .assertSame(name = name)
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForComposable {
        if (config is AndroidTestifyConfig) {
            androidTestifyConfig = config
        }
        return this
    }
}
