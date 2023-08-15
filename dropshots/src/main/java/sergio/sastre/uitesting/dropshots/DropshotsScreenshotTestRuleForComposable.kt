package sergio.sastre.uitesting.dropshots

import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.view.drawToBitmap
import com.dropbox.dropshots.Dropshots
import org.junit.rules.RuleChain
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForComposableRule
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForComposable
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation

class DropshotsScreenshotTestRuleForComposable(
    override val config: ScreenshotConfigForComposable = ScreenshotConfigForComposable(),
) : ScreenshotTestRuleForComposable(config) {

    private val activityScenarioForComposableRule by lazy {
        ActivityScenarioForComposableRule(
            config = config.toComposableConfig(),
            backgroundColor = dropshotsConfig.backgroundColor,
        )
    }

    private val dropshotsRule: DropshotsAPI29Fix by lazy {
        DropshotsAPI29Fix(
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

        when (val bitmapCaptureMethod = dropshotsConfig.bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                takeSnapshotWithCanvas(bitmapCaptureMethod.config, composeView, name)
            is BitmapCaptureMethod.PixelCopy ->
                takeSnapshotWithPixelCopy(bitmapCaptureMethod.config, composeView, name)
            null -> takeSnapshotOfView(composeView, name)
        }
    }

    private fun takeSnapshotOfView(view: View, name: String?) {
        dropshotsRule.assertSnapshot(
            view = view,
            name = name ?: view::class.java.name,
        )
    }

    private fun takeSnapshotWithPixelCopy(bitmapConfig: Bitmap.Config, view: View, name: String?) {
        dropshotsRule.assertSnapshot(
            bitmap = view.drawToBitmapWithElevation(config = bitmapConfig),
            name = name ?: view::class.java.name,
        )
    }

    private fun takeSnapshotWithCanvas(bitmapConfig: Bitmap.Config, view: View, name: String?) {
        dropshotsRule.assertSnapshot(
            bitmap = view.drawToBitmap(config = bitmapConfig),
            name = name ?: view::class.java.name,
        )
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForComposable = apply {
        if (config is DropshotsConfig) {
            dropshotsConfig = config
        }
    }
}
