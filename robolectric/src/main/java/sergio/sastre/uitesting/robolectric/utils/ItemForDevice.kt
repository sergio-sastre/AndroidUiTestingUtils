package sergio.sastre.uitesting.robolectric.utils

import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen

/**
 * Class to be used in parameterized tests to combine test items that must be run under different
 * device configurations
 *
 * If the itemValue is an Enum, you can use .toString() to uniquely identify your test, e.g.
 * for screenshots
 */
data class ItemForDevice<T>(val itemValue: T, val device: DeviceScreen){
    override fun toString(): String {
        return when (itemValue) {
            is Enum<*> -> "${(itemValue as Enum<*>).name}_${device.name}"
            else -> this.toString()
        }
    }
}
