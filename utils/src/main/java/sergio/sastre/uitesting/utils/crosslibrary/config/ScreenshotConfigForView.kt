package sergio.sastre.uitesting.utils.crosslibrary.config

import android.annotation.SuppressLint
import androidx.annotation.StyleRes
import androidx.test.platform.app.InstrumentationRegistry
import sergio.sastre.uitesting.utils.activityscenario.ViewConfigItem
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.FontSizeScale
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

/**
 * Configuration that all screenshot libraries support for cross-library screenshot tests.
 *
 * Warning: The locale should be in IEFT BCP 47 format:
 * language-extlang-script-region-variant-extension-privateuse
 *
 * Warning: The Theme should be in in string form, as accepted by Paparazzi, for instance:
 * "Theme.Custom" or "android:Theme.Material.NoActionBar.Fullscreen"
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
    val fontSize: FontSizeScale = FontSize.NORMAL,
    val displaySize: DisplaySize = DisplaySize.NORMAL,
    val theme: String? = null,
) {
    fun toViewConfig(): ViewConfigItem =
        ViewConfigItem(
            orientation = orientation,
            uiMode = uiMode,
            locale = locale,
            fontSize = fontSize,
            displaySize = displaySize,
            theme = theme.asTheme()
        )

    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @SuppressLint("DiscouragedApi")
    @StyleRes
    private fun String?.asTheme(): Int? =
        this?.let {
            val styleRes = "${context.packageName}:style/${it.replace("_", ".")}"
            context.resources.getIdentifier(
                styleRes,
                "style",
                context.packageName
            )
        }
}
