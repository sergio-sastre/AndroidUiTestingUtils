package sergio.sastre.uitesting.paparazzi

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.cash.paparazzi.Paparazzi
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.paparazzi.config.PaparazziForViewTestRuleBuilder
import sergio.sastre.uitesting.mapper.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRuleForView

class PaparazziScreenshotTestRuleForView(
    override val config: ScreenshotConfigForView = ScreenshotConfigForView(),
) : ScreenshotTestRuleForView(config) {

    private var paparazziConfig = PaparazziConfig()

    private val paparazziTestRule: Paparazzi by lazy {
        PaparazziForViewTestRuleBuilder()
            .applyPaparazziConfig(paparazziConfig)
            .applyScreenshotConfig(config)
            .build()
    }

    override fun apply(base: Statement, description: Description): Statement =
        paparazziTestRule.apply(base, description)

    override val context: Context
        get() = paparazziTestRule.context

    override fun inflate(layoutId: Int): View {
        // FontWeight must be applied before inflating the corresponding View to take effect
        paparazziTestRule.context.run {
            setFontWeight(config.fontWeight)
            setDisplaySize(config.displaySize)
        }
        return paparazziTestRule.inflate(layoutId)
    }

    override fun waitForMeasuredView(actionToDo: () -> View): View = actionToDo()

    override fun waitForMeasuredDialog(actionToDo: () -> Dialog): Dialog = actionToDo()

    override fun waitForMeasuredViewHolder(actionToDo: () -> RecyclerView.ViewHolder): RecyclerView.ViewHolder =
        actionToDo()

    override fun snapshotDialog(name: String?, dialog: Dialog) {
        paparazziTestRule.snapshot(
            name = name,
            view = dialog.window!!.decorView,
            offsetMillis = paparazziConfig.snapshotViewOffsetMillis,
        )
    }

    override fun snapshotView(name: String?, view: View) {
        paparazziTestRule.snapshot(
            name = name,
            view = view,
            offsetMillis = paparazziConfig.snapshotViewOffsetMillis,
        )
    }

    override fun snapshotViewHolder(name: String?, viewHolder: RecyclerView.ViewHolder) {
        paparazziTestRule.snapshot(
            name = name,
            view = viewHolder.itemView,
            offsetMillis = paparazziConfig.snapshotViewOffsetMillis,
        )
    }

    override fun configure(config: LibraryConfig): ScreenshotTestRuleForView = apply {
        if (config is PaparazziConfig) {
            paparazziConfig = config
        }
    }
}
