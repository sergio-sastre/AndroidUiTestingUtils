package sergio.sastre.uitesting.robolectric.utils.view

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ViewConfigItem

class TestDataForViewBuilder<T: Enum<T>>(uiStates: Array<T>) {

    private var testConfigItems: Array<TestDataForView<T>> = uiStates.map { item ->
        TestDataForView(uiState = item)
    }.toTypedArray()

    fun forDevices(vararg deviceScreens: DeviceScreen): TestDataForViewBuilder<T> = apply {
        val cartesianProductList = mutableListOf<TestDataForView<T>>()
        for (testItem in testConfigItems) {
            for (deviceScreen in deviceScreens) {
                cartesianProductList.add(
                    TestDataForView(
                        uiState = testItem.uiState,
                        device = deviceScreen,
                        config = testItem.config
                    )
                )
            }
        }
        testConfigItems = cartesianProductList.toTypedArray()
    }

    fun forConfigs(vararg configs: ViewConfigItem): TestDataForViewBuilder<T> = apply {
        val cartesianProductList = mutableListOf<TestDataForView<T>>()
        for (testItem in testConfigItems) {
            for (config in configs) {
                cartesianProductList.add(
                    TestDataForView(
                        uiState = testItem.uiState,
                        device = testItem.device,
                        config = config
                    )
                )
            }
        }
        testConfigItems = cartesianProductList.toTypedArray()
    }

    fun generateAllCombinations() : Array<TestDataForView<T>> = testConfigItems.copyOf()
}
