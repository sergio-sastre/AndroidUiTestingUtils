package sergio.sastre.uitesting.robolectric.fragmentscenario

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Lifecycle
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.fragmentscenario.FragmentConfigItem

/**
 * Returns a [Fragment] from a [RobolectricFragmentScenarioConfigurator].
 */
fun <A : Fragment> RobolectricFragmentScenarioConfigurator<A>.waitForFragment(): A {
    var fragment: A? = null
    onFragment {
        fragment = it
    }
    return if (fragment != null) {
        fragment!!
    } else {
        throw IllegalStateException("The fragment scenario could not be initialized.")
    }
}

/**
 * Based on FragmentScenario from Google, optimized for Robolectric tests.
 *
 * WARNING: If any DeviceScreen is set, do not use together
 * with @Config(qualifiers = "my_qualifiers") to avoid any misbehaviour
 */
inline fun <reified T : Fragment> RobolectricFragmentScenarioConfigurator<T>.launchInContainer(
    fragmentArgs: Bundle? = null,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    factory: FragmentFactory? = null
): RobolectricFragmentScenarioConfigurator<T> =
    RobolectricFragmentScenarioConfigurator.ForFragment().launchInContainer(
        T::class.java,
        fragmentArgs,
        initialState,
        factory,
    )

/**
 * Based on FragmentScenario from Google, optimized for Robolectric tests.
 *
 * WARNING: If any DeviceScreen is set, do not use together
 * with @Config(qualifiers = "my_qualifiers") to avoid any misbehaviour
 */
inline fun <reified T : Fragment> robolectricFragmentScenarioConfiguratorRule(
    fragmentArgs: Bundle? = null,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    factory: FragmentFactory? = null,
    deviceScreen: DeviceScreen? = null,
    config: FragmentConfigItem? = null,
): RobolectricFragmentScenarioConfiguratorRule<T> = RobolectricFragmentScenarioConfiguratorRule(
    T::class.java,
    fragmentArgs,
    initialState,
    factory,
    deviceScreen,
    config,
)
