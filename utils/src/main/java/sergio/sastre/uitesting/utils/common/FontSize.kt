package sergio.sastre.uitesting.utils.common

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
import androidx.annotation.Discouraged
import org.junit.Assume

interface FontSizeScale {

    companion object {
        fun supportedValuesForCurrentSdk(): Array<FontSizeScale> =
            when (SDK_INT >= UPSIDE_DOWN_CAKE) {
                true -> FontSizeNonLinear.entries.toTypedArray()
                false -> FontSizeLinear.entries.toTypedArray()
            }
    }

    val scale: Float

    fun valueAsName(): String =
        when (this is Enum<*>) {
            true -> this.name
            false -> this.scale.toString().replace(".", "_") + "f"
        }
}

class FontSizeScaleValue(override val scale: Float) : FontSizeScale

private const val MAX_LINEAR_FONT_SCALE = 1.3f
private const val MAX_NON_LINEAR_FONT_SCALE = 2.0f

enum class FontSize(override val scale: Float) : FontSizeScale {
    @Discouraged("Use XLARGE or MAXIMUM_LINEAR instead")
    HUGE(MAX_LINEAR_FONT_SCALE),

    SMALL(0.85f),
    NORMAL(1f),
    LARGE(1.15f),
    XLARGE(MAX_LINEAR_FONT_SCALE),
    XXLARGE(1.5f),
    XXXLARGE(1.8f),
    LARGEST(maxFontSizeScaleSupported),

    MAXIMUM_LINEAR(MAX_LINEAR_FONT_SCALE),
    MAXIMUM_NON_LINEAR(MAX_NON_LINEAR_FONT_SCALE);

    val value: String = scale.toString()
}

enum class FontSizeLinear(override val scale: Float) : FontSizeScale {
    SMALL(0.85f),
    NORMAL(1f),
    LARGE(1.15f),
    LARGEST(MAX_LINEAR_FONT_SCALE);

    val value: String = scale.toString()
}

enum class FontSizeNonLinear(override val scale: Float) : FontSizeScale {
    SMALL(0.85f),
    NORMAL(1f),
    LARGE(1.15f),
    XLARGE(1.3f),
    XXLARGE(1.5f),
    XXXLARGE(1.8f),
    LARGEST(MAX_NON_LINEAR_FONT_SCALE);

    val value: String = scale.toString()
}

private val maxFontSizeScaleSupported =
    when (SDK_INT >= UPSIDE_DOWN_CAKE) {
        true -> MAX_NON_LINEAR_FONT_SCALE
        false -> MAX_LINEAR_FONT_SCALE
    }

fun assumeSdkSupports(fontSizeScale: FontSizeScale?) {
    fontSizeScale?.scale?.run {
        Assume.assumeTrue(this <= maxFontSizeScaleSupported)
    }
}
