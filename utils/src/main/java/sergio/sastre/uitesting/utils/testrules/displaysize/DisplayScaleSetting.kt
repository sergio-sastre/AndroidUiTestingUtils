package sergio.sastre.uitesting.utils.testrules.displaysize

import android.content.res.Resources
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.DisplaySize

class DisplayScaleSetting internal constructor(private val resources: Resources) {

    fun getDensityDpi(): Int {
        return resources.configuration.densityDpi
    }

    fun resetDisplaySizeScale(originalDensity: Int) {
        try {
            getInstrumentation().uiAutomation.executeShellCommand("wm density reset")
        } catch (e: Exception) {
            throw RuntimeException("Unable to reset densityDpi to $originalDensity")
        }
    }

    fun setDisplaySizeScale(scale: DisplaySize) {
        try {
            val targetDensityDpi = resources.configuration.densityDpi * (scale.value).toFloat()
            getInstrumentation().uiAutomation
                .executeShellCommand("wm density " + targetDensityDpi.toInt())
        } catch (e: Exception) {
            throw RuntimeException("Unable to set display size with scale ${scale.name} = ${scale.value}")
        }
    }
}
