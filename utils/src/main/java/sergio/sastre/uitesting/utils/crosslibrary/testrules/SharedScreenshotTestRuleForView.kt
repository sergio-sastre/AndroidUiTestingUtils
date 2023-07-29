package sergio.sastre.uitesting.utils.crosslibrary.testrules

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import java.util.*

abstract class SharedScreenshotTestRuleForView (
    override val config: ScreenshotConfigForView,
) : ScreenshotTestRuleForView(config) {

    companion object ScreenshotTestRuleClassPath {
        const val PAPARAZZI = "sergio.sastre.uitesting.paparazzi.PaparazziScreenshotTestRuleForView";
        const val SHOT = "sergio.sastre.uitesting.shot.ShotScreenshotTestRuleForView";
        const val DROPSHOTS = "sergio.sastre.uitesting.dropshots.DropshotsScreenshotTestRuleForView";
        const val ROBORAZZI = "sergio.sastre.uitesting.roborazzi.RoborazziScreenshotTestRuleForView";
    }

    val dropshotsScreenshotTestRule
    get() = getScreenshotTestRuleClassForName(DROPSHOTS, config)

    val paparazziScreenshotTestRule
    get() = getScreenshotTestRuleClassForName(PAPARAZZI, config)

    val roborazziScreenshotTestRule
    get() = getScreenshotTestRuleClassForName(ROBORAZZI, config)

    val shotScreenshotTestRule
    get() = getScreenshotTestRuleClassForName(SHOT, config)

    fun getScreenshotTestRuleClassForName(
        className: String,
        config: ScreenshotConfigForView,
    ): ScreenshotTestRuleForView {
        return Class.forName(className)
            .getConstructor(ScreenshotConfigForView::class.java)
            .newInstance(config) as ScreenshotTestRuleForView
    }

    private val factory: ScreenshotTestRuleForView by lazy {
        if (isRunningOnJvm()) {
            getJvmScreenshotTestRule(config)
        } else {
            getInstrumentedScreenshotTestRule(config)
        }
    }

    abstract fun getJvmScreenshotTestRule(config: ScreenshotConfigForView): ScreenshotTestRuleForView

    abstract fun getInstrumentedScreenshotTestRule(config: ScreenshotConfigForView): ScreenshotTestRuleForView

    private fun isRunningOnJvm(): Boolean =
        System.getProperty("java.runtime.name")
            ?.lowercase(Locale.getDefault())
            ?.contains("android") != true

    override fun apply(base: Statement?, description: Description?): Statement {
        return factory.apply(base, description)
    }

    override val context: Context
        get() = factory.context

    override fun inflate(layoutId: Int): View =
        factory.inflate(layoutId)

    override fun waitForMeasuredView(actionToDo: () -> View): View =
        factory.waitForMeasuredView(actionToDo)

    override fun waitForMeasuredDialog(actionToDo: () -> Dialog): Dialog =
        factory.waitForMeasuredDialog(actionToDo)

    override fun waitForMeasuredViewHolder(actionToDo: () -> RecyclerView.ViewHolder): RecyclerView.ViewHolder =
        factory.waitForMeasuredViewHolder(actionToDo)

    override fun snapshotDialog(name: String?, dialog: Dialog) {
        factory.snapshotDialog(name, dialog)
    }

    override fun snapshotView(name: String?, view: View) {
        factory.snapshotView(name, view)
    }

    override fun snapshotViewHolder(name: String?, viewHolder: RecyclerView.ViewHolder) {
        factory.snapshotViewHolder(name, viewHolder)
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView {
        return factory.configure(config)
    }
}