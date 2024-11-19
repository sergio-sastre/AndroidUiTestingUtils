package sergio.sastre.uitesting.mapper.paparazzi

import sergio.sastre.uitesting.mapper.paparazzi.wrapper.AccessibilityRenderExtension
import sergio.sastre.uitesting.mapper.paparazzi.wrapper.DeviceConfig
import sergio.sastre.uitesting.mapper.paparazzi.wrapper.Environment
import sergio.sastre.uitesting.mapper.paparazzi.wrapper.RenderExtension
import sergio.sastre.uitesting.mapper.paparazzi.wrapper.RenderingMode
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig

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
 *          the view, and are displaced on top of it.
 */
data class PaparazziConfig(
    val maxPercentageDiff: Double = 0.1,
    val deviceConfig: DeviceConfig = DeviceConfig.NEXUS_5,
    val deviceSystemUiVisibility: DeviceSystemUiVisibility = DeviceSystemUiVisibility(),
    val environment: Environment? = null,
    val snapshotViewOffsetMillis: Long = 0L,
    val renderingMode: RenderingMode? = null,
    val renderExtensions: Set<RenderExtension> = emptySet(),
    val useDeviceResolution: Boolean = false,
    val validateAccessibility: Boolean = false,
) : LibraryConfig {

    fun overrideForDefaultAccessibility(): PaparazziConfig {
        return this.copy(
            deviceConfig = this.deviceConfig.increaseWidthForAccessibilityExtension(),
            renderingMode = RenderingMode.NORMAL,
            renderExtensions = setOf(AccessibilityRenderExtension()),
            validateAccessibility = false, // Paparazzi does not support it together with RenderExtensions
        )
    }
}

data class DeviceSystemUiVisibility(
    val softButtons: Boolean = false,
    val systemUi: Boolean = false,
)
