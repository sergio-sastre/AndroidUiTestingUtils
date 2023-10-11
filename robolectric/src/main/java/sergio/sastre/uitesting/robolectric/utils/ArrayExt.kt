package sergio.sastre.uitesting.robolectric.utils

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen

/**
 * Method to generate items for each Device in a form of an ItemForDevice. This is very useful for
 * parameterized robolectric screenshot tests that must run across several device configurations
 */
fun <T> Array<T>.forDevices(deviceScreens: List<DeviceScreen>): Array<ItemForDevice<T>> {
    val cartesianProductList = mutableListOf<ItemForDevice<T>>()
    for (testItem in this) {
        for (deviceScreen in deviceScreens) {
            cartesianProductList.add(
                ItemForDevice(testItem, deviceScreen)
            )
        }
    }
    return cartesianProductList.toTypedArray()
}