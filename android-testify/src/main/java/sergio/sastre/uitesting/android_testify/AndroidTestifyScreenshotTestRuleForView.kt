package sergio.sastre.uitesting.android_testify

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.junit.rules.RuleChain
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForViewRule
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView
import sergio.sastre.uitesting.utils.testrules.animations.DisableAnimationsRule
import sergio.sastre.uitesting.utils.utils.inflateAndWaitForIdle
import sergio.sastre.uitesting.utils.utils.waitForMeasuredView as androidUiTestingUtilsMeasuredView
import sergio.sastre.uitesting.utils.utils.waitForMeasuredViewHolder as androidUiTestingUtilsMeasuredViewHolder
import sergio.sastre.uitesting.utils.utils.waitForMeasuredDialog as androidUiTestingUtilsMeasuredDialog

class AndroidTestifyScreenshotTestRuleForView(
    override val config: ScreenshotConfigForView = ScreenshotConfigForView(),
) : ScreenshotTestRuleForView(config) {

    private var androidTestifyConfig: AndroidTestifyConfig = AndroidTestifyConfig()

    private val screenshotRule: ScreenshotRuleWithConfigurationForView by lazy {
        ScreenshotRuleWithConfigurationForView(
            exactness = androidTestifyConfig.exactness,
            activityBackgroundColor = androidTestifyConfig.backgroundColor,
            enableReporter = androidTestifyConfig.enableReporter,
            initialTouchMode = androidTestifyConfig.initialTouchMode,
            config = config.toViewConfig()
        )
    }

    // Use only for the context and inflate methods, because for ScreenshotRule it would crash...
    private val activityScenarioRule by lazy {
        ActivityScenarioForViewRule(
            backgroundColor = androidTestifyConfig.backgroundColor,
            config = config.toViewConfig()
        )
    }

    override val context: Context
        get() = activityScenarioRule.activity

    private var viewToScreenshot: View? = null

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        when (androidTestifyConfig.animationsDisabled) {
            true -> RuleChain
                .outerRule(DisableAnimationsRule())
                .around(activityScenarioRule)
                .around(screenshotRule)
                .apply(base, description)
            false -> RuleChain
                .outerRule(activityScenarioRule)
                .around(screenshotRule)
                .apply(base, description)
        }

    override fun inflate(layoutId: Int): View =
        activityScenarioRule.activity.inflateAndWaitForIdle(layoutId)

    override fun waitForMeasuredView(actionToDo: () -> View): View {
        viewToScreenshot = androidUiTestingUtilsMeasuredView { actionToDo() }
        return androidUiTestingUtilsMeasuredView { actionToDo() }
    }

    override fun waitForMeasuredDialog(actionToDo: () -> Dialog): Dialog {
        viewToScreenshot = androidUiTestingUtilsMeasuredView { actionToDo().window!!.decorView }
        return androidUiTestingUtilsMeasuredDialog { actionToDo() }
    }

    override fun waitForMeasuredViewHolder(actionToDo: () -> RecyclerView.ViewHolder): RecyclerView.ViewHolder {
        viewToScreenshot = androidUiTestingUtilsMeasuredView { actionToDo().itemView }
        return androidUiTestingUtilsMeasuredViewHolder { actionToDo() }
    }

    override fun snapshotDialog(name: String?, dialog: Dialog) {
        takeSnapshot(name = name)
    }

    override fun snapshotView(name: String?, view: View) {
        takeSnapshot(name = name)
    }

    override fun snapshotViewHolder(name: String?, viewHolder: RecyclerView.ViewHolder) {
        takeSnapshot(name = name)
    }

    private fun takeSnapshot(name:String?){
        screenshotRule
            .setViewUnderTest(viewToScreenshot!!)
            .setBitmapCaptureMethod(androidTestifyConfig.bitmapCaptureMethod)
            .generateDiffs(androidTestifyConfig.generateDiffs)
            .assertSame(name = name)
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView = apply {
        if (config is AndroidTestifyConfig) {
            androidTestifyConfig = config
        }
    }

    override fun finished(description: Description?) {
        super.finished(description)
        viewToScreenshot = null
    }
}
