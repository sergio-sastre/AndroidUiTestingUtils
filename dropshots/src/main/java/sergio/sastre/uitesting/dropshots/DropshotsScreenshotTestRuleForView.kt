package sergio.sastre.uitesting.dropshots

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dropbox.dropshots.Dropshots
import org.junit.rules.RuleChain
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForViewRule
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView
import sergio.sastre.uitesting.utils.utils.drawToBitmap
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation

class DropshotsScreenshotTestRuleForView(
    override val config: ScreenshotConfigForView = ScreenshotConfigForView(),
) : ScreenshotTestRuleForView(config) {

    private val activityScenarioForViewRule by lazy {
        ActivityScenarioForViewRule(
            config = config.toViewConfig(),
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

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        RuleChain
            .outerRule(dropshotsRule)
            .around(activityScenarioForViewRule)
            .apply(base, description)

    private fun takeSnapshot(name: String?, view: View) {
        when (val bitmapCaptureMethod = dropshotsConfig.bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                takeSnapshotWithCanvas(bitmapCaptureMethod.config, view, name)
            is BitmapCaptureMethod.PixelCopy ->
                takeSnapshotWithPixelCopy(bitmapCaptureMethod.config, view, name)
            null -> takeSnapshotOfView(view, name)
        }
    }

    private fun takeSnapshot(name: String?, dialog: Dialog) {
        when (val bitmapCaptureMethod = dropshotsConfig.bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                takeSnapshotWithCanvas(bitmapCaptureMethod.config, dialog, name)
            is BitmapCaptureMethod.PixelCopy ->
                takeSnapshotWithPixelCopy(bitmapCaptureMethod.config, dialog, name)
            null -> takeSnapshotOfView(dialog.window!!.decorView, name)
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

    private fun takeSnapshotWithPixelCopy(bitmapConfig: Bitmap.Config, dialog: Dialog, name: String?) {
        dropshotsRule.assertSnapshot(
            bitmap = dialog.drawToBitmapWithElevation(config = bitmapConfig),
            name = name ?: dialog::class.java.name,
        )
    }

    private fun takeSnapshotWithCanvas(bitmapConfig: Bitmap.Config, dialog: Dialog, name: String?) {
        dropshotsRule.assertSnapshot(
            bitmap = dialog.drawToBitmap(config = bitmapConfig),
            name = name ?: dialog::class.java.name,
        )
    }

    private fun takeSnapshotWithCanvas(bitmapConfig: Bitmap.Config, view: View, name: String?) {
        dropshotsRule.assertSnapshot(
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
        takeSnapshot(name, viewHolder.itemView)
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView = apply {
        if (config is DropshotsConfig) {
            dropshotsConfig = config
        }
    }
}
