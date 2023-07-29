package sergio.sastre.uitesting.utils.crosslibrary.config

import androidx.annotation.StyleRes
import sergio.sastre.uitesting.utils.activityscenario.ComposableConfigItem
import sergio.sastre.uitesting.utils.activityscenario.ViewConfigItem
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

/**
 * Configuration that all screenshot libraries support for cross-library screenshot tests.
 *
 * Warning: The locale should be in IEFT BCP 47 format:
 * language-extlang-script-region-variant-extension-privateuse
 *
 * For instance:
 * sr-Cyrl-RS // for Serbian written in Cyrillic
 * zh-Latn-TW-pinyin // for Chinese language spoken in Taiwan in pinyin
 * ca-ES-valencia // for Catalan spoken in Valencia
 *
 */
data class ScreenshotConfigForView(
    val orientation: Orientation = Orientation.PORTRAIT,
    val uiMode: UiMode = UiMode.DAY,
    val locale: String = "en",
    val fontSize: FontSize = FontSize.NORMAL,
    val displaySize: DisplaySize = DisplaySize.NORMAL,
    @StyleRes val theme: Int? = null,
) {
    fun toViewConfig(): ViewConfigItem =
        ViewConfigItem(
            orientation = orientation,
            uiMode = uiMode,
            locale = locale,
            fontSize = fontSize,
            displaySize = displaySize,
            theme = theme,
        )
}
