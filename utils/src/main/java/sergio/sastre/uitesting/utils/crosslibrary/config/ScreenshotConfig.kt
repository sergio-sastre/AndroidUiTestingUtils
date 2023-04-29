package sergio.sastre.uitesting.utils.crosslibrary.config

import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

/**
 * Configuration that all screenshot libraries support for library-agnostic screenshot tests
 */
class ScreenshotConfig(
    val orientation: Orientation = Orientation.PORTRAIT,
    val uiMode: UiMode = UiMode.DAY,
    val fontScale: FontSize = FontSize.NORMAL,
    val locale: String = "en",
)
