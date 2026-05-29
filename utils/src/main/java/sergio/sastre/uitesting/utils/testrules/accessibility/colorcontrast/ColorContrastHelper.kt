package sergio.sastre.uitesting.utils.testrules.accessibility.colorcontrast

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
internal class ColorContrastHelper(
    private val targetColorContrast: ColorContrast,
    private val renderingOffsetInMillis: Long,
) {

    private val TAG: String = ColorContrastHelper::class.java.simpleName

    fun applyColorContrast() {
        val context = getInstrumentation().targetContext
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        if (targetColorContrast.value == uiModeManager.contrast) {
            return
        }

        val contrastLatch = CountDownLatch(1)

        val listener = UiModeManager.ContrastChangeListener { newContrast ->
            if (newContrast == targetColorContrast.value) {
                // required since it might take a short while before the UI changes are applied
                Thread.sleep(renderingOffsetInMillis)
                contrastLatch.countDown()
            }
        }
        uiModeManager.addContrastChangeListener(context.mainExecutor, listener)

        try {
            ColorContrastSetting.setColorContrast(targetColorContrast)

            // 1. Wait for system to acknowledge the change
            if (!contrastLatch.await(5, TimeUnit.SECONDS)) {
                Log.w(TAG, "System color contrast change timed out.")
            }

            getInstrumentation().waitForIdleSync()
            Espresso.onIdle()

        } catch (e: Exception) {
            Log.e(TAG, "Error setting color contrast", e)
        } finally {
            uiModeManager.removeContrastChangeListener(listener)
        }
    }
}
