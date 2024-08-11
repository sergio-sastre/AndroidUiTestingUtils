package sergio.sastre.uitesting.android_testify

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.fragment.app.FragmentActivity
import dev.testify.CaptureMethod
import androidx.activity.compose.setContent
import androidx.annotation.ColorInt
import androidx.compose.ui.platform.AbstractComposeView
import androidx.core.view.forEach
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.ScreenshotRule
import dev.testify.core.TestifyConfiguration
import dev.testify.core.processor.capture.pixelCopyCapture
import dev.testify.internal.helpers.findRootView
import junit.framework.TestCase.assertTrue
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator
import sergio.sastre.uitesting.utils.activityscenario.ComposableConfigItem
import sergio.sastre.uitesting.utils.common.Orientation
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Deprecated("For Android-Testify 3.0.0+, use ScreenshotScenarioRuleForComposable instead")
open class ComposableScreenshotRuleWithConfiguration(
    exactness: Float = 0.9f,
    initialTouchMode: Boolean = false,
    enableReporter: Boolean = false,
    config: ComposableConfigItem? = null,
    @ColorInt activityBackgroundColor: Int? = null,
    private val composeTestRule: ComposeTestRule = createEmptyComposeRule()
) : ScreenshotRule<FragmentActivity>(
    activityClass = getActivityClassFor(config?.orientation),
    enableReporter = enableReporter,
    initialTouchMode = initialTouchMode,
    configuration = TestifyConfiguration(exactness = exactness)
) {
    init {
        ActivityScenarioConfigurator.ForComposable()
            .apply {
                config?.uiMode?.run { setUiMode(this) }
                config?.locale?.run { setLocale(this) }
                config?.fontSize?.run { setFontSize(this) }
                config?.displaySize?.run { setDisplaySize(this) }
                activityBackgroundColor?.run { setActivityBackgroundColor(this) }
            }
    }

    lateinit var composeFunction: @Composable () -> Unit
    private var composeActions: ((ComposeTestRule) -> Unit)? = null
    private var captureMethod: CaptureMethod = ::pixelCopyCapture

    private fun Activity.disposeComposition() {
        val syncLatch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            try {
                findViewById<ViewGroup>(android.R.id.content)?.let { viewGroup ->
                    viewGroup.visibility = View.GONE
                    viewGroup.disposeCompositions()
                }
            } finally {
                syncLatch.countDown()
            }
        }
        assertTrue("Failed to synchronize composition", syncLatch.await(3, TimeUnit.SECONDS))
    }

    private fun ViewGroup.disposeCompositions() {
        this.forEach { child ->
            if (child is ViewGroup) child.disposeCompositions()
        }
        (this as? AbstractComposeView)?.disposeComposition()
    }

    open fun onCleanUp(activity: Activity) {
        activity.disposeComposition()
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.captureMethod = captureMethod }")
    )
    override fun setCaptureMethod(captureMethod: CaptureMethod?): ComposableScreenshotRuleWithConfiguration {
        configure {
            this@configure.captureMethod = captureMethod
        }
        return this
    }

    /**
     * Set a screenshot view provider to capture only the @Composable bounds
     */
    override fun beforeAssertSame() {
        super.beforeAssertSame()
        super.configure {
            captureMethod = this@ComposableScreenshotRuleWithConfiguration.captureMethod
        }
        setScreenshotViewProvider {
            it.getChildAt(0)
        }
    }

    /**
     * Render the composable function after the activity has loaded.
     */
    override fun afterActivityLaunched() {
        activity.runOnUiThread {
            activity.setContent {
                composeFunction()
            }
        }
        composeTestRule.waitForIdle()
        super.afterActivityLaunched()
    }

    /**
     * Proactively dispose of any compositions after the screenshot has been taken.
     */
    override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {
        super.afterScreenshot(activity, currentBitmap)
        onCleanUp(activity)
    }

    /**
     * Used to provide a @Composable function to be rendered in the screenshot.
     */
    fun setCompose(composable: @Composable () -> Unit): ComposableScreenshotRuleWithConfiguration {
        composeFunction = composable
        return this
    }

    /**
     * UI tests in Compose use semantics to interact with the UI hierarchy. [setComposeActions] allows you to manipulate
     * your Compose UI using [Finders](https://developer.android.com/jetpack/compose/testing#finders) and [Actions](https://developer.android.com/jetpack/compose/testing#actions).
     *
     * The provided [actions] lambda will be invoked after the Activity is loaded, before any Espresso actions and before
     * the screenshot is taken.
     *
     * **For more information:**
     * - [ComposeTestRule](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/junit4/ComposeTestRule)
     * - [Testing your Compose layout](https://developer.android.com/jetpack/compose/testing)
     *
     * @param actions: A lambda which provides a [ComposeTestRule] instance that can be used with semantics to interact
     * with the UI hierarchy.
     *
     */
    fun setComposeActions(actions: (ComposeTestRule) -> Unit): ComposableScreenshotRuleWithConfiguration {
        composeActions = actions
        return this
    }

    /**
     * Test lifecycle method.
     * Invoked after layout inflation and all view modifications have been applied.
     */
    override fun afterInitializeView(activity: Activity) {
        composeActions?.invoke(composeTestRule)
        composeTestRule.waitForIdle()
        super.afterInitializeView(activity)
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before the screenshot is taken.
     */
    override fun beforeScreenshot(activity: Activity) {
        val targetView = activity.findRootView(rootViewId).getChildAt(0)
        if (targetView.width == 0 && targetView.height == 0)
            throw IllegalStateException(
                "Target view has 0 size. " +
                        "Verify if you have provided a ComposeTestRule instance to ComposableScreenshotRule."
            )

        super.beforeScreenshot(activity)
    }

    /**
     * Modifies the method-running Statement to implement this test-running rule.
     */
    override fun apply(base: Statement, description: Description): Statement {
        val statement = composeTestRule.apply(base, description)
        return super.apply(statement, description)
    }

    /**
     * Set the configuration for the ComposableScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     */
    override fun configure(configureRule: TestifyConfiguration.() -> Unit): ComposableScreenshotRuleWithConfiguration {
        super.configure(configureRule)

        this.captureMethod = configuration.captureMethod ?: ::pixelCopyCapture

        return this
    }
}

@Suppress("UNCHECKED_CAST")
private fun getActivityClassFor(orientation: Orientation?): Class<FragmentActivity> =
    when (orientation == Orientation.LANDSCAPE) {
        true -> ActivityScenarioConfigurator.LandscapeSnapshotConfiguredActivity::class.java
        false -> ActivityScenarioConfigurator.PortraitSnapshotConfiguredActivity::class.java
    } as Class<FragmentActivity>
