package sergio.sastre.uitesting.dropshots

import android.app.Dialog
import android.graphics.Bitmap
import android.view.View
import androidx.core.view.drawToBitmap
import com.dropbox.dropshots.Dropshots
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.utils.drawToBitmap
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation

/**
 * Wrapper on Dropshots to record/verify screenshots
 */
internal class ScreenshotTaker(
    private val dropshots: Dropshots,
) : TestRule {
    override fun apply(base: Statement, description: Description): Statement =
        dropshots.apply(base, description)

    fun assertSnapshot(
        view: View,
        bitmapCaptureMethod: BitmapCaptureMethod?,
        name: String?,
        filePath: String?,
    ) {
        when (bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                assertSnapshot(
                    bitmap = view.drawToBitmap(config = bitmapCaptureMethod.config),
                    name = name,
                    filePath = filePath,
                )
            is BitmapCaptureMethod.PixelCopy ->
                assertSnapshot(
                    bitmap = view.drawToBitmapWithElevation(config = bitmapCaptureMethod.config),
                    name = name,
                    filePath = filePath,
                )
            null -> assertSnapshot(
                view = view,
                name = name,
                filePath = filePath,
            )
        }
    }

    fun assertSnapshot(
        dialog: Dialog,
        bitmapCaptureMethod: BitmapCaptureMethod?,
        name: String?,
        filePath: String?,
    ) {
        when (bitmapCaptureMethod) {
            is BitmapCaptureMethod.Canvas ->
                assertSnapshot(
                    bitmap = dialog.drawToBitmap(config = bitmapCaptureMethod.config),
                    name = name,
                    filePath = filePath,
                )
            is BitmapCaptureMethod.PixelCopy ->
                assertSnapshot(
                    bitmap = dialog.drawToBitmapWithElevation(config = bitmapCaptureMethod.config),
                    name = name,
                    filePath = filePath,
                )
            null -> assertSnapshot(
                view = dialog.window!!.decorView,
                name = name,
                filePath = filePath,
            )
        }
    }

    private fun assertSnapshot(bitmap: Bitmap, name: String?, filePath: String?) {
        if (name != null) {
            dropshots.assertSnapshot(
                bitmap = bitmap,
                name = name,
                filePath = filePath
            )
        } else {
            dropshots.assertSnapshot(
                bitmap = bitmap,
                filePath = filePath,
            )
        }
    }

    private fun assertSnapshot(view: View, name: String?, filePath: String?) {
        if (name != null) {
            dropshots.assertSnapshot(
                view = view,
                name = name,
                filePath = filePath,
            )
        } else {
            dropshots.assertSnapshot(
                view = view,
                filePath = filePath,
            )
        }
    }
}
