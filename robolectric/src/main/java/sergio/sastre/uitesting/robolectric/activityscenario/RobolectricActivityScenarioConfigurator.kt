package sergio.sastre.uitesting.robolectric.activityscenario

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ActivityScenario
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import sergio.sastre.uitesting.robolectric.config.RobolectricQualifiersBuilder.setQualifiers
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.common.LocaleUtil
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontSizeScale
import sergio.sastre.uitesting.utils.common.FontWeight
import java.util.*
import androidx.core.graphics.drawable.toDrawable

object RobolectricActivityScenarioConfigurator {

    internal data class State(
        var deviceScreen: DeviceScreen? = null,
        var fontSize: FontSizeScale? = null,
        var locale: Locale? = null,
        var orientation: Orientation? = null,
        var uiMode: UiMode? = null,
        var displaySize: DisplaySize? = null,
        var fontWeight: FontWeight? = null,

        @get:ColorInt
        var backgroundColor: Int? = null,

        @get:StyleRes
        var themeId: Int? = null,
    )

    private fun Application.setThemeOnActivityCreated(@StyleRes themeId: Int) {
        registerActivityLifecycleCallbacks(object : OnActivityCreatedCallback {
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?
            ) {
                if (activity is SnapshotConfiguredActivity) {
                    activity.setTheme(themeId)
                }
                unregisterActivityLifecycleCallbacks(this)
            }
        })
    }

    private fun Configuration.applyState(state: State): Configuration {
        val newConfig = this@applyState
        state.apply {
            orientation?.let { newConfig.orientation = it.activityInfo }
            fontWeight?.let {
                when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    true -> newConfig.fontWeightAdjustment = it.value
                    false -> Log.d(
                        RobolectricActivityScenarioConfigurator.javaClass.simpleName,
                        "Skipping FontWeightAdjustment. It can only be used on API 31+, and the current API is ${Build.VERSION.SDK_INT}"
                    )
                }
            }
            fontSize?.let { newConfig.fontScale = it.scale }
            displaySize?.let {
                val newDensityDpi = it.value.toFloat() * newConfig.densityDpi
                newConfig.densityDpi = newDensityDpi.toInt()
            }
            locale?.let { newConfig.setLocale(it) }
            uiMode?.let { newConfig.uiMode = it.configurationInt }
        }
        return newConfig
    }

    private fun ActivityController<out FragmentActivity>.applyState(state: State) = apply {
        val newConfig = Configuration(this@applyState.get().resources.configuration)
        this@applyState.configurationChange(newConfig.applyState(state))
    }

    private fun ActivityScenario<out FragmentActivity>.applyBackgroundColor(
        @ColorInt backgroundColor: Int? = null,
    ) = apply {
        backgroundColor?.let { color ->
            onActivity { activity ->
                activity.window.setBackgroundDrawable(color.toDrawable())
            }
        }
    }

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
        private var state = State()
        private lateinit var activityController: ActivityController<out FragmentActivity>

        /**
         * Sets the Robolectric runtime qualifiers corresponding to the given [DeviceScreen].
         *
         * WARNING: Do not use together with @Config(qualifiers = "my_qualifiers") to avoid
         * any misbehaviour
         */
        fun setDeviceScreen(deviceScreen: DeviceScreen): ForView = apply {
            state = state.copy(deviceScreen = deviceScreen)
        }

        fun setFontSize(fontSize: FontSizeScale): ForView = apply {
            state = state.copy(fontSize = fontSize)
        }

        fun setLocale(locale: String): ForView = apply {
            state = state.copy(locale = LocaleUtil.localeFromString(locale))
        }

        fun setLocale(locale: Locale): ForView = apply {
            state = state.copy(locale = locale)
        }

        fun setUiMode(uiMode: UiMode): ForView = apply {
            state = state.copy(uiMode = uiMode)
        }

        fun setInitialOrientation(orientation: Orientation): ForView = apply {
            state = state.copy(orientation = orientation)
        }

        fun setDisplaySize(displaySize: DisplaySize): ForView = apply {
            state = state.copy(displaySize = displaySize)
        }

        fun setTheme(@StyleRes theme: Int): ForView = apply {
            state = state.copy(themeId = theme)
        }

        fun setFontWeight(fontWeight: FontWeight): ForView = apply {
            state = state.copy(fontWeight = fontWeight)
        }

        fun launchConfiguredActivity(
            @ColorInt backgroundColor: Int? = null,
        ): ActivityScenario<out FragmentActivity> {
            setQualifiers(
                deviceScreen = state.deviceScreen,
                configOrientation = state.orientation,
            )

            activityController =
                Robolectric
                    .buildActivity(SnapshotConfiguredActivity::class.java)
                    .setup()
                    .applyState(state)

            state.themeId?.let {
                RuntimeEnvironment.getApplication().setThemeOnActivityCreated(it)
            }

            return ActivityScenario
                .launch(SnapshotConfiguredActivity::class.java)
                .applyBackgroundColor(backgroundColor)
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
        private var state = State()
        private lateinit var activityController: ActivityController<out FragmentActivity>

        /**
         * Sets the Robolectric runtime qualifiers corresponding to the given [DeviceScreen].
         *
         * WARNING: Do not use together with @Config(qualifiers = "my_qualifiers") to avoid
         * any misbehaviour
         */
        fun setDeviceScreen(deviceScreen: DeviceScreen): ForComposable = apply {
            state = state.copy(deviceScreen = deviceScreen)
        }

        fun setFontSize(fontSize: FontSizeScale): ForComposable = apply {
            state = state.copy(fontSize = fontSize)
        }

        fun setLocale(locale: String): ForComposable = apply {
            state = state.copy(locale = LocaleUtil.localeFromString(locale))
        }

        fun setLocale(locale: Locale): ForComposable = apply {
            state = state.copy(locale = locale)
        }

        fun setUiMode(uiMode: UiMode): ForComposable = apply {
            state = state.copy(uiMode = uiMode)
        }

        fun setInitialOrientation(orientation: Orientation): ForComposable = apply {
            state = state.copy(orientation = orientation)
        }

        fun setDisplaySize(displaySize: DisplaySize): ForComposable = apply {
            state = state.copy(displaySize = displaySize)
        }

        fun setFontWeight(fontWeight: FontWeight): ForComposable = apply {
            state = state.copy(fontWeight = fontWeight)
        }

        fun launchConfiguredActivity(
            @ColorInt backgroundColor: Int? = null,
        ): ActivityScenario<out FragmentActivity> {
            setQualifiers(
                deviceScreen = state.deviceScreen,
                configOrientation = state.orientation,
            )

            activityController =
                Robolectric
                    .buildActivity(SnapshotConfiguredActivity::class.java)
                    .setup()
                    .applyState(state)

            return ActivityScenario
                .launch(SnapshotConfiguredActivity::class.java)
                .applyBackgroundColor(backgroundColor)
        }
    }

    /**
     *  Use this for standard Ui testing or to snapshot test full Activities,
     *  regardless of whether they contain Views or Composables
     *
     *  It launches an Activity of your choice under a given Locale, FontSize, uiMode, etc.
     */
    class ForActivity {
        private var state = State()
        private lateinit var activityController: ActivityController<out Activity>

        private fun ActivityController<out Activity>.applyState(state: State) = apply {
            val newConfig = Configuration(this@applyState.get().resources.configuration)
            this@applyState.configurationChange(newConfig.applyState(state))
        }

        /**
         * Sets the Robolectric runtime qualifiers corresponding to the given [DeviceScreen].
         *
         * WARNING: Do not use together with @Config(qualifiers = "my_qualifiers") to avoid
         * any misbehaviour
         */
        fun setDeviceScreen(deviceScreen: DeviceScreen): ForActivity = apply {
            state = state.copy(deviceScreen = deviceScreen)
        }

        fun setFontSize(fontSize: FontSizeScale): ForActivity = apply {
            state = state.copy(fontSize = fontSize)
        }

        fun setSystemLocale(locale: String): ForActivity = apply {
            state = state.copy(locale = LocaleUtil.localeFromString(locale))
        }

        fun setSystemLocale(locale: Locale): ForActivity = apply {
            state = state.copy(locale = locale)
        }

        fun setUiMode(uiMode: UiMode): ForActivity = apply {
            state = state.copy(uiMode = uiMode)
        }

        fun setOrientation(orientation: Orientation): ForActivity = apply {
            state = state.copy(orientation = orientation)
        }

        fun setDisplaySize(displaySize: DisplaySize): ForActivity = apply {
            state = state.copy(displaySize = displaySize)
        }

        fun setFontWeight(fontWeight: FontWeight): ForActivity = apply {
            state = state.copy(fontWeight = fontWeight)
        }

        fun <T : Activity> launch(clazz: Class<T>): ActivityScenario<T> {
            setQualifiers(
                deviceScreen = state.deviceScreen,
                configOrientation = state.orientation,
            )

            activityController =
                Robolectric
                    .buildActivity(clazz)
                    .setup()
                    .applyState(state)

            return ActivityScenario.launch(clazz)
        }

        fun <T : Activity> launch(
            clazz: Class<T>,
            intent: Intent,
            activityOptions: Bundle? = null,
        ): ActivityScenario<T> {
            setQualifiers(
                deviceScreen = state.deviceScreen,
                configOrientation = state.orientation,
            )

            activityController =
                Robolectric
                    .buildActivity(clazz)
                    .setup()
                    .applyState(state)

            return ActivityScenario.launch(intent, activityOptions)
        }
    }

    class SnapshotConfiguredActivity : FragmentActivity()
}
