package sergio.sastre.uitesting.android_testify.screenshotscenario

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.TestDescription
import dev.testify.TestifyFeatures
import dev.testify.scenario.ScreenshotScenarioRule
import dev.testify.testDescription
import sergio.sastre.uitesting.utils.crosslibrary.config.BitmapCaptureMethod
import sergio.sastre.uitesting.utils.utils.drawToBitmapWithElevation
import sergio.sastre.uitesting.utils.utils.waitForMeasuredView

fun ScreenshotScenarioRule.assertSame(name: String?) {
    if (name != null) {
        // this is how to change the name of the screenshot file
        InstrumentationRegistry.getInstrumentation().testDescription = TestDescription(
            methodName = name,
            testClass = InstrumentationRegistry.getInstrumentation().testDescription.testClass
        )
    }
    assertSame()
}

fun ScreenshotScenarioRule.waitForIdleSync(): ScreenshotScenarioRule = apply {
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
}

internal fun ScreenshotScenarioRule.setDialogViewUnderTest(
    view: View,
): ScreenshotScenarioRule = apply {
    setScreenshotViewProvider {
        InstrumentationRegistry.getInstrumentation().run {
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

internal fun ScreenshotScenarioRule.setViewUnderTest(
    view: View,
): ScreenshotScenarioRule = apply {
    setScreenshotViewProvider {
        waitForMeasuredView { view }
    }
}

internal fun ScreenshotScenarioRule.setComposeViewUnderTest(
    view: View,
): ScreenshotScenarioRule = apply {
    setScreenshotViewProvider { view }
}

internal fun ScreenshotScenarioRule.setBitmapCaptureMethod(
    bitmapCaptureMethod: BitmapCaptureMethod?,
): ScreenshotScenarioRule = apply {
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

        null -> { /*no-op*/ }
    }
}

fun ScreenshotScenarioRule.generateDiffs(
    generate: Boolean,
): ScreenshotScenarioRule = apply {
    if (generate) {
        TestifyFeatures.GenerateDiffs.setEnabled(true)
    }
}
