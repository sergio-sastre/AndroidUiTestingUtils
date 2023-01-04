package sergio.sastre.uitesting.utils.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import java.util.concurrent.CountDownLatch
import kotlin.random.Random

fun View.drawToBitmapWithElevation(): Bitmap =
    drawToBitmapWithElevation(rootView.findViewById<View>(android.R.id.content).context as Activity)

fun View.drawToBitmapWithElevation(activity: Activity): Bitmap =
    drawToBitmapWithElevation(activity.window)

fun Fragment.drawToBitmapWithElevation(): Bitmap =
    view!!.drawToBitmapWithElevation(activity!!)

fun Activity.drawToBitmapWithElevation(): Bitmap =
    window!!.decorView.drawToBitmapWithElevation(this)

fun Window.drawToBitmapWithElevation(): Bitmap =
    decorView.drawToBitmapWithElevation(this)

fun Dialog.drawToBitmapWithElevation(): Bitmap =
    window!!.decorView.drawToBitmapWithElevation(window!!)

fun RecyclerView.ViewHolder.drawToBitmapWithElevation(): Bitmap =
    itemView.drawToBitmapWithElevation()

fun RecyclerView.ViewHolder.drawToBitmapWithElevation(activity: Activity): Bitmap =
    itemView.drawToBitmapWithElevation(activity)

fun View.drawToBitmapWithElevation(window: Window): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        drawToBitmapWithPixelCopy(window)
    } else {
        Log.e(
            "PixelCopy",
            "PixelCopy supported from ${Build.VERSION_CODES.O}+. " +
                    "The device API is ${Build.VERSION.SDK_INT}. Creating Bitmap from Canvas. " +
                    "Elevation will be ignored"
        )
        drawToBitmap()
    }
}

@TargetApi(Build.VERSION_CODES.O)
private fun View.drawToBitmapWithPixelCopy(window: Window): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val locationInWindow = IntArray(2)
    getLocationInWindow(locationInWindow)
    val latch = CountDownLatch(1)
    val handlerThread =
        HandlerThread("PixelCopy${Random.nextInt(0, Int.MAX_VALUE)}").apply { start() }

    PixelCopy.request(
        window,
        Rect(
            locationInWindow[0],
            locationInWindow[1],
            locationInWindow[0] + width,
            locationInWindow[1] + height
        ),
        bitmap,
        { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                latch.countDown()
            } else {
                throw RuntimeException("Failed to capture bitmap with PixelCopy")
            }
            handlerThread.quitSafely()
        },
        Handler(handlerThread.looper)
    )

    latch.await()

    return bitmap
}

fun drawFullScreenBitmap(): Bitmap =
    getInstrumentation().uiAutomation.takeScreenshot()