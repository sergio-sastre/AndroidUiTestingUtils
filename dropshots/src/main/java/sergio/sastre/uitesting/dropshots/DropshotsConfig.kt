package sergio.sastre.uitesting.dropshots

import android.os.Environment
import androidx.annotation.ColorInt
import androidx.test.platform.app.InstrumentationRegistry
import com.dropbox.differ.ImageComparator
import com.dropbox.differ.SimpleImageComparator
import com.dropbox.dropshots.CountValidator
import com.dropbox.dropshots.ResultValidator
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import java.io.File

data class DropshotsConfig(
    val resultValidator: ResultValidator = CountValidator(0),
    val imageComparator: ImageComparator = SimpleImageComparator(maxDistance = 0.004f),
    val bitmapCaptureMethod: BitmapCaptureMethod? = null,
    @get:ColorInt val backgroundColor: Int? = null,
    val filePath: String? = null,
    val rootScreenshotDir: File = defaultRootScreenshotDirectory()
) : LibraryConfig

private fun defaultRootScreenshotDirectory(): File {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val externalStorageDir = Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    return File(externalStorageDir, "screenshots/${context.packageName}")
}
