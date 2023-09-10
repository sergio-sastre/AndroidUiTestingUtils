package sergio.sastre.uitesting.shot

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.karumi.shot.ScreenshotTest
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForViewRule
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView
import sergio.sastre.uitesting.utils.utils.waitForMeasuredView as androidUiTestingUtilsMeasuredView
import sergio.sastre.uitesting.utils.utils.waitForMeasuredViewHolder as androidUiTestingUtilsMeasuredViewHolder
import sergio.sastre.uitesting.utils.utils.waitForMeasuredDialog as androidUiTestingUtilsMeasuredDialog

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
        ScreenshotTaker(this).compareSnapshot(
            dialog = dialog,
            bitmapCaptureMethod = shotConfig.bitmapCaptureMethod,
            name = name,
        )
    }

    override fun snapshotView(name: String?, view: View) {
        ScreenshotTaker(this).compareSnapshot(
            view = view,
            bitmapCaptureMethod = shotConfig.bitmapCaptureMethod,
            name = name,
        )
    }

    override fun snapshotViewHolder(name: String?, viewHolder: ViewHolder) {
        ScreenshotTaker(this).compareSnapshot(
            viewHolder = viewHolder,
            bitmapCaptureMethod = shotConfig.bitmapCaptureMethod,
            name = name,
        )
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView = apply {
        if (config is ShotConfig) {
            shotConfig = config
        }
    }
}
