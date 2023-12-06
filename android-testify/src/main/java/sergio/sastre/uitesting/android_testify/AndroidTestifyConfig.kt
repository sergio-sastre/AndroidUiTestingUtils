package sergio.sastre.uitesting.android_testify

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.drawToBitmap
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation

typealias CaptureMethod = (activity: Activity, targetView: View?) -> Bitmap?

class AndroidTestifyConfig(
    val captureMethod: CaptureMethod? = null,
    val exactness: Float = 0.9f,
    val enableReporter: Boolean = false,
    val initialTouchMode: Boolean = false,
    val generateDiffs: Boolean = true,
    val animationsDisabled: Boolean = true,
    @ColorInt val backgroundColor: Int? = null,
) : LibraryConfig

fun pixelCopy(activity: Activity, targetView: View?): Bitmap? {
    return targetView?.drawToBitmapWithElevation()
}

fun canvas(activity: Activity, targetView: View?): Bitmap? {
    return targetView?.drawToBitmap()
}
