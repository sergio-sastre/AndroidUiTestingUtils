package sergio.sastre.uitesting.utils.activityscenario.orientation

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.test.core.app.ActivityScenario
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import sergio.sastre.uitesting.utils.common.Orientation

class OrientationTestWatcher(private val orientation: Orientation?) : TestWatcher() {

    var activityScenario: ActivityScenario<out Activity>? = null
        set(value) {
            field = value
            field?.setOrientation(orientation)
        }

    private fun <A : Activity> ActivityScenario<A>.setOrientation(orientation: Orientation?)
            : ActivityScenario<A> {
        var orientationHelper: OrientationHelper<A>? = null
        val activityScenario = this.onActivity {
            orientation?.run {
                orientationHelper = OrientationHelper(it)
                orientationHelper?.requestedOrientation = this.activityInfo
            }
        }
        orientationHelper?.afterActivityLaunched()
        return activityScenario
    }

    override fun finished(description: Description?) {
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }
}
