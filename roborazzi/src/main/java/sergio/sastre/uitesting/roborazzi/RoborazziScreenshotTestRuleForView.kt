package sergio.sastre.uitesting.roborazzi

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioForViewRule
import sergio.sastre.uitesting.roborazzi.config.RoborazziSharedTestAdapter
import sergio.sastre.uitesting.sharedtest.roborazzi.RoborazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView
import sergio.sastre.uitesting.utils.utils.drawToBitmap

class RoborazziScreenshotTestRuleForView(
    override val config: ScreenshotConfigForView = ScreenshotConfigForView(),
) : ScreenshotTestRuleForView(config) {

    private val activityScenarioRule: RobolectricActivityScenarioForViewRule by lazy {
        RobolectricActivityScenarioForViewRule(
            config = config.toViewConfig(),
            deviceScreen = roborazziAdapter.asDeviceScreen(),
            backgroundColor = roborazziConfig.backgroundColor,
        )
    }

    private val roborazziAdapter: RoborazziSharedTestAdapter by lazy {
        RoborazziSharedTestAdapter(roborazziConfig)
    }

    private var roborazziConfig: RoborazziConfig = RoborazziConfig()

    override fun apply(base: Statement?, description: Description?): Statement =
        activityScenarioRule.apply(base, description)

    override val context: Context
        get() = activityScenarioRule.activity

    override fun inflate(layoutId: Int): View =
        activityScenarioRule.inflateAndWaitForIdle(layoutId)

    override fun waitForMeasuredView(actionToDo: () -> View): View =
        sergio.sastre.uitesting.utils.utils.waitForMeasuredView { actionToDo() }

    override fun waitForMeasuredDialog(actionToDo: () -> Dialog): Dialog =
        sergio.sastre.uitesting.utils.utils.waitForMeasuredDialog { actionToDo() }

    override fun waitForMeasuredViewHolder(actionToDo: () -> RecyclerView.ViewHolder): RecyclerView.ViewHolder =
        sergio.sastre.uitesting.utils.utils.waitForMeasuredViewHolder { actionToDo() }

    override fun snapshotDialog(name: String?, dialog: Dialog) {
        dialog
            .drawToBitmap()
            .captureRoboImage(
                filePath = "${roborazziConfig.filePath}$name.png",
                roborazziOptions = roborazziAdapter.asRoborazziOptions(),
            )
    }

    override fun snapshotView(name: String?, view: View) {
        view
            .captureRoboImage(
                filePath = "${roborazziConfig.filePath}$name.png",
                roborazziOptions = roborazziAdapter.asRoborazziOptions(),
            )
    }

    override fun snapshotViewHolder(name: String?, viewHolder: RecyclerView.ViewHolder) {
        viewHolder
            .itemView
            .captureRoboImage(
                filePath = "${roborazziConfig.filePath}$name.png",
                roborazziOptions = roborazziAdapter.asRoborazziOptions(),
            )
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView = apply {
        if (config is RoborazziConfig) {
            roborazziConfig = config
        }
    }
}
