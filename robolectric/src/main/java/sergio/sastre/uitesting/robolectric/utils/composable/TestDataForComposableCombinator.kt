package sergio.sastre.uitesting.robolectric.utils.composable

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ComposableConfigItem

class TestDataForComposableCombinator<T: Enum<T>>(uiStates: Array<T>) {

    private var testDataItems: Array<TestDataForComposable<T>> = uiStates.map { item ->
        TestDataForComposable(uiState = item)
    }.toTypedArray()

    fun forDevices(vararg deviceScreens: DeviceScreen): TestDataForComposableCombinator<T> = apply {
        val cartesianProductList = mutableListOf<TestDataForComposable<T>>()
        for (testItem in testDataItems) {
            for (deviceScreen in deviceScreens) {
                cartesianProductList.add(
                    TestDataForComposable(
                        uiState = testItem.uiState,
                        device = deviceScreen,
                        config = testItem.config
                    )
                )
            }
        }
        testDataItems = cartesianProductList.toTypedArray()
    }

    fun forConfigs(vararg configs: ComposableConfigItem): TestDataForComposableCombinator<T> = apply {
        val cartesianProductList = mutableListOf<TestDataForComposable<T>>()
        for (testItem in testDataItems) {
            for (config in configs) {
                cartesianProductList.add(
                    TestDataForComposable(
                        uiState = testItem.uiState,
                        device = testItem.device,
                        config = config
                    )
                )
            }
        }
        testDataItems = cartesianProductList.toTypedArray()
    }

    fun combineAll() : Array<TestDataForComposable<T>> = testDataItems.copyOf()
}
