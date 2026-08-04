package sergio.sastre.uitesting.utils.activityscenario

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.annotation.Discouraged
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ActivityScenario
import sergio.sastre.uitesting.utils.common.LocaleUtil
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.activityscenario.orientation.OrientationTestWatcher
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontSizeScale
import java.util.*
import androidx.core.graphics.drawable.toDrawable
import sergio.sastre.uitesting.utils.common.FontWeight

object ActivityScenarioConfigurator {
    private var fontSize: FontSizeScale? = null
    private var locale: Locale? = null
    private var orientation: Orientation? = null
    private var uiMode: UiMode? = null
    private var displaySize: DisplaySize? = null
    private var fontWeight: FontWeight? = null

    @ColorInt
    private var backgroundColor: Int? = null

    private var showStatusBar: Boolean = false

    @StyleRes
    private var themeId: Int? = null

    /**
     *  Use this for snapshot testing any view that is not an Activity or Composable, e.g. Dialog,
     *  ViewHolder, custom View, etc.
     *
     *  It launches a pre-configured default Activity under a given Locale, FontSize, UiMode, etc.
     *
     *  IMPORTANT: All UI elements inflated with this Activity context will inherit such properties.
     *  That's why you must inflate every custom View, Dialog, ViewHolders, etc. you want to
     *  snapshot test with the context of this activity.
     *  You can use the method Activity.inflate in the Extension.kt provided in this library for
     *  that purpose.
     */
    class ForView {
        fun setFontSize(fontSize: FontSizeScale): ForView = apply {
            ActivityScenarioConfigurator.fontSize = fontSize
        }

        fun setLocale(locale: String): ForView = apply {
            ActivityScenarioConfigurator.locale = LocaleUtil.localeFromString(locale)
        }

        fun setLocale(locale: Locale): ForView = apply {
            ActivityScenarioConfigurator.locale = locale
        }

        fun setUiMode(uiMode: UiMode): ForView = apply {
            ActivityScenarioConfigurator.uiMode = uiMode
        }

        fun setInitialOrientation(orientation: Orientation): ForView = apply {
            ActivityScenarioConfigurator.orientation = orientation
        }

        fun setDisplaySize(displaySize: DisplaySize): ForView = apply {
            ActivityScenarioConfigurator.displaySize = displaySize
        }

        fun setTheme(@StyleRes theme: Int): ForView = apply {
            ActivityScenarioConfigurator.themeId = theme
        }

        fun setFontWeight(fontWeight: FontWeight): ForView = apply {
            ActivityScenarioConfigurator.fontWeight = fontWeight
        }

        fun setActivityBackgroundColor(@ColorInt backgroundColor: Int): ForView = apply {
            ActivityScenarioConfigurator.backgroundColor = backgroundColor
        }

        fun setShowStatusBar(show: Boolean): ForView = apply {
            ActivityScenarioConfigurator.showStatusBar = show
        }

        fun launchConfiguredActivity(
            @ColorInt backgroundColor: Int? = null,
            showStatusBar: Boolean? = null,
        ): ActivityScenario<out FragmentActivity> {
            backgroundColor?.let { ActivityScenarioConfigurator.backgroundColor = it }
            showStatusBar?.let { ActivityScenarioConfigurator.showStatusBar = it }
            return ActivityScenario.launch(activityForOrientation(orientation))
        }
    }

    /**
     *  Use this for standard Ui testing or to snapshot test Composables.
     *
     *  It launches a pre-configured default Activity under a given Locale, FontSize, uiMode, etc.
     *
     *  In order to make Composables work with ActivityScenarios, one needs to use
     *  @get:Rule
     *  val composeTestRule = createEmptyComposeRule()
     *
     *  Moreover, if using uiMode, use the AppTheme of your app in [ComponentActivity].setContent,
     *  i.e. the one encapsulating isSystemInDarkTheme(). Otherwise the Theme you are using in
     *  [ComponentActivity].setContent will be applied instead
     *
     */
    class ForComposable {
        fun setFontSize(fontSize: FontSizeScale): ForComposable = apply {
            ActivityScenarioConfigurator.fontSize = fontSize
        }

        fun setLocale(locale: String): ForComposable = apply {
            ActivityScenarioConfigurator.locale = LocaleUtil.localeFromString(locale)
        }

        fun setLocale(locale: Locale): ForComposable = apply {
            ActivityScenarioConfigurator.locale = locale
        }

        fun setUiMode(uiMode: UiMode): ForComposable = apply {
            ActivityScenarioConfigurator.uiMode = uiMode
        }

        fun setInitialOrientation(orientation: Orientation): ForComposable = apply {
            ActivityScenarioConfigurator.orientation = orientation
        }

        fun setDisplaySize(displaySize: DisplaySize): ForComposable = apply {
            ActivityScenarioConfigurator.displaySize = displaySize
        }

        fun setFontWeight(fontWeight: FontWeight): ForComposable = apply {
            ActivityScenarioConfigurator.fontWeight = fontWeight
        }

        fun setActivityBackgroundColor(@ColorInt backgroundColor: Int): ForComposable = apply {
            ActivityScenarioConfigurator.backgroundColor = backgroundColor
        }

        fun setShowStatusBar(show: Boolean): ForComposable = apply {
            ActivityScenarioConfigurator.showStatusBar = show
        }

        fun launchConfiguredActivity(
            @ColorInt backgroundColor: Int? = null,
            showStatusBar: Boolean? = null,
        ): ActivityScenario<out FragmentActivity> {
            backgroundColor?.let { ActivityScenarioConfigurator.backgroundColor = it }
            showStatusBar?.let { ActivityScenarioConfigurator.showStatusBar = it }
            return ActivityScenario.launch(activityForOrientation(orientation))
        }
    }

    /**
     *  Use this for standard Ui testing or to snapshot test full Activities,
     *  regardless of whether they contain Views or Composables
     *
     *  It launches an Activity of your choice under a given orientation.
     *
     *  For further configuration: you want to set the Locale or FontSize, add the corresponding
     *  TestRules:
     *  - Locale -> Use LocaleTestRule together with this
     *  - FontSize -> Use FontSizeTestRule together with this
     *
     */
    class ForActivity {

        private var orientationTestWatcher: OrientationTestWatcher? = null

        @Discouraged(
            message = "Consider using UiModeTestRule(uiMode.appCompatDelegateInt). " +
                    "Using this method might throw an IllegalStateException: Must be called from " +
                    "main thread."
        )
        fun setUiMode(uiMode: UiMode): ForActivity = apply {
            AppCompatDelegate.setDefaultNightMode(uiMode.appCompatDelegateInt)
        }

        fun setOrientation(orientation: Orientation): ForActivity = apply {
            orientationTestWatcher = OrientationTestWatcher(orientation)
        }

        fun <T : Activity> launch(clazz: Class<T>): ActivityScenario<T> {
            val activityScenario = ActivityScenario.launch(clazz)
            orientationTestWatcher?.activityScenario = activityScenario
            return activityScenario
        }

        fun <T : Activity> launch(
            intent: Intent,
            activityOptions: Bundle? = null,
        ): ActivityScenario<T> {
            val activityScenario =
                ActivityScenario.launch<T>(intent, activityOptions)
            orientationTestWatcher?.activityScenario = activityScenario
            return activityScenario
        }
    }

    private fun activityForOrientation(orientation: Orientation?) =
        if (orientation == Orientation.LANDSCAPE) {
            LandscapeSnapshotConfiguredActivity::class.java
        } else {
            PortraitSnapshotConfiguredActivity::class.java
        }

    private fun Activity.applyThemeId() {
        themeId?.run {
            setTheme(this)
        }
    }

    private fun Activity.applyWindowStyle() {
        backgroundColor?.run {
            window.setBackgroundDrawable(this.toDrawable())
        }
    }

    private fun Activity.applyStatusBarVisibility() {
        val window = this.window
        // Accessing window.decorView ensures mDecor is initialized in PhoneWindow,
        // preventing NPE when accessing window.insetsController on some API 30 devices
        val decorView = window.decorView
        val controller = WindowCompat.getInsetsController(window, decorView)
        if (!showStatusBar) {
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.statusBars())
        }
    }

    private fun resetConfig() {
        fontSize = null
        locale = null
        uiMode = null
        orientation = null
        displaySize = null
        fontWeight = null
        backgroundColor = null
        showStatusBar = false
        themeId = null
    }

    private fun Context.wrap(): Context {
        val newConfig = Configuration(resources.configuration)

        fontSize?.run { newConfig.fontScale = this.scale }
        locale?.run { newConfig.setLocale(this) }
        uiMode?.run { newConfig.uiMode = this.configurationInt }
        displaySize?.run {
            val newDensityDpi = this.value.toFloat() * newConfig.densityDpi
            newConfig.densityDpi = newDensityDpi.toInt()
        }
        fontWeight?.let {
            when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                true -> newConfig.fontWeightAdjustment = it.value
                false -> Log.d(
                    this.javaClass.simpleName,
                    "Skipping FontWeightAdjustment. It can only be used on API 31+, and the current API is ${Build.VERSION.SDK_INT}"
                )
            }
        }

        return createConfigurationContext(newConfig)
    }

    abstract class SnapshotConfiguredActivity : FragmentActivity() {
        override fun attachBaseContext(newBase: Context?) {
            super.attachBaseContext(newBase?.wrap())
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            applyThemeId()
            super.onCreate(savedInstanceState)
            applyWindowStyle()
        }

        override fun onPostCreate(savedInstanceState: Bundle?) {
            super.onPostCreate(savedInstanceState)
            applyStatusBarVisibility()
        }

        override fun onWindowFocusChanged(hasFocus: Boolean) {
            super.onWindowFocusChanged(hasFocus)
            if (hasFocus) {
                applyStatusBarVisibility()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            if (!isChangingConfigurations) {
                resetConfig()
            }
        }
    }

    class PortraitSnapshotConfiguredActivity : SnapshotConfiguredActivity()

    class LandscapeSnapshotConfiguredActivity : SnapshotConfiguredActivity()
}
