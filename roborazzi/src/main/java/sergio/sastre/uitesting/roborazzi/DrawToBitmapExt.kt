package sergio.sastre.uitesting.roborazzi

import android.app.Dialog
import android.graphics.Bitmap
import android.view.View
import androidx.core.view.drawToBitmap
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation

internal fun View.drawToBitmap(
    bitmapCaptureMethod: BitmapCaptureMethod?
): Bitmap =
    when (bitmapCaptureMethod){
        is BitmapCaptureMethod.Canvas -> drawToBitmap(config = bitmapCaptureMethod.config)
        is BitmapCaptureMethod.PixelCopy -> drawToBitmapWithElevation(config = bitmapCaptureMethod.config)
        null -> drawToBitmapWithElevation()
    }

internal fun Dialog.drawToBitmap(
    bitmapCaptureMethod: BitmapCaptureMethod?,
): Bitmap =
    window!!.decorView.drawToBitmap(bitmapCaptureMethod)