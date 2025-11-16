package sergio.sastre.uitesting.shot

import androidx.annotation.ColorInt
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig

/**
 * @param viewMaxPixels: Max Pixels for the generated screenshot before throwing an exception.
 * WARNING:
 *    1. Requires bitmapCaptureMethod = null
 *    2. Works only with Views, not with Composables
 */
data class ShotConfig(
    val ignoredViews: List<Int> = emptyList(),
    val prepareUIForScreenshot: () -> Unit = {},
    val bitmapCaptureMethod: BitmapCaptureMethod? = null,
    @get:ColorInt val backgroundColor: Int? = null,
    val viewMaxPixels: Long = 10_000_000L,
): LibraryConfig
