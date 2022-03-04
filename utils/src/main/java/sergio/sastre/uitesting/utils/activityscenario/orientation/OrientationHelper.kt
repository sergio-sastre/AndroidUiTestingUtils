package sergio.sastre.uitesting.utils.activityscenario.orientation

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Point
import androidx.test.espresso.Espresso
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import java.lang.RuntimeException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Class responsible for waiting till the activity actually rotates
 *
 * Taken from : https://github.com/Shopify/android-testify/issues
 */
internal class OrientationHelper<T : Activity>(
    private val activity: T
) {
    var deviceOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    var requestedOrientation: Int? = null
    private lateinit var lifecycleLatch: CountDownLatch

    fun afterActivityLaunched() {

        // Set the orientation based on how the activity was launched
        deviceOrientation = if (activity.isLandscape)
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

        this.requestedOrientation?.let {
            if (!activity.isRequestedOrientation(it)) {
                activity.changeOrientation(it)

                // Re-capture the orientation based on user requested value
                deviceOrientation = it
            }
        }
        this.requestedOrientation = null
    }

    private val Activity.isLandscape: Boolean
        get() {
            val size = Point(-1, -1)
            this.windowManager?.defaultDisplay?.getRealSize(size)
            return size.y < size.x
        }

    /**
     * Check if the activity's current orientation matches what was requested
     */
    private fun Activity.isRequestedOrientation(requestedOrientation: Int): Boolean {
        return (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && this.isLandscape) ||
                (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && !this.isLandscape)
    }

    /**
     * Lifecycle callback. Wait for the activity under test to completely resume after configuration change.
     */
    private fun lifecycleCallback(activity: Activity, stage: Stage) {
        if (activity::class.java == this.activity::class.java) {
            if (stage == Stage.RESUMED) {
                lifecycleLatch.countDown()
            }
        }
    }

    private fun Activity.changeOrientation(requestedOrientation: Int) {
        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(::lifecycleCallback)

        Espresso.onIdle()

        val rotationLatch = CountDownLatch(1)
        lifecycleLatch = CountDownLatch(1)

        this.runOnUiThread {
            this.requestedOrientation = requestedOrientation
            rotationLatch.countDown()
        }

        // Wait for the rotation request to be made
        if (!rotationLatch.await(30, TimeUnit.SECONDS)) {
            throw RuntimeException("Failed to apply requested rotation.")
        }

        try {
            // Wait for the activity to fully resume
            if (!lifecycleLatch.await(30, TimeUnit.SECONDS)) {
                throw RuntimeException("Activity did not resume.")
            }
        } finally {
            ActivityLifecycleMonitorRegistry.getInstance()
                .removeLifecycleCallback(::lifecycleCallback)
        }
    }
}
