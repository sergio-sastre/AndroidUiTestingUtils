package sergio.sastre.uitesting.utils.activityscenario

import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontSizeScale
import sergio.sastre.uitesting.utils.common.FontWeight
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

data class ActivityConfigItem(
    val orientation: Orientation? = null,
    val uiMode: UiMode? = null,
    val systemLocale: String? = null,
    val fontSize: FontSizeScale? = null,
    val displaySize: DisplaySize? = null,
    val fontWeight: FontWeight? = null,
) {
    val id: String
        get() {
            val nonNullProperties = mutableListOf<String>()
            systemLocale?.let { nonNullProperties.add(it.uppercase()) }
            uiMode?.let { nonNullProperties.add(it.name) }
            fontSize?.let { nonNullProperties.add("FONT_${it.valueAsName()}") }
            fontWeight?.let { nonNullProperties.add("WEIGHT_${it.name}") }
            displaySize?.let { nonNullProperties.add("DISPLAY_${it.name}") }
            orientation?.let { nonNullProperties.add(it.name) }
            return nonNullProperties.joinToString(separator = "_")
        }
}
