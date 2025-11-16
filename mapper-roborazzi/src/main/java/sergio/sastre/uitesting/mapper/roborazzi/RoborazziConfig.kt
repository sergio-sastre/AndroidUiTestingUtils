package sergio.sastre.uitesting.mapper.roborazzi

import androidx.annotation.ColorInt
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.CaptureType
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.DumpExplanation.AccessibilityExplanation
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.RoborazziOptions
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.screen.DeviceScreen
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig

data class RoborazziConfig(
    val filePath: String = DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH,
    val roborazziOptions: RoborazziOptions = RoborazziOptions(),
    val deviceScreen: DeviceScreen? = null,
    @get:ColorInt val backgroundColor: Int? = null,
    val bitmapCaptureMethod: BitmapCaptureMethod? = null,
) : LibraryConfig {

    fun overrideForDefaultAccessibility(): RoborazziConfig {
        return copy(
            roborazziOptions = roborazziOptions.copy(
                captureType = CaptureType.Dump(AccessibilityExplanation)
            ),
        )
    }

    companion object {
        const val DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH = "build/outputs/roborazzi"
    }
}
