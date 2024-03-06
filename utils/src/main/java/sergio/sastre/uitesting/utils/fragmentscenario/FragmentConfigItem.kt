package sergio.sastre.uitesting.utils.fragmentscenario

import androidx.annotation.StyleRes
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontSizeScale
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

data class FragmentConfigItem(
    val orientation: Orientation? = null,
    val uiMode: UiMode? = null,
    val locale: String? = null,
    val fontSize: FontSizeScale? = null,
    val displaySize: DisplaySize? = null,
    @StyleRes val theme: Int? = null,
) {
    val id : String
        get() {
            val nonNullProperties = mutableListOf<String>()
            locale?.let { nonNullProperties.add(it.uppercase()) }
            uiMode?.let { nonNullProperties.add(it.name) }
            theme?.let {
                val themeInfo = getInstrumentation().targetContext.resources.getResourceName(it)
                // The themeInfo will be in the format: "android:style/Theme.XXX"
                val theme = themeInfo.substring(themeInfo.lastIndexOf('/') + 1)
                nonNullProperties.add(theme.replace(".","_").uppercase())
            }
            fontSize?.let { nonNullProperties.add("FONT_${it.valueAsName()}") }
            displaySize?.let { nonNullProperties.add("DISPLAY_${it.name}") }
            orientation?.let { nonNullProperties.add(it.name) }
            return nonNullProperties.joinToString(separator = "_")
        }
}
