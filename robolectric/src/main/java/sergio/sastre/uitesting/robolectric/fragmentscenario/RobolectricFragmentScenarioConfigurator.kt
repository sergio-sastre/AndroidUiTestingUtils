/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sergio.sastre.uitesting.robolectric.fragmentscenario

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RestrictTo
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioConfigurator
import sergio.sastre.uitesting.robolectric.config.RobolectricQualifiersBuilder.setQualifiers
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.LocaleUtil
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import java.io.Closeable
import java.util.*

/**
 * Based on FragmentScenario from Google, optimized for Robolectric tests.
 *
 * WARNING: If any DeviceScreen is set, do not use together with @Config(qualifiers = "my_qualifiers") to avoid
 * any misbehaviour
*/
class RobolectricFragmentScenarioConfigurator<F : Fragment> constructor(
    @Suppress("MemberVisibilityCanBePrivate") /* synthetic access */
    internal val fragmentClass: Class<F>,
    private val activityScenario: ActivityScenario<out FragmentActivity>
) : Closeable {

    /**
     * A view-model to hold a fragment factory.
     *
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    internal class FragmentFactoryHolderViewModel : ViewModel() {
        var fragmentFactory: FragmentFactory? = null

        override fun onCleared() {
            super.onCleared()
            fragmentFactory = null
        }

        companion object {
            fun getInstance(activity: FragmentActivity): FragmentFactoryHolderViewModel {
                val viewModel: FragmentFactoryHolderViewModel by activity.viewModels {
                    object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val viewModel =
                                FragmentFactoryHolderViewModel()
                            return viewModel as T
                        }
                    }
                }
                return viewModel
            }
        }
    }

    /**
     * Moves Fragment state to a new state.
     *
     *  If a new state and current state are the same, this method does nothing. It accepts
     * [CREATED][Lifecycle.State.CREATED], [STARTED][Lifecycle.State.STARTED],
     * [RESUMED][Lifecycle.State.RESUMED], and [DESTROYED][Lifecycle.State.DESTROYED].
     * [DESTROYED][Lifecycle.State.DESTROYED] is a terminal state.
     * You cannot move to any other state after the Fragment reaches that state.
     *
     * This method cannot be called from the main thread.
     */
    fun moveToState(newState: Lifecycle.State): RobolectricFragmentScenarioConfigurator<F> {
        if (newState == Lifecycle.State.DESTROYED) {
            activityScenario.onActivity { activity ->
                val fragment = activity.supportFragmentManager
                    .findFragmentByTag(FRAGMENT_TAG)
                // Null means the fragment has been destroyed already.
                if (fragment != null) {
                    activity.supportFragmentManager.commitNow {
                        remove(fragment)
                    }
                }
            }
        } else {
            activityScenario.onActivity { activity ->
                val fragment = requireNotNull(
                    activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
                ) {
                    "The fragment has been removed from the FragmentManager already."
                }
                activity.supportFragmentManager.commitNow {
                    setMaxLifecycle(fragment, newState)
                }
            }
        }
        return this
    }

    /**
     * Recreates the host Activity.
     *
     * After this method call, it is ensured that the Fragment state goes back to the same state
     * as its previous state.
     *
     * This method cannot be called from the main thread.
     */
    fun recreate(): RobolectricFragmentScenarioConfigurator<F> {
        activityScenario.recreate()
        return this
    }

    /**
     * FragmentAction interface should be implemented by any class whose instances are intended to
     * be executed by the main thread. A Fragment that is instrumented by the FragmentScenario is
     * passed to [FragmentAction.perform] method.
     *
     * You should never keep the Fragment reference as it will lead to unpredictable behaviour.
     * It should only be accessed in [FragmentAction.perform] scope.
     */
    fun interface FragmentAction<F : Fragment> {
        /**
         * This method is invoked on the main thread with the reference to the Fragment.
         *
         * @param fragment a Fragment instrumented by the FragmentScenario.
         */
        public fun perform(fragment: F)
    }

    /**
     * Runs a given [action] on the current Activity's main thread.
     *
     * Note that you should never keep Fragment reference passed into your [action]
     * because it can be recreated at anytime during state transitions.
     *
     * Throwing an exception from [action] makes the host Activity crash. You can
     * inspect the exception in logcat outputs.
     *
     * This method cannot be called from the main thread.
     */
    fun onFragment(action: FragmentAction<F>): RobolectricFragmentScenarioConfigurator<F> {
        activityScenario.onActivity { activity ->
            val fragment = requireNotNull(
                activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            ) {
                "The fragment has been removed from the FragmentManager already."
            }
            check(fragmentClass.isInstance(fragment))
            action.perform(requireNotNull(fragmentClass.cast(fragment)))
        }
        return this
    }

    /**
     * Finishes the managed fragments and cleans up device's state. This method blocks execution
     * until the host activity becomes [Lifecycle.State.DESTROYED].
     */
    override fun close() {
        activityScenario.close()
    }

    class ForFragment() {
        data class State(
            var deviceScreen: DeviceScreen? = null,
            var fontSize: FontSize? = null,
            var locale: Locale? = null,
            var orientation: Orientation? = null,
            var uiMode: UiMode? = null,
            var displaySize: DisplaySize? = null,

            @StyleRes
            var themeId: Int? = null,
        )

        private var state = State()

        /**
         * Sets the Robolectric runtime qualifiers corresponding to the given [DeviceScreen]
         *
         * WARNING: Do not use together with @Config(qualifiers = "my_qualifiers") to avoid
         * any misbehaviour
         */
        fun setDeviceScreen(deviceScreen: DeviceScreen) = apply {
            state = state.copy(deviceScreen = deviceScreen)
        }

        fun setInitialOrientation(orientation: Orientation) = apply {
            state = state.copy(orientation = orientation)
        }

        fun setLocale(locale: Locale) = apply {
            state = state.copy(locale = locale)
        }

        fun setLocale(locale: String) = apply {
            state = state.copy(locale = LocaleUtil.localeFromString(locale))
        }

        fun setUiMode(uiMode: UiMode) = apply {
            state = state.copy(uiMode = uiMode)
        }

        fun setFontSize(fontSize: FontSize) = apply {
            state = state.copy(fontSize = fontSize)
        }

        fun setDisplaySize(displaySize: DisplaySize) = apply {
            state = state.copy(displaySize = displaySize)
        }

        fun setTheme(theme: Int) = apply {
            state = state.copy(themeId = theme)
        }

        /**
         * Launches a Fragment in the Activity's root view container `android.R.id.content`, with
         * given arguments hosted by an empty [FragmentActivity],
         * using the given [FragmentFactory] and waits for it to reach [initialState].
         *
         * This method cannot be called from the main thread.
         *
         * @param fragmentClass a fragment class to instantiate
         * @param fragmentArgs a bundle to passed into fragment
         * @param themeResId a style resource id to be set to the host activity's theme
         * @param initialState The initial [Lifecycle.State]. This must be one of
         * [CREATED][Lifecycle.State.CREATED], [STARTED][Lifecycle.State.STARTED], and
         * [RESUMED][Lifecycle.State.RESUMED].
         * @param factory a fragment factory to use or null to use default factory
         */
        @JvmOverloads
        fun <F : Fragment> launchInContainer(
            fragmentClass: Class<F>,
            fragmentArgs: Bundle? = null,
            initialState: Lifecycle.State = Lifecycle.State.RESUMED,
            factory: FragmentFactory? = null
        ): RobolectricFragmentScenarioConfigurator<F> = internalLaunch(
            fragmentClass,
            fragmentArgs,
            initialState,
            factory,
        )

        @SuppressLint("RestrictedApi")
        internal fun <F : Fragment> internalLaunch(
            fragmentClass: Class<F>,
            fragmentArgs: Bundle?,
            initialState: Lifecycle.State,
            factory: FragmentFactory?,
        ): RobolectricFragmentScenarioConfigurator<F> {
            require(initialState != Lifecycle.State.DESTROYED) {
                "Cannot set initial Lifecycle state to $initialState for FragmentScenario"
            }

            val activityScenario = RobolectricActivityScenarioConfigurator.ForView()
                .apply {
                    setQualifiers(
                        deviceScreen = state.deviceScreen,
                        configOrientation = state.orientation,
                    )
                }
                .apply {
                    state.orientation?.also { orientation -> setInitialOrientation(orientation) }
                    state.locale?.also { locale -> setLocale(locale) }
                    state.uiMode?.also { uiMode -> setUiMode(uiMode) }
                    state.fontSize?.also { fontSize -> setFontSize(fontSize) }
                    state.displaySize?.also { displaySize -> setDisplaySize(displaySize) }
                    state.themeId?.also { theme -> setTheme(theme) }
                }.launchConfiguredActivity()

            val fragmentScenarioConfigurator = RobolectricFragmentScenarioConfigurator(
                fragmentClass,
                activityScenario,
            )

            fragmentScenarioConfigurator.activityScenario.onActivity { activity ->
                if (factory != null) {
                    FragmentFactoryHolderViewModel.getInstance(activity).fragmentFactory =
                        factory
                    activity.supportFragmentManager.fragmentFactory = factory
                }

                val fragment = activity.supportFragmentManager.fragmentFactory
                    .instantiate(requireNotNull(fragmentClass.classLoader), fragmentClass.name)
                fragment.arguments = fragmentArgs
                activity.supportFragmentManager.commitNow {
                    add(android.R.id.content, fragment, FRAGMENT_TAG)
                    setMaxLifecycle(fragment, initialState)
                }
            }
            return fragmentScenarioConfigurator
        }
    }

    companion object {
        private const val FRAGMENT_TAG = "RobolectricConfigurableFragmentScenario_Fragment_Tag"
    }
}
