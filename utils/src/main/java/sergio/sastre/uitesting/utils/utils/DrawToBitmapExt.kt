package sergio.sastre.uitesting.utils.utils

import android.R.id.content
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
import java.util.concurrent.CountDownLatch
import kotlin.random.Random

/**
 * Return a [Bitmap] representation of the itemView of this [RecyclerView.ViewHolder].
 *
 * The resulting bitmap will be the same width and height as this view's current layout
 * dimensions. This does not take into account any transformations such as scale or translation.
 *
 * Note, this will use the software rendering pipeline to draw the view to the bitmap. This may
 * result with different drawing to what is rendered on a hardware accelerated canvas (such as
 * the device screen).
 *
 * If this view has not been laid out this method will throw a [IllegalStateException].
 *
 * @param config Bitmap config of the desired bitmap. Defaults to [Bitmap.Config.ARGB_8888].
 */
fun RecyclerView.ViewHolder.drawToBitmap(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    itemView.drawToBitmap(config)

/**
 * Return a [Bitmap] representation of the rootView of this [Fragment].
 *
 * The resulting bitmap will be the same width and height as this view's current layout
 * dimensions. This does not take into account any transformations such as scale or translation.
 *
 * Note, this will use the software rendering pipeline to draw the view to the bitmap. This may
 * result with different drawing to what is rendered on a hardware accelerated canvas (such as
 * the device screen).
 *
 * If this view has not been laid out this method will throw a [IllegalStateException].
 *
 * @param config Bitmap config of the desired bitmap. Defaults to [Bitmap.Config.ARGB_8888].
 */
fun Fragment.drawToBitmap(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    view!!.drawToBitmap(config)

/**
 * Return a [Bitmap] representation of the rootView of this [Activity].
 *
 * The resulting bitmap will be the same width and height as this view's current layout
 * dimensions. This does not take into account any transformations such as scale or translation.
 *
 * Note, this will use the software rendering pipeline to draw the view to the bitmap. This may
 * result with different drawing to what is rendered on a hardware accelerated canvas (such as
 * the device screen).
 *
 * If this view has not been laid out this method will throw a [IllegalStateException].
 *
 * @param config Bitmap config of the desired bitmap. Defaults to [Bitmap.Config.ARGB_8888].
 */
fun Activity.drawToBitmap(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    window!!.decorView.drawToBitmap(config)

/**
 * Return a [Bitmap] representation of the decorView of this [Window].
 *
 * The resulting bitmap will be the same width and height as this view's current layout
 * dimensions. This does not take into account any transformations such as scale or translation.
 *
 * Note, this will use the software rendering pipeline to draw the view to the bitmap. This may
 * result with different drawing to what is rendered on a hardware accelerated canvas (such as
 * the device screen).
 *
 * If this view has not been laid out this method will throw a [IllegalStateException].
 *
 * @param config Bitmap config of the desired bitmap. Defaults to [Bitmap.Config.ARGB_8888].
 */
fun Window.drawToBitmap(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    decorView.drawToBitmap(config)

/**
 * Return a [Bitmap] representation of the window of this [Dialog].
 *
 * The resulting bitmap will be the same width and height as this view's current layout
 * dimensions. This does not take into account any transformations such as scale or translation.
 *
 * Note, this will use the software rendering pipeline to draw the view to the bitmap. This may
 * result with different drawing to what is rendered on a hardware accelerated canvas (such as
 * the device screen).
 *
 * If this view has not been laid out this method will throw a [IllegalStateException].
 *
 * @param config Bitmap config of the desired bitmap. Defaults to [Bitmap.Config.ARGB_8888].
 */
fun Dialog.drawToBitmap(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    window!!.decorView.drawToBitmap(config)

/**
 * Generates a bitmap of the [View] by using PixelCopy, which considers elevation.
 *
 * However, PixelCopy has some limitations.
 * It can only generate bitmaps of content inside the given [Activity]'s [Window].
 * This means, cannot generate bitmaps of views/composables beyond or above/below screen,
 * e.g. full scrollable views that go beyond what fits on the user's screen.
 *
 * @param config Bitmap config of the desired bitmap. Defaults to [Bitmap.Config.ARGB_8888].
 */
fun View.drawToBitmapWithElevation(
    activity: Activity = rootView.findViewById<View>(content).context as Activity,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    drawToBitmapWithElevation(activity.window, config)

/**
 * Generates a bitmap of the [RecyclerView.ViewHolder] by using PixelCopy, which considers elevation.
 *
 * However, PixelCopy has some limitations.
 * It can only generate bitmaps of content inside the given [Activity]'s [Window].
 * This means, cannot generate bitmaps of views/composables beyond or above/below screen,
 * e.g. full scrollable views that go beyond what fits on the user's screen.
 */
fun RecyclerView.ViewHolder.drawToBitmapWithElevation(
    activity: Activity = itemView.rootView.findViewById<View>(content).context as Activity,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    itemView.drawToBitmapWithElevation(activity, config)

/**
 * Generates a bitmap of the [Fragment] by using PixelCopy, which considers elevation.
 *
 * However, PixelCopy has some limitations.
 * It can only generate bitmaps of content inside the [Fragment]'s [Activity]'s [Window].
 * This means, cannot generate bitmaps of views/composables beyond or above/below screen,
 * e.g. full scrollable views that go beyond what fits on the user's screen.
 */
fun Fragment.drawToBitmapWithElevation(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    view!!.drawToBitmapWithElevation(requireActivity(), config)

/**
 * Generates a bitmap of the [Activity] by using PixelCopy, which considers elevation.
 *
 * However, PixelCopy has some limitations.
 * It can only generate bitmaps of content inside the [Activity]'s [Window].
 * This means, cannot generate bitmaps of views/composables beyond or above/below screen,
 * e.g. full scrollable views that go beyond what fits on the user's screen.
 */
fun Activity.drawToBitmapWithElevation(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    window!!.decorView.drawToBitmapWithElevation(this, config)

/**
 * Generates a bitmap of the [Window] by using PixelCopy, which considers elevation.
 *
 * However, PixelCopy has some limitations.
 * It can only generate bitmaps of content inside the [Window].
 * This means, cannot generate bitmaps of views/composables beyond or above/below screen,
 * e.g. full scrollable views that go beyond what fits on the user's screen.
 */
fun Window.drawToBitmapWithElevation(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    decorView.drawToBitmapWithElevation(this, config)

/**
 * Generates a bitmap of the [Window] by using PixelCopy, which considers elevation.
 *
 * However, PixelCopy has some limitations.
 * It can only generate bitmaps of content inside the [Dialog]'s [Window].
 * This means, cannot generate bitmaps of views/composables beyond or above/below screen,
 * e.g. full scrollable views that go beyond what fits on the user's screen.
 */
fun Dialog.drawToBitmapWithElevation(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    window!!.decorView.drawToBitmapWithElevation(window!!, config)

/**
 * Forces the Scrollable View to be remeasured & waits for the Ui thread to be Idle.
 * Then, it draws a bitmap via Canvas out of it.
 *
 * This is necessary to take a screenshot with the expected size of the scrollable view.
 * The scrollable view is likely to expand beyond the window size. Therefore, do drawing a bitmap with
 * elevation via PixelCopy would distort the image.
 *
 * That's why Canvas is used instead.
 */
fun View.drawFullScrollableToBitmap(): Bitmap =
    waitForMeasuredView { this }.drawToBitmap()

/**
 * Generates a bitmap of the [View] by using PixelCopy, which considers elevation on API 26+,
 * defaults to Canvas on lower APIs.
 *
 * However, PixelCopy has some limitations.
 * It can only generate bitmaps of content inside the given [Window].
 * This means, cannot generate bitmaps of views/composables beyond or above/below screen,
 * e.g. full scrollable views that go beyond what fits on the user's screen.
 *
 */
private fun View.drawToBitmapWithElevation(
    window: Window,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        drawToBitmapWithPixelCopy(window, config)
    } else {
        Log.e(
            "PixelCopy",
            "PixelCopy supported from ${Build.VERSION_CODES.O}+. " +
                    "The device API is ${Build.VERSION.SDK_INT}. Creating Bitmap from Canvas. " +
                    "Elevation will be ignored"
        )
        drawToBitmap(config)
    }

@TargetApi(Build.VERSION_CODES.O)
private fun View.drawToBitmapWithPixelCopy(
    window: Window,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, config)

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
