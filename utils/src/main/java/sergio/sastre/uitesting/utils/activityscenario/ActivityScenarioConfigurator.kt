package sergio.sastre.uitesting.utils.activityscenario

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ActivityScenario
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.LocaleUtil
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.activityscenario.orientation.OrientationTestWatcher
import java.util.*

object ActivityScenarioConfigurator {
    private var fontSize: FontSize? = null
    private var locale: Locale? = null
    private var orientation: Orientation? = null
    private var uiMode: UiMode? = null

    @JvmInline
    value class StringLocale(val locale: String)

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
        fun setFontSize(fontSize: FontSize): ForView = apply {
            ActivityScenarioConfigurator.fontSize = fontSize
        }

        fun setLocale(locale: String): ForView = apply {
            ActivityScenarioConfigurator.locale = LocaleUtil.localeFromString(locale)
        }

        fun setLocale(stringLocale: StringLocale): ForView = apply {
            ActivityScenarioConfigurator.locale = LocaleUtil.localeFromString(stringLocale.locale)
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

        fun launchConfiguredActivity() =
            ActivityScenario.launch(activityForOrientation(orientation))
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
        fun setFontSize(fontSize: FontSize): ForComposable = apply {
            ActivityScenarioConfigurator.fontSize = fontSize
        }

        fun setLocale(locale: String): ForComposable = apply {
            ActivityScenarioConfigurator.locale = LocaleUtil.localeFromString(locale)
        }

        fun setLocale(stringLocale: StringLocale): ForComposable = apply {
            ActivityScenarioConfigurator.locale = LocaleUtil.localeFromString(stringLocale.locale)
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

        fun launchConfiguredActivity() =
            ActivityScenario.launch(activityForOrientation(orientation))
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

    private fun Context.wrap(): Context {
        val newConfig = Configuration(resources.configuration)

        fontSize?.run { newConfig.fontScale = value.toFloat() }
        locale?.run { newConfig.setLocale(this) }
        uiMode?.run { newConfig.uiMode = this.configurationInt }

        fontSize = null
        locale = null
        uiMode = null
        orientation = null

        return createConfigurationContext(newConfig)
    }

    class PortraitSnapshotConfiguredActivity : ComponentActivity() {
        override fun attachBaseContext(newBase: Context?) {
            super.attachBaseContext(newBase?.wrap())
        }
    }

    class LandscapeSnapshotConfiguredActivity :
        ComponentActivity() {
        override fun attachBaseContext(newBase: Context?) {
            super.attachBaseContext(newBase?.wrap())
        }
    }
}