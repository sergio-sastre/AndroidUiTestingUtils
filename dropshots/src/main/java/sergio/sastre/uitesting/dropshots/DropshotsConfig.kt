package sergio.sastre.uitesting.dropshots

import android.content.Context
import android.os.Environment
import androidx.annotation.ColorInt
import com.dropbox.differ.ImageComparator
import com.dropbox.differ.SimpleImageComparator
import com.dropbox.dropshots.CountValidator
import com.dropbox.dropshots.ResultValidator
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig
import java.io.File

/**
 * Configuration for Dropshots screenshot tests.
 *
 * @property rootScreenshotDir A file provider for the root directory where screenshots are stored on the device.
 *   **The directory must be writable by the instrumentation process.**
 *
 *   The default value targets `Downloads/screenshots/<packageName>` and is provided as a
 *   convenience for **AGP 9.x test-storage setups that already handle permissions** via
 *   the Dropshots Gradle plugin. If your setup does not manage permissions, override this
 *   with a writable path such as:
 *   ```kotlin
 *   DropshotsConfig(
 *       rootScreenshotDir = { context -> context.getExternalFilesDir(null)!! }
 *   )
 *   ```
 */
data class DropshotsConfig(
    val resultValidator: ResultValidator = CountValidator(0),
    val imageComparator: ImageComparator = SimpleImageComparator(maxDistance = 0.004f),
    val bitmapCaptureMethod: BitmapCaptureMethod? = null,
    @get:ColorInt val backgroundColor: Int? = null,
    val filePath: String? = null,
    val rootScreenshotDir: (Context) -> File = { context ->
        defaultRootScreenshotDirectory(context)
    }
) : LibraryConfig

internal fun defaultRootScreenshotDirectory(context: Context): File {
    val externalStorageDir = Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    return File(externalStorageDir, "screenshots/${context.packageName}")
}
