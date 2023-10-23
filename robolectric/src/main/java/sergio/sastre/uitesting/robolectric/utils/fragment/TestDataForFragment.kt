package sergio.sastre.uitesting.robolectric.utils.fragment

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.fragmentscenario.FragmentConfigItem

data class TestDataForFragment<T :Enum<T>>(
    val uiState: T,
    val device: DeviceScreen? = null,
    val config: FragmentConfigItem? = null
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
