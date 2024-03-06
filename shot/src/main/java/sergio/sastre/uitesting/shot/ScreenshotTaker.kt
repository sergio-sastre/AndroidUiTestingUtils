package sergio.sastre.uitesting.shot

import android.app.Dialog
import android.os.Build
import android.view.View
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.karumi.shot.ScreenshotTest
import org.lsposed.hiddenapibypass.HiddenApiBypass
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.utils.drawToBitmap
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation

internal class ScreenshotTaker(
    private val screenshotTest: ScreenshotTest,
) {

    private fun compareScreenshot(
        view: View,
        bitmapCaptureMethod: BitmapCaptureMethod?,
        name: String?,
        onNoBitmapCapturedMethod: () -> Unit,
    ){
        when (bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas -> screenshotTest.compareScreenshot(
                bitmap = view.drawToBitmap(config = bitmapCaptureMethod.config),
                name = name,
            )
            is BitmapCaptureMethod.PixelCopy -> screenshotTest.compareScreenshot(
                bitmap = view.drawToBitmapWithElevation(config = bitmapCaptureMethod.config),
                name = name,
            )
            null -> onNoBitmapCapturedMethod()
        }
    }

    fun compareSnapshot(
        view: View,
        bitmapCaptureMethod: BitmapCaptureMethod?,
        name: String?,
        maxPixels: Long,
    ) {
        compareScreenshot(
            view = view,
            bitmapCaptureMethod = bitmapCaptureMethod,
            name = name,
            onNoBitmapCapturedMethod = {
                screenshotTest.compareScreenshot(
                    view = view,
                    name = name,
                    maxPixels = maxPixels,
                )
            }
        )
    }

    fun compareSnapshot(
        composeTestRule: ComposeTestRule,
        view: View,
        bitmapCaptureMethod: BitmapCaptureMethod?,
        name: String?,
    ) {
        compareScreenshot(
            view = view,
            bitmapCaptureMethod = bitmapCaptureMethod,
            name = name,
            onNoBitmapCapturedMethod = {
                screenshotTest.compareScreenshot(
                    rule = composeTestRule,
                    name = name,
                )
            }
        )
    }

    fun compareSnapshot(
        viewHolder: ViewHolder,
        bitmapCaptureMethod: BitmapCaptureMethod?,
        name: String?,
        maxPixels: Long,
    ) {
        compareScreenshot(
            view = viewHolder.itemView,
            bitmapCaptureMethod = bitmapCaptureMethod,
            name = name,
            onNoBitmapCapturedMethod = {
                screenshotTest.compareScreenshot(
                    holder = viewHolder,
                    heightInPx = viewHolder.itemView.measuredHeight,
                    name = name,
                    maxPixels = maxPixels,
                )
            }
        )
    }

    fun compareSnapshot(
        dialog: Dialog,
        bitmapCaptureMethod: BitmapCaptureMethod?,
        name: String?,
        maxPixels: Long,
    ) {
        when (bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas -> screenshotTest.compareScreenshot(
                bitmap = dialog.drawToBitmap(config = bitmapCaptureMethod.config),
                name = name,
            )
            is BitmapCaptureMethod.PixelCopy -> screenshotTest.compareScreenshot(
                bitmap = dialog.drawToBitmapWithElevation(config = bitmapCaptureMethod.config),
                name = name,
            )
            null -> bypassNonSDKInterface {
                screenshotTest.compareScreenshot(
                    dialog = dialog,
                    name = name,
                    maxPixels = maxPixels,
                )
            }
        }
    }

    // compareScreenshot uses some non-SDK interfaces to take screenshots of dialogs,
    // so we need to bypass it
    private fun bypassNonSDKInterface(actionToDo: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }
        actionToDo()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.clearHiddenApiExemptions()
        }
    }
}
