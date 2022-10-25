package sergio.sastre.uitesting.utils.testrules.displaysize

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand

class DisplayScaleSetting internal constructor() {

    private val resources
        get() = getInstrumentation().targetContext.resources

    fun getDensityDpi(): Int {
        return resources.configuration.densityDpi
    }

    fun setDisplaySizeScale(originalDensity: Int) {
        try {
            getInstrumentation().waitForExecuteShellCommand("wm density reset")
        } catch (e: Exception) {
            throw RuntimeException("Unable to reset densityDpi to $originalDensity")
        }
    }

    fun setDisplaySizeScale(scale: DisplaySize) {
        try {
            val targetDensityDpi = resources.configuration.densityDpi * (scale.value).toFloat()
            getInstrumentation().waitForExecuteShellCommand("wm density " + targetDensityDpi.toInt())
        } catch (e: Exception) {
            throw RuntimeException("Unable to set display size with scale ${scale.name} = ${scale.value}")
        }
    }
}
