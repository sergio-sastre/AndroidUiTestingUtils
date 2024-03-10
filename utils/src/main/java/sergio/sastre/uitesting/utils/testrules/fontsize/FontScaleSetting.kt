package sergio.sastre.uitesting.utils.testrules.fontsize

/**
 * Concept taken from : https://github.com/novoda/espresso-support
 */
import android.content.res.Resources
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.FontSizeScale
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand

internal class FontScaleSetting internal constructor() {

    private val resources
        get() = getInstrumentation().targetContext.resources

    internal fun get(): FontSizeScale {
        return FontSizeScale.Value(resources.configuration.fontScale)
    }

    internal fun set(scale: FontSizeScale) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                changeFontScaleFromApi24(scale)
            } else {
                changeFontScalePreApi24(scale)
            }
        } catch (e: Exception) {
            throw saveFontScaleError(scale)
        }
    }

    private fun changeFontScaleFromApi24(fontScale: FontSizeScale) {
        getInstrumentation().waitForExecuteShellCommand(
            "settings put system font_scale " + fontScale.scale
        )
    }

    @Suppress("DEPRECATION")
    private fun changeFontScalePreApi24(fontScale: FontSizeScale) {
        resources.configuration.fontScale = fontScale.scale
        val metrics = Resources.getSystem().displayMetrics
        metrics.scaledDensity = resources.configuration.fontScale * metrics.density
        resources.updateConfiguration(resources.configuration, metrics)
    }

    private fun saveFontScaleError(fontScale: FontSizeScale): RuntimeException {
        return RuntimeException("Unable to save font size scale" + fontScale.scale)
    }
}
