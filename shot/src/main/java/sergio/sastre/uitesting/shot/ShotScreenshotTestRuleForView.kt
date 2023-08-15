package sergio.sastre.uitesting.shot

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.karumi.shot.ScreenshotTest
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForViewRule
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView
import sergio.sastre.uitesting.utils.utils.drawToBitmap
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation

class ShotScreenshotTestRuleForView(
    override val config: ScreenshotConfigForView = ScreenshotConfigForView(),
) : ScreenshotTestRuleForView(config), ScreenshotTest {

    private val activityScenarioForViewRule by lazy {
        ActivityScenarioForViewRule(
            config = config.toViewConfig(),
            backgroundColor = shotConfig.backgroundColor,
        )
    }

    private var shotConfig: ShotConfig = ShotConfig()

    override fun apply(base: Statement?, description: Description?): Statement =
        activityScenarioForViewRule.apply(base, description)

    private fun takeSnapshot(name: String?, view: View) {
        when (val bitmapCaptureMethod = shotConfig.bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                takeSnapshotWithCanvas(bitmapCaptureMethod.config, view, name)
            is BitmapCaptureMethod.PixelCopy ->
                takeSnapshotWithPixelCopy(bitmapCaptureMethod.config, view, name)
            null -> takeSnapshotOfView(view, name)
        }
    }

    private fun takeSnapshot(name: String?, dialog: Dialog) {
        when (val bitmapCaptureMethod = shotConfig.bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                takeSnapshotWithCanvas(bitmapCaptureMethod.config, dialog, name)
            is BitmapCaptureMethod.PixelCopy ->
                takeSnapshotWithPixelCopy(bitmapCaptureMethod.config, dialog, name)
            null -> takeSnapshotOfDialog(dialog, name)
        }
    }

    private fun takeSnapshot(name: String?, viewHolder: ViewHolder) {
        when (val bitmapCaptureMethod = shotConfig.bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                takeSnapshotWithCanvas(bitmapCaptureMethod.config, viewHolder.itemView, name)
            is BitmapCaptureMethod.PixelCopy ->
                takeSnapshotWithPixelCopy(bitmapCaptureMethod.config, viewHolder.itemView, name)
            null -> takeSnapshotOfViewHolder(viewHolder, name)
        }
    }

    private fun takeSnapshotOfView(view: View, name: String?) {
        compareScreenshot(
            view = view,
            name = name ?: view::class.java.name,
        )
    }

    private fun takeSnapshotOfDialog(dialog: Dialog, name: String?) {
        compareScreenshot(
            dialog = dialog,
            name = name ?: dialog::class.java.name,
        )
    }

    private fun takeSnapshotOfViewHolder(viewHolder: ViewHolder, name: String?) {
        compareScreenshot(
            holder = viewHolder,
            heightInPx = viewHolder.itemView.measuredHeight,
            name = name ?: viewHolder::class.java.name,
        )
    }

    private fun takeSnapshotWithPixelCopy(bitmapConfig: Bitmap.Config, view: View, name: String?) {
        compareScreenshot(
            bitmap = view.drawToBitmapWithElevation(config = bitmapConfig),
            name = name ?: view::class.java.name,
        )
    }

    private fun takeSnapshotWithPixelCopy(
        bitmapConfig: Bitmap.Config,
        dialog: Dialog,
        name: String?
    ) {
        compareScreenshot(
            bitmap = dialog.drawToBitmapWithElevation(config = bitmapConfig),
            name = name ?: dialog::class.java.name,
        )
    }

    private fun takeSnapshotWithCanvas(bitmapConfig: Bitmap.Config, dialog: Dialog, name: String?) {
        compareScreenshot(
            bitmap = dialog.drawToBitmap(config = bitmapConfig),
            name = name ?: dialog::class.java.name,
        )
    }

    private fun takeSnapshotWithCanvas(bitmapConfig: Bitmap.Config, view: View, name: String?) {
        compareScreenshot(
            bitmap = view.drawToBitmap(config = bitmapConfig),
            name = name ?: view::class.java.name,
        )
    }

    override val context: Context
        get() = activityScenarioForViewRule.activity

    override fun inflate(layoutId: Int): View =
        activityScenarioForViewRule.inflateAndWaitForIdle(layoutId)


    override fun waitForMeasuredView(actionToDo: () -> View): View =
        sergio.sastre.uitesting.utils.utils.waitForMeasuredView {
            actionToDo()
        }

    override fun waitForMeasuredDialog(actionToDo: () -> Dialog): Dialog =
        sergio.sastre.uitesting.utils.utils.waitForMeasuredDialog {
            actionToDo()
        }

    override fun waitForMeasuredViewHolder(actionToDo: () -> ViewHolder): ViewHolder =
        sergio.sastre.uitesting.utils.utils.waitForMeasuredViewHolder {
            actionToDo()
        }

    override fun snapshotDialog(name: String?, dialog: Dialog) {
        takeSnapshot(name, dialog)
    }

    override fun snapshotView(name: String?, view: View) {
        takeSnapshot(name, view)
    }

    override fun snapshotViewHolder(name: String?, viewHolder: ViewHolder) {
        takeSnapshot(name, viewHolder)
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView = apply {
        if (config is ShotConfig) {
            shotConfig = config
        }
    }
}
