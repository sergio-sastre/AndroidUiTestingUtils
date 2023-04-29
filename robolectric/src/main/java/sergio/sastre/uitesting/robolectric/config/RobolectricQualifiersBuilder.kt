package sergio.sastre.uitesting.robolectric.config

import org.robolectric.RuntimeEnvironment.setQualifiers
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.common.Orientation

object RobolectricQualifiersBuilder {

    fun setQualifiers(
        deviceScreen: DeviceScreen?,
        configOrientation: Orientation?,
    ) {
        if (deviceScreen != null) {
            deviceScreen.apply {
                val listOfQualifiers = mutableListOf<String>()
                widthDp.let { listOfQualifiers.add("w${it}dp") }
                heightDp.let { listOfQualifiers.add("h${it}dp") }
                size.let { listOfQualifiers.add(it.qualifier) }
                aspect.let { listOfQualifiers.add(it.qualifier) }
                round?.let { listOfQualifiers.add(it.qualifier) }
                orientationQualifier(configOrientation).let { listOfQualifiers.add(it) }
                type?.let { listOfQualifiers.add(it.qualifier) }
                density.let { listOfQualifiers.add(it.qualifier) }

                setQualifiers(listOfQualifiers.joinToString(separator = "-"))
            }
        } else {
            configOrientation?.let {
                val orientation = if (it == Orientation.PORTRAIT) "port" else "land"
                setQualifiers("+$orientation")
            }
        }
    }

    /**
     * Returns the corresponding orientation, where config orientation always wins over
     * the device default orientation.
     */
    private fun DeviceScreen.orientationQualifier(orientation: Orientation?): String =
        orientation?.let {
            if (it == Orientation.PORTRAIT) "port" else "land"
        } ?: this.defaultOrientation.qualifier
}
