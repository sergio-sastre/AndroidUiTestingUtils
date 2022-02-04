package sergio.sastre.uitesting.utils.testrules.fontsize

/**
 * Concept taken from : https://github.com/novoda/espresso-support
 */
import android.content.res.Resources
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.FontScale

class FontScaleSetting internal constructor(private val resources: Resources) {

    fun get(): FontScale {
        return FontScale.from(resources.configuration.fontScale)
    }

    fun set(scale: FontScale) {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                changeFontScaleFromApi25(scale)
            } else {
                changeFontScalePreApi25(scale)
            }

        } catch (e: Exception) {
            throw saveFontScaleError(scale)
        }
    }

    private fun changeFontScalePreApi25(scale: FontScale) {
        resources.configuration.fontScale = java.lang.Float.parseFloat(scale.value)
        val metrics = Resources.getSystem().displayMetrics
        metrics.scaledDensity = resources.configuration.fontScale * metrics.density
        resources.updateConfiguration(resources.configuration, metrics)
    }

    private fun changeFontScaleFromApi25(scale: FontScale) {
        getInstrumentation().uiAutomation
            .executeShellCommand("settings put system font_scale " + scale.value)
    }

    private fun saveFontScaleError(scale: FontScale): RuntimeException {
        return RuntimeException("Unable to save font size " + scale.name)
    }
}
