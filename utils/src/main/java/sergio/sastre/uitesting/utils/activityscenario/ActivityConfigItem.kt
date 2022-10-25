package sergio.sastre.uitesting.utils.activityscenario

import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

data class ActivityConfigItem(
    val orientation: Orientation? = null,
    val uiMode: UiMode? = null,
    val systemLocale: String? = null,
    val fontSize: FontSize? = null,
    val displaySize: DisplaySize? = null,
)