package sergio.sastre.uitesting.shot

import android.graphics.Bitmap
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.drawToBitmap
import com.karumi.shot.ScreenshotTest
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfig
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForComposableRule
import sergio.sastre.uitesting.utils.activityscenario.ComposableConfigItem
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRule
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation
import sergio.sastre.uitesting.utils.utils.waitForActivity
import sergio.sastre.uitesting.utils.utils.waitForComposeView

class ShotScreenshotTestRule(
    override val config: ScreenshotConfig,
) : ScreenshotTestRule(config), ScreenshotTest {

    private val activityScenario = ActivityScenarioForComposableRule(
        config = ComposableConfigItem(
            orientation = config.orientation,
            uiMode = config.uiMode,
            locale = config.locale,
            fontSize = config.fontScale,
        )
    )

    private var shotConfig: ShotConfig = ShotConfig()

    override fun snapshot(composable: @Composable () -> Unit) {
        takeSnapshot(null, composable)
    }

    override fun snapshot(name: String?, composable: @Composable () -> Unit) {
        takeSnapshot(name, composable)
    }

    override fun apply(base: Statement?, description: Description?): Statement =
        activityScenario.apply(base, description)

    private fun takeSnapshot(name: String?, composable: @Composable () -> Unit) {
        val existingComposeView =
            activityScenario.activityScenario
                .onActivity {
                    it.setContent {
                        composable.invoke()
                    }
                }
                .waitForActivity()
                .waitForComposeView()

        when (val bitmapCaptureMethod = shotConfig.bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                takeSnapshotWithCanvas(bitmapCaptureMethod.config, existingComposeView, name)
            is BitmapCaptureMethod.PixelCopy ->
                takeSnapshotWithPixelCopy(bitmapCaptureMethod.config, existingComposeView, name)
            null -> takeSnapshotOfView(existingComposeView, name)
        }
    }

    private fun takeSnapshotOfView(view: View, name: String?) {
        compareScreenshot(
            view = view,
            heightInPx = view.measuredHeight,
            widthInPx = view.measuredWidth,
            name = name,
        )
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

    override fun configure(config: LibraryConfig): ScreenshotTestRule = apply {
        if (config is ShotConfig) {
            shotConfig = config
        }
    }
}