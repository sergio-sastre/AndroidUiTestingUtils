package sergio.sastre.uitesting.utils.utils

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import sergio.sastre.uitesting.utils.activityscenario.orientation.OrientationHelper
import java.lang.IllegalStateException
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator
import sergio.sastre.uitesting.utils.common.Orientation

/**
 * Returns a view with [layoutId] using the context of [Activity], and attaches it to the root.
 * This is meant to snapshot test custom views, ViewHolders, Dialogs, etc. after launching an
 * Activity via ActivityScenarioConfigurator.
 *
 * All views inflated with the context of this Activity inherit its configuration, like Locale,
 * FontSize, UiMode, etc.
 */
fun Activity.inflate(@LayoutRes layoutId: Int, id: Int = android.R.id.content): View {
    val root = findViewById<View>(id) as ViewGroup
    return LayoutInflater.from(this).inflate(layoutId, root, true)
}

/**
 * Wait for Activity. This is a copy-cat of Shot method to be able to extend it conveniently.
 */
fun <A : Activity> ActivityScenario<A>.waitForActivity(): A {
    var activity: A? = null
    onActivity {
        activity = it
    }
    return if (activity != null) {
        activity!!
    } else {
        throw IllegalStateException("The activity scenario could not be initialized.")
    }
}

/**
 * Setup the view under test and wait till the view is ready and main thread is idle
 * before recording any snapshot
 *
 * @param actionToDo: Everything that drives the view to the state we want to snapshot.
 * This also includes inflation
 */
fun <V> waitForView(actionToDo: () -> V): V =
    InstrumentationRegistry.getInstrumentation().run {
        var view: V? = null
        runOnMainSync { view = actionToDo() }
        waitForIdleSync()
        return view!!
    }

/**
 * Rotates the Activity to the given [orientation]
 *
 * WARNING: Do not use this method to take snapshots. It does not ensure that the device is ready
 * for a snapshot right after calling this method.
 * If you are writing screenshot tests for different orientations, use
 * the [ActivityScenarioConfigurator] instead
 */
fun Activity.rotateTo(orientation: Orientation) {
    OrientationHelper(this).apply {
        requestedOrientation = orientation.activityInfo
        afterActivityLaunched()
    }
}