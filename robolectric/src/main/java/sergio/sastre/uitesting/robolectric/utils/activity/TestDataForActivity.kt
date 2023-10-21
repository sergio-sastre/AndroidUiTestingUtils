package sergio.sastre.uitesting.robolectric.utils.activity

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ActivityConfigItem

data class TestDataForActivity<T :Enum<T>>(
    val uiState: T,
    val device: DeviceScreen? = null,
    val config: ActivityConfigItem? = null
){
    val screenshotId: String
        get() = listOf(
            uiState.name,
            config?.asFileName.orEmpty(),
            device?.asFileName.orEmpty()
        ).joinToString(separator = "_")

    private val DeviceScreen.asFileName
        get() = this.name.replace(" ", "_").uppercase()

    private val ActivityConfigItem.asFileName : String
        get() {
            val nonNullProperties = mutableListOf<String>()
            systemLocale?.let { nonNullProperties.add(it.uppercase()) }
            uiMode?.let { nonNullProperties.add(it.name) }
            fontSize?.let { nonNullProperties.add(it.name) }
            displaySize?.let { nonNullProperties.add(it.name) }
            orientation?.let { nonNullProperties.add(it.name) }
            return nonNullProperties.joinToString(separator = "_")
        }
}
