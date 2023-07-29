package sergio.sastre.uitesting.paparazzi.config

import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import com.android.resources.ScreenOrientation.*
import sergio.sastre.uitesting.sharedtest.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForComposable
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfigForView

internal class PaparazziScreenshotConfigAdapter(
    private val paparazziConfig: PaparazziConfig
) {

    fun getDeviceConfigFor(screenshotConfigForComposable: ScreenshotConfigForComposable): app.cash.paparazzi.DeviceConfig =
        PaparazziSharedTestAdapter(paparazziConfig).asPaparazziDeviceConfig().copy(
            orientation = screenshotConfigForComposable.orientation.toScreenOrientation(),
            nightMode = screenshotConfigForComposable.uiMode.toNightMode(),
            fontScale = screenshotConfigForComposable.fontScale.value.toFloat(),
            locale = screenshotConfigForComposable.locale.toBC47Locale(),
        ).adjustDimensionsToOrientation()

    fun getDeviceConfigFor(screenshotConfigForView: ScreenshotConfigForView): app.cash.paparazzi.DeviceConfig =
        PaparazziSharedTestAdapter(paparazziConfig).asPaparazziDeviceConfig().copy(
            orientation = screenshotConfigForView.orientation.toScreenOrientation(),
            nightMode = screenshotConfigForView.uiMode.toNightMode(),
            fontScale = screenshotConfigForView.fontSize.value.toFloat(),
            locale = screenshotConfigForView.locale.toBC47Locale(),
        ).adjustDimensionsToOrientation()

    private fun app.cash.paparazzi.DeviceConfig.adjustDimensionsToOrientation()
            : app.cash.paparazzi.DeviceConfig {
        // If a Paparazzi rendering mode applies,
        // do not hack Height and Width to simulate orientation change
        if (paparazziConfig.renderingMode != null) {
            return switchHeightAndWidth()
        }

        val old = copy()
        return when (orientation) {
            PORTRAIT -> copy(screenHeight = 1)
            LANDSCAPE -> copy(
                screenHeight = 1,
                screenWidth = old.screenHeight,
            )
            SQUARE -> this
        }
    }

    private fun app.cash.paparazzi.DeviceConfig.switchHeightAndWidth()
            : app.cash.paparazzi.DeviceConfig {
        val old = copy()
        val height = when (orientation) {
            LANDSCAPE -> old.screenWidth
            PORTRAIT, SQUARE -> old.screenHeight
        }
        val width = when (orientation) {
            LANDSCAPE -> old.screenHeight
            PORTRAIT, SQUARE -> old.screenWidth
        }
        return this.copy(
            screenHeight = height,
            screenWidth = width,
            orientation = this.orientation
        )
    }

    private fun Orientation.toScreenOrientation(): ScreenOrientation =
        when (this) {
            Orientation.PORTRAIT -> PORTRAIT
            Orientation.LANDSCAPE -> LANDSCAPE
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
