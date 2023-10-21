package sergio.sastre.uitesting.robolectric.utils.view

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ViewConfigItem

data class TestDataForView<T :Enum<T>>(
    val uiState: T,
    val device: DeviceScreen? = null,
    val config: ViewConfigItem? = null
){
    val screenshotId: String
        get() = listOf(
            uiState.name,
            config?.asFileName.orEmpty(),
            device?.asFileName.orEmpty()
        ).joinToString(separator = "_")

    private val DeviceScreen.asFileName
        get() = this.name.replace(" ", "_").uppercase()

    private val ViewConfigItem.asFileName : String
        get() {
            val nonNullProperties = mutableListOf<String>()
            locale?.let { nonNullProperties.add(it.uppercase()) }
            uiMode?.let { nonNullProperties.add(it.name) }
            theme?.let {
                val themeInfo = getInstrumentation().targetContext.resources.getResourceName(it)
                // The themeInfo will be in the format: "android:style/Theme.XXX"
                val theme = themeInfo.substring(themeInfo.lastIndexOf('/') + 1)
                nonNullProperties.add(theme.replace(".","_").uppercase())
            }
            fontSize?.let { nonNullProperties.add(it.name) }
            displaySize?.let { nonNullProperties.add(it.name) }
            orientation?.let { nonNullProperties.add(it.name) }
            return nonNullProperties.joinToString(separator = "_")
        }
}
