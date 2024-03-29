package sergio.sastre.uitesting.robolectric.utils.activity

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.activityscenario.ActivityConfigItem

class TestDataForActivityCombinator<T: Enum<T>>(uiStates: Array<T>) {

    private var testConfigItems: Array<TestDataForActivity<T>> = uiStates.map { item ->
        TestDataForActivity(uiState = item)
    }.toTypedArray()

    fun forDevices(vararg deviceScreens: DeviceScreen): TestDataForActivityCombinator<T> = apply {
        val cartesianProductList = mutableListOf<TestDataForActivity<T>>()
        for (testItem in testConfigItems) {
            for (deviceScreen in deviceScreens) {
                cartesianProductList.add(
                    TestDataForActivity(
                        uiState = testItem.uiState,
                        device = deviceScreen,
                        config = testItem.config
                    )
                )
            }
        }
        testConfigItems = cartesianProductList.toTypedArray()
    }

    fun forConfigs(vararg configs: ActivityConfigItem): TestDataForActivityCombinator<T> = apply {
        val cartesianProductList = mutableListOf<TestDataForActivity<T>>()
        for (testItem in testConfigItems) {
            for (config in configs) {
                cartesianProductList.add(
                    TestDataForActivity(
                        uiState = testItem.uiState,
                        device = testItem.device,
                        config = config
                    )
                )
            }
        }
        testConfigItems = cartesianProductList.toTypedArray()
    }

    fun combineAll() : Array<TestDataForActivity<T>> = testConfigItems.copyOf()
}
