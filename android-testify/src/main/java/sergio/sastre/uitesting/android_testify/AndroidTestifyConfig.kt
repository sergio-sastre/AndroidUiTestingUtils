package sergio.sastre.uitesting.android_testify

import androidx.annotation.ColorInt
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig

class AndroidTestifyConfig(
    val bitmapCaptureMethod: BitmapCaptureMethod? = null,
    val exactness: Float = 0.9f,
    val enableReporter: Boolean = false,
    val initialTouchMode: Boolean = false,
    val generateDiffs: Boolean = true,
    val animationsDisabled: Boolean = true,
    @ColorInt val backgroundColor: Int? = null,
) : LibraryConfig
