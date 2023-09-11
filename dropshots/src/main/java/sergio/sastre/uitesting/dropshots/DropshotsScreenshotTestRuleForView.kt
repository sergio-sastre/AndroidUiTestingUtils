package sergio.sastre.uitesting.dropshots

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dropbox.dropshots.Dropshots
import org.junit.rules.RuleChain
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForViewRule
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView
import sergio.sastre.uitesting.utils.utils.waitForMeasuredView as androidUiTestingUtilsMeasuredView
import sergio.sastre.uitesting.utils.utils.waitForMeasuredViewHolder as androidUiTestingUtilsMeasuredViewHolder
import sergio.sastre.uitesting.utils.utils.waitForMeasuredDialog as androidUiTestingUtilsMeasuredDialog

class DropshotsScreenshotTestRuleForView(
    override val config: ScreenshotConfigForView = ScreenshotConfigForView(),
) : ScreenshotTestRuleForView(config) {

    private val activityScenarioForViewRule by lazy {
        ActivityScenarioForViewRule(
            config = config.toViewConfig(),
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

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        RuleChain
            .outerRule(dropshotsRule)
            .around(activityScenarioForViewRule)
            .apply(base, description)

    private val Dialog.view
        get() = window!!.decorView

    override val context: Context
        get() = activityScenarioForViewRule.activity

    override fun inflate(layoutId: Int): View =
        activityScenarioForViewRule.inflateAndWaitForIdle(layoutId)

    override fun waitForMeasuredView(actionToDo: () -> View): View =
        androidUiTestingUtilsMeasuredView { actionToDo() }

    override fun waitForMeasuredDialog(actionToDo: () -> Dialog): Dialog =
        androidUiTestingUtilsMeasuredDialog { actionToDo() }

    override fun waitForMeasuredViewHolder(actionToDo: () -> ViewHolder): ViewHolder =
        androidUiTestingUtilsMeasuredViewHolder { actionToDo() }

    override fun snapshotDialog(name: String?, dialog: Dialog) {
        dropshotsRule.assertSnapshot(
            dialog = dialog,
            bitmapCaptureMethod = dropshotsConfig.bitmapCaptureMethod,
            name = name,
            filePath = dropshotsConfig.filePath,
        )
    }

    override fun snapshotView(name: String?, view: View) {
        dropshotsRule.assertSnapshot(
            view = view,
            bitmapCaptureMethod = dropshotsConfig.bitmapCaptureMethod,
            name = name,
            filePath = dropshotsConfig.filePath,
        )
    }

    override fun snapshotViewHolder(name: String?, viewHolder: ViewHolder) {
        dropshotsRule.assertSnapshot(
            view = viewHolder.itemView,
            bitmapCaptureMethod = dropshotsConfig.bitmapCaptureMethod,
            name = name,
            filePath = dropshotsConfig.filePath,
        )
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView = apply {
        if (config is DropshotsConfig) {
            dropshotsConfig = config
        }
    }
}
