package sergio.sastre.uitesting.paparazzi.config

import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import sergio.sastre.uitesting.sharedtest.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfig
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

internal class PaparazziScreenshotConfigAdapter(
    private val paparazziConfig: PaparazziConfig
) {

    fun getDeviceConfigFor(screenshotConfig: ScreenshotConfig): app.cash.paparazzi.DeviceConfig =
        PaparazziSharedTestAdapter(paparazziConfig).asPaparazziDeviceConfig().copy(
            orientation = screenshotConfig.orientation.toScreenOrientation(),
            nightMode = screenshotConfig.uiMode.toNightMode(),
            fontScale = screenshotConfig.fontScale.value.toFloat(),
            locale = screenshotConfig.locale.toBC47Locale(),
        ).adjustDimensionsToOrientation()

    private fun app.cash.paparazzi.DeviceConfig.adjustDimensionsToOrientation()
            : app.cash.paparazzi.DeviceConfig {
        // If a Paparazzi rendering mode applies,
        // do not hack Height and Width to simulate orientation change
        if (paparazziConfig.renderingMode != null) {
            return this.copy(orientation = this.orientation)
        }

        val old = this.copy()
        return when (this.orientation) {
            ScreenOrientation.PORTRAIT -> this.copy(
                screenHeight = 1,
            )
            ScreenOrientation.LANDSCAPE -> this.copy(
                screenHeight = 1,
                screenWidth = old.screenHeight,
            )
            ScreenOrientation.SQUARE -> this
        }
    }

    private fun Orientation.toScreenOrientation(): ScreenOrientation =
        when (this) {
            Orientation.PORTRAIT -> ScreenOrientation.PORTRAIT
            Orientation.LANDSCAPE -> ScreenOrientation.LANDSCAPE
        }

    private fun String.toBC47Locale(): String {
        return if (this.contains("-")) {
            "b+${this.replace(oldChar = '-', newChar = '+')}"
        } else {
            this
        }
    }

    private fun UiMode.toNightMode(): NightMode =
        when (this) {
            UiMode.NIGHT -> NightMode.NIGHT
            UiMode.DAY -> NightMode.NOTNIGHT
        }
}
