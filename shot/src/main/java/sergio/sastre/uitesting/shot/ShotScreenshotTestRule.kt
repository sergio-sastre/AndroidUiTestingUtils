package sergio.sastre.uitesting.shot

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.drawToBitmap
import com.karumi.shot.ScreenshotTest
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.ScreenshotTestRule
import sergio.sastre.uitesting.utils.LibraryConfig
import sergio.sastre.uitesting.utils.ScreenshotConfig
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation
import sergio.sastre.uitesting.utils.utils.waitForActivity

class ShotScreenshotTestRule(
    private val screenshotConfig: ScreenshotConfig = ScreenshotConfig(),
) : ScreenshotTestRule, ScreenshotTest {

    private val activityScenario = ActivityScenarioForComposableTestWatcher(screenshotConfig)

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
        val activity =
            activityScenario.scenario
                .onActivity {
                    it.setContent {
                        composable.invoke()
                    }
                }.waitForActivity()

        val existingComposeView =
            activity.window.decorView
                .findViewById<ViewGroup>(android.R.id.content)
                .getChildAt(0) as ComposeView

        when(val bitmapCaptureMethod = shotConfig.bitmapCaptureMethod){
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

    private fun takeSnapshotWithPixelCopy(bitmapConfig: Bitmap.Config, view: View, name: String?) {
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
