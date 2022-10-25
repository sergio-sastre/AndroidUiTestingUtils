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

    fun resetDisplaySizeScale(originalDensity: Int) {
        try {
            getInstrumentation().waitForExecuteShellCommand("wm density reset")
        } catch (e: Exception) {
            throw RuntimeException("Unable to reset densityDpi to $originalDensity")
        }
    }

    fun setDisplaySizeScale(targetDensity: Int){
        try {
            getInstrumentation().waitForExecuteShellCommand("wm density $targetDensity")
        } catch (e: Exception) {
            throw RuntimeException("Unable to set display size with density $targetDensity")
        }
    }

    fun setDisplaySizeScale(scale: DisplaySize) {
        val targetDensity = resources.configuration.densityDpi * (scale.value).toFloat()
        setDisplaySizeScale(targetDensity.toInt())
    }
}
