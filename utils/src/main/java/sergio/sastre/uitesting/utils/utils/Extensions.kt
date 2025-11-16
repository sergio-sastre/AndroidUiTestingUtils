package sergio.sastre.uitesting.utils.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.Instrumentation
import android.content.pm.PackageManager
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Discouraged
import androidx.annotation.LayoutRes
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.activityscenario.orientation.OrientationHelper
import java.lang.IllegalStateException
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator
import sergio.sastre.uitesting.utils.common.Orientation
import java.io.BufferedReader
import java.io.InputStreamReader

private fun waitForCompletion(descriptor: ParcelFileDescriptor) {
    BufferedReader(InputStreamReader(ParcelFileDescriptor.AutoCloseInputStream(descriptor))).use {
        it.readText()
    }
}

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
 * Returns a view with [layoutId] using the context of [Activity], and attaches it to the root.
 * In addition to [inflate], it also waits synchronously till the main thread is Idle.
 *
 * This is meant to snapshot test custom views, ViewHolders, Dialogs, etc. after launching an
 * Activity via ActivityScenarioConfigurator.
 *
 * All views inflated with the context of this Activity inherit its configuration, like Locale,
 * FontSize, UiMode, etc.
 */
@SuppressLint("DiscouragedApi")
fun Activity.inflateAndWaitForIdle(
    @LayoutRes layoutId: Int,
    id: Int = android.R.id.content,
): View {
    val root = findViewById<View>(id) as ViewGroup
    return waitForView {
        LayoutInflater.from(this).inflate(layoutId, root, true)
    }
}

/**
 * Returns an [Activity] from an [ActivityScenario].
 * This is a copy-cat of Shot method to be able to extend it conveniently.
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

val <A : Activity> ActivityScenario<A>.rootView: View
    get() = waitForActivity().window.decorView

/**
 * Setup the view under test and wait till the view is ready and main thread is idle. This helps
 * before recording any snapshot.
 *
 * @param actionToDo: Everything that drives the view to the state we want to snapshot.
 * This also includes inflation
 */
@Discouraged(
    message = "Consider using waitForMeasuredView, waitForMeasuredViewHolder or " +
            "waitForMeasuredDialog instead. This method might become private in the future."
)
fun <V> waitForView(actionToDo: () -> V): V =
    getInstrumentation().run {
        waitForIdleSync()
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
        setLayoutOrientation()
    }
}

fun Instrumentation.waitForExecuteShellCommand(command: String) {
    waitForCompletion(uiAutomation.executeShellCommand(command))
}

/**
 * Waits for the Ui thread to be Idle and calculates the expected dimensions of the
 * [RecyclerView.ViewHolder].
 * This is necessary to take a screenshot with the expected size of the dialog. For that, we need
 * to provide [RecyclerView.ViewHolder]'s measuredHeight/measuredWidth to the method used to take/verify
 * the screenshot.
 */
@SuppressLint("DiscouragedApi")
fun waitForMeasuredViewHolder(
    exactHeightPx: Int? = null,
    exactWidthPx: Int? = null,
    actionToDo: () -> RecyclerView.ViewHolder,
): RecyclerView.ViewHolder =
    waitForView { actionToDo() }.apply {
        itemView.setDimensions(exactHeightPx, exactWidthPx)
    }

/**
 * Waits for the Ui thread to be Idle and calculates the expected dimensions of the
 * [View].
 * This is necessary to take a screenshot with the expected size of the dialog. For that, we need
 * to provide [View.getMeasuredHeight]/[View.getMeasuredWidth] to the method used to take/verify
 * the screenshot.
 */
@SuppressLint("DiscouragedApi")
fun waitForMeasuredView(
    exactHeightPx: Int? = null,
    exactWidthPx: Int? = null,
    actionToDo: () -> View,
): View =
    waitForView { actionToDo() }.apply {
        setDimensions(exactHeightPx, exactWidthPx)
    }

/**
 * Waits for the Ui thread to be Idle and calculates the expected dimensions of the
 * [Dialog].
 * This is necessary to take a screenshot with the expected size of the dialog. For that, we need
 * to provide [Dialog]'s measuredHeight/measuredWidth to the method used to take/verify
 * the screenshot.
 */
@SuppressLint("DiscouragedApi")
fun waitForMeasuredDialog(
    exactWidthPx: Int? = null,
    exactHeightPx: Int? = null,
    actionToDo: () -> Dialog,
): Dialog =
    waitForView { actionToDo().apply { show() } }.apply {
        window!!.decorView.setDimensions(exactHeightPx, exactWidthPx)
    }

private fun View.setDimensions(
    exactHeightPx: Int? = null,
    exactWidthPx: Int? = null,
) {
    getInstrumentation().apply {
        runOnMainSync {
            MeasureViewHelpers
                .setupView(this@setDimensions)
                .also {
                    if (exactHeightPx != null) {
                        it.setExactHeightPx(exactHeightPx)
                    }
                }
                .setExactWidthPx(exactWidthPx ?: this@setDimensions.width)
                .layout()
        }
        waitForIdleSync()
    }
}

@SuppressLint("DiscouragedApi")
fun Activity.waitForComposeView(): ComposeView =
    waitForView {
        window
            .decorView
            .findViewById<ViewGroup>(android.R.id.content)
            .getChildAt(0) as ComposeView
    }


fun grantChangeConfigurationIfNeeded() {
    val instrumentation = getInstrumentation()
    if (ContextCompat.checkSelfPermission(
            instrumentation.targetContext,
            Manifest.permission.CHANGE_CONFIGURATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        /*
      The following fails on API 27 or before:
      UiAutomation.grantRuntimePermission(...)

      Therefore better to use .executeShellCommand(..)
     */
        instrumentation.waitForExecuteShellCommand(
            "pm grant ${instrumentation.targetContext.packageName} android.permission.CHANGE_CONFIGURATION"
        )
    }
}