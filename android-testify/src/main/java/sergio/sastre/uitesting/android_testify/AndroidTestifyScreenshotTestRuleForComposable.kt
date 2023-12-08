package sergio.sastre.uitesting.android_testify

import androidx.compose.runtime.Composable
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable

class AndroidTestifyScreenshotTestRuleForComposable(
    override val config: ScreenshotConfigForComposable = ScreenshotConfigForComposable(),
) : ScreenshotTestRuleForComposable(config) {

    private val screenshotRule: ComposableScreenshotRuleWithConfiguration by lazy {
        ComposableScreenshotRuleWithConfiguration(
            exactness = androidTestifyConfig.exactness,
            activityBackgroundColor = androidTestifyConfig.backgroundColor,
            enableReporter = androidTestifyConfig.enableReporter,
            initialTouchMode = androidTestifyConfig.initialTouchMode,
            config = config.toComposableConfig(),
        )
    }

    private var androidTestifyConfig: AndroidTestifyConfig = AndroidTestifyConfig()

    override fun apply(base: Statement, description: Description): Statement {
        return screenshotRule.apply(base, description)
    }

    override fun snapshot(composable: @Composable () -> Unit) {
        screenshotRule
            .setCompose { composable() }
            .setBitmapCaptureMethod(androidTestifyConfig.bitmapCaptureMethod)
            .generateDiffs(androidTestifyConfig.generateDiffs)
            .assertSame()
    }

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        screenshotRule
            .setCompose { composable() }
            .setBitmapCaptureMethod(androidTestifyConfig.bitmapCaptureMethod)
            .generateDiffs(androidTestifyConfig.generateDiffs)
            .assertSame(name = name)
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForComposable {
        if (config is AndroidTestifyConfig){
            androidTestifyConfig = config
        }
        return this
    }
}
