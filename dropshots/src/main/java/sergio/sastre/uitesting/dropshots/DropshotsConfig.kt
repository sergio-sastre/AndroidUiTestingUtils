package sergio.sastre.uitesting.dropshots

import androidx.annotation.ColorInt
import com.dropbox.differ.ImageComparator
import com.dropbox.differ.SimpleImageComparator
import com.dropbox.dropshots.CountValidator
import com.dropbox.dropshots.ResultValidator
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig

class DropshotsConfig(
    val resultValidator: ResultValidator = CountValidator(0),
    val imageComparator: ImageComparator = SimpleImageComparator(maxDistance = 0.004f),
    val bitmapCaptureMethod: BitmapCaptureMethod? = null,
    val recordScreenshots: Boolean = false,
    @ColorInt val backgroundColor: Int? = null,
) : LibraryConfig
