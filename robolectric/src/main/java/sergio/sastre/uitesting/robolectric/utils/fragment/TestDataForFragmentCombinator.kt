package sergio.sastre.uitesting.robolectric.utils.fragment

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.fragmentscenario.FragmentConfigItem

class TestDataForFragmentCombinator<T: Enum<T>>(uiStates: Array<T>) {

    private var testConfigItems: Array<TestDataForFragment<T>> = uiStates.map { item ->
        TestDataForFragment(uiState = item)
    }.toTypedArray()

    fun forDevices(vararg deviceScreens: DeviceScreen): TestDataForFragmentCombinator<T> = apply {
        val cartesianProductList = mutableListOf<TestDataForFragment<T>>()
        for (testItem in testConfigItems) {
            for (deviceScreen in deviceScreens) {
                cartesianProductList.add(
                    TestDataForFragment(
                        uiState = testItem.uiState,
                        device = deviceScreen,
                        config = testItem.config
                    )
                )
            }
        }
        testConfigItems = cartesianProductList.toTypedArray()
    }

    fun forConfigs(vararg configs: FragmentConfigItem): TestDataForFragmentCombinator<T> = apply {
        val cartesianProductList = mutableListOf<TestDataForFragment<T>>()
        for (testItem in testConfigItems) {
            for (config in configs) {
                cartesianProductList.add(
                    TestDataForFragment(
                        uiState = testItem.uiState,
                        device = testItem.device,
                        config = config
                    )
                )
            }
        }
        testConfigItems = cartesianProductList.toTypedArray()
    }

    fun combineAll() : Array<TestDataForFragment<T>> = testConfigItems.copyOf()
}
