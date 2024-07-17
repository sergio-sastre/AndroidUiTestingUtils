package sergio.sastre.uitesting.roborazzi

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry.*
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioForViewRule
import sergio.sastre.uitesting.roborazzi.config.RoborazziSharedTestAdapter
import sergio.sastre.uitesting.mapper.roborazzi.RoborazziConfig
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.CaptureType
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView

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

    private val filePathGenerator: FilePathGenerator = FilePathGenerator()

    private var dumpDialogView: View? = null

    override fun apply(base: Statement?, description: Description?): Statement =
        activityScenarioRule.apply(base, description)

    override val context: Context
        get() = activityScenarioRule.activity

    override fun inflate(layoutId: Int): View =
        activityScenarioRule.inflateAndWaitForIdle(layoutId)

    override fun waitForMeasuredView(actionToDo: () -> View): View =
        sergio.sastre.uitesting.utils.utils.waitForMeasuredView { actionToDo() }

    override fun waitForMeasuredDialog(actionToDo: () -> Dialog): Dialog {
        dumpDialogView = actionToDo().window!!.decorView
        return sergio.sastre.uitesting.utils.utils.waitForMeasuredDialog { actionToDo() }
    }

    override fun waitForMeasuredViewHolder(actionToDo: () -> RecyclerView.ViewHolder): RecyclerView.ViewHolder =
        sergio.sastre.uitesting.utils.utils.waitForMeasuredViewHolder { actionToDo() }

    override fun snapshotDialog(name: String?, dialog: Dialog) {
        requireNotNull(dumpDialogView).run {
            (activityScenarioRule.activity.window.decorView as ViewGroup).addView(this)
            getInstrumentation().waitForIdleSync()
            snapshotView(name, waitForMeasuredView { this })
        }.also {
            dumpDialogView = null
        }
    }

    override fun snapshotView(name: String?, view: View) {
        @OptIn(ExperimentalRoborazziApi::class)
        when (roborazziConfig.roborazziOptions.captureType is CaptureType.Dump) {
            // Dump does not support Bitmap.captureRoboImage
            true -> view
                .captureRoboImage(
                    filePath = filePathGenerator.invoke(roborazziConfig.filePath, name),
                    roborazziOptions = roborazziAdapter.asRoborazziOptions(),
                )

            false -> view
                .drawToBitmap(roborazziConfig.bitmapCaptureMethod)
                .captureRoboImage(
                    filePath = filePathGenerator.invoke(roborazziConfig.filePath, name),
                    roborazziOptions = roborazziAdapter.asRoborazziOptions(),
                )
        }
    }

    override fun snapshotViewHolder(name: String?, viewHolder: RecyclerView.ViewHolder) {
        snapshotView(name, viewHolder.itemView)
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView = apply {
        if (config is RoborazziConfig) {
            roborazziConfig = config
        }
    }
}
