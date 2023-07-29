package sergio.sastre.uitesting.paparazzi

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.cash.paparazzi.Paparazzi
import sergio.sastre.uitesting.paparazzi.config.PaparazziTestRuleGenerator
import sergio.sastre.uitesting.sharedtest.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView

class PaparazziScreenshotTestRuleForView(
    override val config: ScreenshotConfigForView = ScreenshotConfigForView(),
) : ScreenshotTestRuleForView(config) {

    private var paparazziConfig = PaparazziConfig()

    private val paparazziTestRule: Paparazzi by lazy {
        PaparazziTestRuleGenerator(config, paparazziConfig).generatePaparazziTestRule()
    }

    override val context: Context
        get() = TODO("Not yet implemented")

    override fun inflate(layoutId: Int): View {
        TODO("Not yet implemented")
    }

    override fun waitForMeasuredView(actionToDo: () -> View): View {
        TODO("Not yet implemented")
    }

    override fun waitForMeasuredDialog(actionToDo: () -> Dialog): Dialog {
        TODO("Not yet implemented")
    }

    override fun waitForMeasuredViewHolder(actionToDo: () -> RecyclerView.ViewHolder): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun snapshotDialog(name: String?, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun snapshotView(name: String?, view: View) {
        TODO("Not yet implemented")
    }

    override fun snapshotViewHolder(name: String?, viewHolder: RecyclerView.ViewHolder) {
        TODO("Not yet implemented")
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView {
        TODO("Not yet implemented")
    }
}