package sergio.sastre.uitesting.utils.crosslibrary.config

import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

/**
 * Configuration that all screenshot libraries support for library-agnostic screenshot tests.
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
class ScreenshotConfig(
    val orientation: Orientation = Orientation.PORTRAIT,
    val uiMode: UiMode = UiMode.DAY,
    val fontScale: FontSize = FontSize.NORMAL,
    val locale: String = "en",
)
