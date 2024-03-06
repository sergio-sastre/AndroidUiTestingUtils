package sergio.sastre.uitesting.paparazzi.config

import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import com.android.resources.ScreenOrientation.LANDSCAPE
import com.android.resources.ScreenOrientation.PORTRAIT
import com.android.resources.ScreenOrientation.SQUARE
import sergio.sastre.uitesting.mapper.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView

internal class PaparazziScreenshotConfigAdapter(
    private val paparazziConfig: PaparazziConfig
) {
    fun getDeviceConfigFor(screenshotConfigForComposable: ScreenshotConfigForComposable): app.cash.paparazzi.DeviceConfig =
        PaparazziWrapperConfigAdapter(paparazziConfig).asPaparazziDeviceConfig().copy(
            orientation = screenshotConfigForComposable.orientation.toScreenOrientation(),
            nightMode = screenshotConfigForComposable.uiMode.toNightMode(),
            fontScale = screenshotConfigForComposable.fontScale.value.toFloat(),
            locale = screenshotConfigForComposable.locale.toBC47Locale(),
        ).hackViewDimensionsToOrientation()

    fun getDeviceConfigFor(screenshotConfigForView: ScreenshotConfigForView): app.cash.paparazzi.DeviceConfig =
        PaparazziWrapperConfigAdapter(paparazziConfig).asPaparazziDeviceConfig().copy(
            orientation = screenshotConfigForView.orientation.toScreenOrientation(),
            nightMode = screenshotConfigForView.uiMode.toNightMode(),
            fontScale = screenshotConfigForView.fontSize.value.toFloat(),
            locale = screenshotConfigForView.locale.toBC47Locale(),
        ).hackViewDimensionsToOrientation()

    /**
     * Paparazzi has sometimes problems with measuring views. Therefore, this uses a default
     * configuration that renders most views properly.
     *
     * Set the RenderingMode explicitly if that is not desired.
     */
    private fun app.cash.paparazzi.DeviceConfig.hackViewDimensionsToOrientation()
            : app.cash.paparazzi.DeviceConfig {
        // If a Paparazzi rendering mode applies,
        // do not hack Height and Width to simulate orientation change
        if (paparazziConfig.renderingMode != null) {
            return copy(orientation = this.orientation)
        }

        // This is a hack when if using SHRINK, the ViewHolder dimensions are miscalculated
        // For that, one should use V_SCROLL + these values in DeviceConfig
        val old = copy()
        return when (orientation) {
            PORTRAIT -> copy(
                screenHeight = old.screenWidth,
                screenWidth = 1,
                orientation = LANDSCAPE,
            )
            LANDSCAPE -> copy(
                screenWidth = old.screenHeight,
                screenHeight = 1,
                orientation = LANDSCAPE,
            )
            SQUARE -> this
        }
    }

    private fun Orientation.toScreenOrientation(): ScreenOrientation =
        when (this) {
            Orientation.PORTRAIT -> PORTRAIT
            Orientation.LANDSCAPE -> LANDSCAPE
        }

    private fun String.toBC47Locale(): String =
        when {
            this == "en_XA" -> "en-rXA"
            this == "ar_XB" -> "ar-rXB"
            this.contains("-") -> "b+${this.replace(oldChar = '-', newChar = '+')}"
            else -> this
        }

    private fun UiMode.toNightMode(): NightMode =
        when (this) {
            UiMode.NIGHT -> NightMode.NIGHT
            UiMode.DAY -> NightMode.NOTNIGHT
        }
}
