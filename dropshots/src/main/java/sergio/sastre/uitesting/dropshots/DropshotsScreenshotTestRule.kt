package sergio.sastre.uitesting.dropshots

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.drawToBitmap
import androidx.test.core.app.ActivityScenario
import com.dropbox.dropshots.Dropshots
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfig
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRule
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation
import sergio.sastre.uitesting.utils.utils.waitForActivity

class DropshotsScreenshotTestRule(
    override val config: ScreenshotConfig = ScreenshotConfig(),
) : ScreenshotTestRule(config) {

    private val activityScenario: ActivityScenario<out ComponentActivity> by lazy {
        ActivityScenarioConfigurator.ForComposable()
            .setLocale(config.locale)
            .setInitialOrientation(config.orientation)
            .setUiMode(config.uiMode)
            .setFontSize(config.fontScale)
            .setDisplaySize(config.displaySize)
            .launchConfiguredActivity(dropshotsConfig.backgroundColor)
    }

    private val dropshotsRule: DropshotsAPI29Fix by lazy {
        DropshotsAPI29Fix(
            Dropshots(
                resultValidator = dropshotsConfig.resultValidator,
                imageComparator = dropshotsConfig.imageComparator,
                recordScreenshots = dropshotsConfig.recordScreenshots,
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

    override fun apply(base: Statement, description: Description): Statement =
        dropshotsRule.apply(base, description)

    private fun takeSnapshot(name: String?, composable: @Composable () -> Unit) {
        val activity =
            activityScenario
                .onActivity {
                    it.setContent {
                        composable.invoke()
                    }
                }.waitForActivity()

        val existingComposeView =
            activity.window.decorView
                .findViewById<ViewGroup>(android.R.id.content)
                .getChildAt(0) as ComposeView

        when (val bitmapCaptureMethod = dropshotsConfig.bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                takeSnapshotWithCanvas(bitmapCaptureMethod.config, existingComposeView, name)
            is BitmapCaptureMethod.PixelCopy ->
                takeSnapshotWithPixelCopy(bitmapCaptureMethod.config, existingComposeView, name)
            null -> takeSnapshotOfView(existingComposeView, name)
        }
    }

    private fun takeSnapshotOfView(view: View, name: String?) {
        dropshotsRule.assertSnapshot(
            view = view,
            name = name.orEmpty(),
        )
    }

    private fun takeSnapshotWithPixelCopy(bitmapConfig: Bitmap.Config, view: View, name: String?) {
        dropshotsRule.assertSnapshot(
            bitmap = view.drawToBitmapWithElevation(config = bitmapConfig),
            name = name.orEmpty(),
        )
    }

    private fun takeSnapshotWithCanvas(bitmapConfig: Bitmap.Config, view: View, name: String?) {
        dropshotsRule.assertSnapshot(
            bitmap = view.drawToBitmap(config = bitmapConfig),
            name = name.orEmpty(),
        )
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRule = apply {
        if (config is DropshotsConfig) {
            dropshotsConfig = config
        }
    }

    override fun finished(description: Description?) {
        super.finished(description)
        activityScenario.close()
    }
}
