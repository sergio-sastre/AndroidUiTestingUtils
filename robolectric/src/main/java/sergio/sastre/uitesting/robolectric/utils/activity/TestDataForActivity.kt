package sergio.sastre.uitesting.robolectric.utils.activity

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ActivityConfigItem

data class TestDataForActivity<T :Enum<T>>(
    val uiState: T,
    val device: DeviceScreen? = null,
    val config: ActivityConfigItem? = null
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
