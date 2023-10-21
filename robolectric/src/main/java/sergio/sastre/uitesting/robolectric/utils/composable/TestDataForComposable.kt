package sergio.sastre.uitesting.robolectric.utils.composable

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ComposableConfigItem

data class TestDataForComposable<T :Enum<T>>(
    val uiState: T,
    val device: DeviceScreen? = null,
    val config: ComposableConfigItem? = null
){
    val screenshotId: String
        get() = listOf(
            uiState.name,
            config?.asFileName.orEmpty(),
            device?.asFileName.orEmpty()
        ).joinToString(separator = "_")

    private val DeviceScreen.asFileName
        get() = this.name.replace(" ", "_").uppercase()

    private val ComposableConfigItem.asFileName : String
        get() {
            val nonNullProperties = mutableListOf<String>()
            locale?.let { nonNullProperties.add(it.uppercase()) }
            uiMode?.let { nonNullProperties.add(it.name) }
            fontSize?.let { nonNullProperties.add(it.name) }
            displaySize?.let { nonNullProperties.add(it.name) }
            orientation?.let { nonNullProperties.add(it.name) }
            return nonNullProperties.joinToString(separator = "_")
        }
}
