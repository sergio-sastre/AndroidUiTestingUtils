package sergio.sastre.uitesting.shot

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.view.drawToBitmap
import com.karumi.shot.ScreenshotTest
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForComposableRule
import sergio.sastre.uitesting.utils.activityscenario.ComposableConfigItem
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation

class ShotScreenshotTestRuleForComposable(
    override val config: ScreenshotConfigForComposable,
) : ScreenshotTestRuleForComposable(config), ScreenshotTest {

    override val ignoredViews: List<Int>
        get() = shotConfig.ignoredViews

    override fun prepareUIForScreenshot() {
        shotConfig.prepareUIForScreenshot()
    }

    private val activityScenarioRule: ActivityScenarioForComposableRule by lazy {
        ActivityScenarioForComposableRule(
            config = config.toComposableConfig(),
            backgroundColor = shotConfig.backgroundColor,
        )
    }

    private var shotConfig: ShotConfig = ShotConfig()

    override fun snapshot(composable: @Composable () -> Unit) {
        takeSnapshot(null, composable)
    }

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        takeSnapshot(name, composable)
    }

    override fun apply(base: Statement?, description: Description?): Statement =
        activityScenarioRule.apply(base, description)

    private fun takeSnapshot(name: String?, composable: @Composable () -> Unit) {
        val composeView =
            activityScenarioRule
                .setContent(composable)
                .composeView

        when (val bitmapCaptureMethod = shotConfig.bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                takeSnapshotWithCanvas(bitmapCaptureMethod.config, composeView, name)
            is BitmapCaptureMethod.PixelCopy ->
                takeSnapshotWithPixelCopy(bitmapCaptureMethod.config, composeView, name)
            null -> takeSnapshotWithComposeRuleIfPossible(composeView, name)
        }
    }

    private fun takeSnapshotWithComposeRuleIfPossible(
        view: View,
        name: String?,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            compareScreenshot(
                rule = activityScenarioRule.composeRule,
                name = name,
            )
        } else {
            takeSnapshotWithCanvas(Bitmap.Config.ARGB_8888, view, name)
        }
    }

    private fun takeSnapshotWithPixelCopy(
        bitmapConfig: Bitmap.Config,
        view: View,
        name: String?
    ) {
        compareScreenshot(
            bitmap = view.drawToBitmapWithElevation(config = bitmapConfig),
            name = name,
        )
    }

    private fun takeSnapshotWithCanvas(bitmapConfig: Bitmap.Config, view: View, name: String?) {
        compareScreenshot(
            bitmap = view.drawToBitmap(config = bitmapConfig),
            name = name,
        )
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForComposable = apply {
        if (config is ShotConfig) {
            shotConfig = config
        }
    }
}
