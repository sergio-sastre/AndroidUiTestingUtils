package sergio.sastre.uitesting.utils

import android.graphics.Bitmap

sealed class BitmapCaptureMethod {
    class PixelCopy(val config: Bitmap.Config = Bitmap.Config.ARGB_8888): BitmapCaptureMethod()
    class Canvas(val config: Bitmap.Config = Bitmap.Config.ARGB_8888): BitmapCaptureMethod()
}
