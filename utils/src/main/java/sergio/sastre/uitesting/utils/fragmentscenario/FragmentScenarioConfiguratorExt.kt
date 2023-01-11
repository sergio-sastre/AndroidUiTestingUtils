package sergio.sastre.uitesting.utils.fragmentscenario

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Lifecycle

/**
 * Returns a [Fragment] from a [FragmentScenarioConfigurator].
*/
fun <A : Fragment> FragmentScenarioConfigurator<A>.waitForFragment(): A {
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

inline fun <reified T : Fragment> FragmentScenarioConfigurator.Companion.launchInContainer(
    fragmentArgs: Bundle? = null,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    factory: FragmentFactory? = null
): FragmentScenarioConfigurator<T> = launchInContainer(
    T::class.java,
    fragmentArgs,
    initialState,
    factory,
)

inline fun <reified T : Fragment> fragmentScenarioConfiguratorRule(
    fragmentArgs: Bundle? = null,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    factory: FragmentFactory? = null,
    config: FragmentConfigItem? = null,
): FragmentScenarioConfiguratorRule<T> = FragmentScenarioConfiguratorRule(
    T::class.java,
    fragmentArgs,
    initialState,
    factory,
    config,
)
