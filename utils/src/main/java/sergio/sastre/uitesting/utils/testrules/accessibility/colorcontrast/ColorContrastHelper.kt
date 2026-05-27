package sergio.sastre.uitesting.utils.testrules.accessibility.colorcontrast

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.ViewTreeObserver
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal class ColorContrastHelper<T : Activity>(
    private val activity: T
) {
    private lateinit var lifecycleLatch: CountDownLatch
    private lateinit var layoutLatch: CountDownLatch

    private fun lifecycleCallback(newActivity: Activity, stage: Stage) {
        if (newActivity::class.java == this.activity::class.java && stage == Stage.RESUMED) {
            getInstrumentation().runOnMainSync {
                val decorView = newActivity.window.decorView
                decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        layoutLatch.countDown()
                    }
                })
            }
            lifecycleLatch.countDown()
        }
    }

    fun applyColorContrast(colorContrast: ColorContrast) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            return
        }

        if (colorContrast.value == getCurrentContrastValue()){
            return
        }

        lifecycleLatch = CountDownLatch(1)
        layoutLatch = CountDownLatch(1)

        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(::lifecycleCallback)

        try {
            getInstrumentation().waitForIdleSync()
            Espresso.onIdle()

            setColorContrast(colorContrast)

            // Wait for the activity to fully resume
            if (lifecycleLatch.await(5, TimeUnit.SECONDS)) {
                // Wait for the actual layout pass on the new activity
                if (!layoutLatch.await(2, TimeUnit.SECONDS)) {
                    Log.w(
                        ColorContrastHelper::class.java.simpleName,
                        "Layout pass did not happen in time after activity resume."
                    )
                }
            } else {
                Log.w(
                    ColorContrastHelper::class.java.simpleName,
                    "Activity did not resume after setting color contrast. It might not have been recreated."
                )
            }
            getInstrumentation().waitForIdleSync()
            Espresso.onIdle()
        } catch (e: Exception) {
            Log.e(ColorContrastHelper::class.java.simpleName, "Error setting color contrast", e)
        } finally {
            ActivityLifecycleMonitorRegistry.getInstance()
                .removeLifecycleCallback(::lifecycleCallback)
        }
    }

    private fun getCurrentContrastValue(): Float? {
        return try {
            val contrastAsString = getInstrumentation().waitForExecuteShellCommand("settings get secure contrast_level")
            contrastAsString.substringAfterLast("=").trim().toFloatOrNull()
        } catch (_: Exception) {
            null
        }
    }

    @Throws(IOException::class)
    private fun setColorContrast(colorContrast: ColorContrast) {
        getInstrumentation().waitForExecuteShellCommand("settings put secure contrast_level ${colorContrast.value}")
    }
}
