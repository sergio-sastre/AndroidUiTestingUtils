package sergio.sastre.uitesting.android_testify

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import dev.testify.ScreenshotRule
import dev.testify.TestDescription
import dev.testify.TestifyFeatures
import dev.testify.testDescription
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation
import sergio.sastre.uitesting.utils.utils.waitForMeasuredView

fun <T : Activity> ScreenshotRule<T>.setViewHolderForScreenshot(
    @LayoutRes layoutId: Int,
    createViewHolder: (ViewGroup) -> ViewHolder
): ScreenshotRule<T> =
    this
        .setTargetLayoutId(layoutId)
        .setViewModifications { targetLayout ->
            createViewHolder(targetLayout)
        }
        .setScreenshotViewProvider {
            it.getChildAt(0)
        }

fun <T : Activity> ScreenshotRule<T>.setScreenshotFirstView(): ScreenshotRule<T> =
    this.setScreenshotViewProvider {
        it.getChildAt(0)
    }

fun <T : Activity> ScreenshotRule<T>.setScreenshotFragment(
    fragment: Fragment,
    fragmentArgs: Bundle? = null,
): ScreenshotRule<T> =
    this.setViewModifications {
        (activity as FragmentActivity).supportFragmentManager.beginTransaction()
            .add(
                android.R.id.content,
                fragment.apply { if (fragmentArgs != null) arguments = fragmentArgs },
                "Android-Testify-Fragment"
            )
            .commitNow()
    }
        .setScreenshotViewProvider {
            (activity as FragmentActivity).supportFragmentManager
                .findFragmentByTag("Android-Testify-Fragment")!!.requireView()
        }

fun <T : Activity> ScreenshotRule<T>.assertSame(name: String?) {
    if (name != null) {
        // this is how to change the name of the screenshot file
        getInstrumentation().testDescription = TestDescription(
            methodName = name,
            testClass = getInstrumentation().testDescription.testClass
        )
    }
    assertSame()
}

// We need to ensure the view is attached to the screenshotRule activity window for testify to
// take the screenshot. This is especially important for dialogs
internal fun <T : Activity> ScreenshotRule<T>.setViewUnderTest(
    view: View,
): ScreenshotRule<T> = apply {
    setScreenshotViewProvider {
        getInstrumentation().run {
            runOnMainSync {
                (view.parent as ViewGroup?)?.removeAllViews()
            }
            waitForIdleSync()

            runOnMainSync {
                (activity.window.decorView as ViewGroup).addView(view)
            }
            waitForIdleSync()
        }
        waitForMeasuredView { view }
    }
}

internal fun <T : Activity> ScreenshotRule<T>.setBitmapCaptureMethod(
    bitmapCaptureMethod: BitmapCaptureMethod?,
): ScreenshotRule<T> = apply {
    when (bitmapCaptureMethod) {
        is BitmapCaptureMethod.Canvas -> {
            fun canvas(activity: Activity, targetView: View?): Bitmap? {
                return targetView?.drawToBitmap(bitmapCaptureMethod.config)
            }
            configure { captureMethod = ::canvas }
        }

        is BitmapCaptureMethod.PixelCopy -> {
            fun pixelCopy(activity: Activity, targetView: View?): Bitmap? {
                return targetView?.drawToBitmapWithElevation(
                    activity = activity,
                    config = bitmapCaptureMethod.config
                )
            }
            configure { captureMethod = ::pixelCopy }
        }

        null -> {/*no-op*/
        }
    }
}

internal fun <T : Activity> ScreenshotRule<T>.generateDiffs(
    generate: Boolean,
): ScreenshotRule<T> = apply {
    if (generate) {
        withExperimentalFeatureEnabled(TestifyFeatures.GenerateDiffs)
    }
}
