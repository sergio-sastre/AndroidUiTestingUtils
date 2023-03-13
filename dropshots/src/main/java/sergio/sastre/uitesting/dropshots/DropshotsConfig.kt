package sergio.sastre.uitesting.dropshots

import com.dropbox.dropshots.ResultValidator
import com.dropbox.dropshots.ThresholdValidator
import sergio.sastre.uitesting.utils.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.LibraryConfig

class DropshotsConfig(
    val bitmapCaptureMethod: BitmapCaptureMethod? = null,
    val resultValidator: ResultValidator = ThresholdValidator(0.0f),
): LibraryConfig
