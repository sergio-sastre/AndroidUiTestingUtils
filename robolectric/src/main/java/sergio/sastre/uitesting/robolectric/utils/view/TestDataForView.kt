package sergio.sastre.uitesting.robolectric.utils.view

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ViewConfigItem

data class TestDataForView<T :Enum<T>>(
    val uiState: T,
    val device: DeviceScreen? = null,
    val config: ViewConfigItem? = null
){
    val screenshotId: String
        get() = listOfNotNull(
            uiState.name,
            config?.id,
            device?.name
        )
            .filter { it.isNotBlank() }
            .joinToString(separator = "_")
}
