package sergio.sastre.uitesting.robolectric.utils.composable

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ComposableConfigItem

data class TestDataForComposable<T : Enum<T>>(
    val uiState: T,
    val device: DeviceScreen? = null,
    val config: ComposableConfigItem? = null
) {
    val screenshotId: String
        get() = listOfNotNull(
            uiState.name,
            config?.id,
            device?.name
        )
            .filter { it.isNotBlank() }
            .joinToString(separator = "_")
}
