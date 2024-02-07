package sergio.sastre.uitesting.utils.common

import android.os.Build
import androidx.annotation.Discouraged
import org.junit.Assume

interface FontSizeScale {
    val scale: Float

    fun valueAsName(): String =
        when (this is FontSize) {
            true -> this.name
            false -> this.scale.toString()
        }
}

class FontSizeScaleValue(override val scale: Float) : FontSizeScale

private const val MAX_LINEAR_FONT_SCALE = 1.3f
private const val MAX_NON_LINEAR_FONT_SCALE = 2.0f

enum class FontSize(override val scale: Float) : FontSizeScale {
    SMALL(0.85f),
    NORMAL(1f),
    LARGE(1.15f),
    @Discouraged("Use XLARGE instead") HUGE(MAX_LINEAR_FONT_SCALE),
    XLARGE(MAX_LINEAR_FONT_SCALE),
    XXLARGE(1.5f),
    XXXLARGE(1.8f),
    LARGEST(MAX_NON_LINEAR_FONT_SCALE);

    val value: String = scale.toString()
}

fun assumeSupportsFontSizeScale(fontSizeScale: FontSizeScale) {
    if (fontSizeScale.scale > MAX_LINEAR_FONT_SCALE) {
        Assume.assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    }
}
