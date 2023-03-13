package sergio.sastre.uitesting.sharedtest.paparazzi

import sergio.sastre.uitesting.sharedtest.paparazzi.wrapper.DeviceConfig
import sergio.sastre.uitesting.sharedtest.paparazzi.wrapper.Environment
import sergio.sastre.uitesting.sharedtest.paparazzi.wrapper.RenderingMode
import sergio.sastre.uitesting.utils.LibraryConfig

/**
 * A wrapper for allowed Paparazzi configuration in shared Tests.
 *
 * This is necessary because we cannot access Paparazzi dependencies directly in androidTests
 * (including shared Tests running on a device or emulator), which would throw
 * a class Duplication error at Runtime.
 *
 * WARNING:
 * @param renderingMode: if null, the screenshot is automatically resized to that of the View,
 *          considering the orientation (e.g. width and height switched for Landscape)
 * @param softButtons: false for all wrapped DeviceConfigs, contrary to Paparazzi's default.
 *          That's because of this default renderingMode: if softButtons ==  true, they overlap
 *          the view, and are displaded on top of it.
 */
class PaparazziConfig(
    val maxPercentageDiff: Double = 0.1,
    val deviceConfig: DeviceConfig = DeviceConfig.NEXUS_5,
    val softButtons: Boolean = false,
    val renderingMode: RenderingMode? = null,
    val environment: Environment? = null,
) : LibraryConfig
