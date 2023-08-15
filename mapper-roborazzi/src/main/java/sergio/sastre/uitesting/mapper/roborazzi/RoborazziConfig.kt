package sergio.sastre.uitesting.mapper.roborazzi

import androidx.annotation.ColorInt
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.RoborazziOptions
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.screen.DeviceScreen
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig

data class RoborazziConfig(
    val filePath: String = DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH,
    val roborazziOptions: RoborazziOptions = RoborazziOptions(),
    val deviceScreen: DeviceScreen? = null,
    @ColorInt val backgroundColor: Int? = null,
) : LibraryConfig {

    companion object {
        const val DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH = "build/outputs/roborazzi"
    }
}
