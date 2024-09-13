package sergio.sastre.uitesting.android_testify

import android.graphics.Rect
import android.view.ViewGroup
import androidx.annotation.ColorInt
import dev.testify.CompareMethod
import dev.testify.core.ExclusionRectProvider
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.crosslibrary.config.LibraryConfig

class AndroidTestifyConfig(
    val bitmapCaptureMethod: BitmapCaptureMethod? = null,
    val exactness: Float = 0.9f,
    val enableReporter: Boolean = false,
    val generateDiffs: Boolean = true,
    val animationsDisabled: Boolean = true,
    val hideCursor: Boolean = true,
    val hidePasswords: Boolean = true,
    val hideScrollbars: Boolean = true,
    val hideSoftKeyboard: Boolean = true,
    val hideTextSuggestions: Boolean = true,
    val useSoftwareRenderer: Boolean = false,
    val exclusionRects: MutableSet<Rect> = HashSet(),
    val exclusionRectProvider: ExclusionRectProvider? = null,
    val compareMethod: CompareMethod? = null,
    val pauseForInspection: Boolean = false,
    @ColorInt val backgroundColor: Int? = null,
) : LibraryConfig
