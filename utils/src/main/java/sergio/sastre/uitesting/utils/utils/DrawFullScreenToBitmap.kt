package sergio.sastre.uitesting.utils.utils

import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice

/**
 * Captures a screenshot of the entire screen, including the Status Bar and Navigation Bar.
 *
 * WARNING: For screenshot testing, it is NOT recommended to use this unless:
 * 1. You exclude the status bar and navigation bar from the comparison.
 * 2. You ensure they are reproducible (e.g., by using [sergio.sastre.uitesting.utils.testrules.systemui.SystemUiTestRule]).
 */
fun drawFullScreenToBitmap(): Bitmap =
    UiDevice.getInstance(getInstrumentation()).apply { waitForIdle() }.takeScreenshot()!!