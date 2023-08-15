package sergio.sastre.uitesting.utils.crosslibrary.testrules.implementations

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.providers.ScreenshotLibraryTestRuleForViewProvider

abstract class ScreenshotLibraryTestRuleForView (
    override val config: ScreenshotConfigForView,
) : ScreenshotTestRuleForView(config), ScreenshotLibraryTestRuleForViewProvider {

    private val factory: ScreenshotTestRuleForView by lazy {
        getScreenshotLibraryTestRule(config)
    }

    abstract fun getScreenshotLibraryTestRule(config: ScreenshotConfigForView): ScreenshotTestRuleForView

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
