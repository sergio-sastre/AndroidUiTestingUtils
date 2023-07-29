package sergio.sastre.uitesting.utils.crosslibrary.testrules

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.junit.rules.TestWatcher
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView

abstract class ScreenshotTestRuleForView(
    open val config: ScreenshotConfigForView,
): TestWatcher() {
    abstract val context: Context
    abstract fun inflate(@LayoutRes layoutId: Int): View

    abstract fun waitForMeasuredView(actionToDo: () -> View): View
    abstract fun waitForMeasuredDialog(actionToDo: () -> Dialog): Dialog
    abstract fun waitForMeasuredViewHolder(actionToDo: () -> ViewHolder): ViewHolder

    abstract fun snapshotDialog(name: String? = null, dialog: Dialog)
    abstract fun snapshotView(name: String? = null, view: View)
    abstract fun snapshotViewHolder(name: String? = null, viewHolder: ViewHolder)

    abstract fun configure(config: LibraryConfig): ScreenshotTestRuleForView
}
