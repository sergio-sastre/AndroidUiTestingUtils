package sergio.sastre.uitesting.utils.crosslibrary.config

import sergio.sastre.uitesting.utils.activityscenario.ComposableConfigItem
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
class ScreenshotConfigForComposable(
    val orientation: Orientation = Orientation.PORTRAIT,
    val uiMode: UiMode = UiMode.DAY,
    val fontScale: FontSize = FontSize.NORMAL,
    val locale: String = "en",
    val displaySize: DisplaySize = DisplaySize.NORMAL,
) {

    fun toComposableConfig(): ComposableConfigItem =
        ComposableConfigItem(
            orientation = orientation,
            uiMode = uiMode,
            locale = locale,
            fontSize = fontScale,
            displaySize = displaySize,
        )
}
