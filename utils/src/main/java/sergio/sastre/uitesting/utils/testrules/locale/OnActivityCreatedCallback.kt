package sergio.sastre.uitesting.utils.testrules.locale

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal interface OnActivityCreatedCallback: Application.ActivityLifecycleCallbacks {

    override fun onActivityStarted(activity: Activity) {
        // no-op
    }

    override fun onActivityResumed(activity: Activity) {
        // no-op
    }

    override fun onActivityPaused(activity: Activity) {
        // no-op
    }

    override fun onActivityStopped(activity: Activity) {
        // no-op
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
        // no-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        // no-op
    }
}