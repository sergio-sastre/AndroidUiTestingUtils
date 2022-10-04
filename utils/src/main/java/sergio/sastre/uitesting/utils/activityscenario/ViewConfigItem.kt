package sergio.sastre.uitesting.utils.activityscenario

import androidx.annotation.StyleRes
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

data class ViewConfigItem(
    val orientation: Orientation? = null,
    val uiMode: UiMode? = null,
    val locale: String? = null,
    val fontSize: FontSize? = null,
    val displaySize: DisplaySize? = null,
    @StyleRes val theme: Int? = null,
)
