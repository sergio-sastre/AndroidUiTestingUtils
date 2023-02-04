package sergio.sastre.uitesting.utils.testrules.fontsize

/**
 * Concept taken from : https://github.com/novoda/espresso-support
 */
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand

class FontScaleSetting internal constructor() {

    private val resources
        get() = getInstrumentation().targetContext.resources

    fun get(): FontSize {
        return FontSize.from(resources.configuration.fontScale)
    }

    fun set(scale: FontSize) {
        try {
            changeFontScale(scale)
        } catch (e: Exception) {
            throw saveFontScaleError(scale)
        }
    }

    private fun changeFontScale(scale: FontSize) {
        getInstrumentation().waitForExecuteShellCommand("settings put system font_scale " + scale.value)
    }

    private fun saveFontScaleError(scale: FontSize): RuntimeException {
        return RuntimeException("Unable to save font size " + scale.name)
    }
}
