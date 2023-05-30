package sergio.sastre.uitesting.shot

import androidx.annotation.ColorInt
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig

class ShotConfig(
    val ignoredViews: List<Int> = emptyList(),
    val prepareUIForScreenshot: () -> Unit = {},
    val bitmapCaptureMethod: BitmapCaptureMethod? = null,
    @ColorInt val backgroundColor: Int? = null,
): LibraryConfig
