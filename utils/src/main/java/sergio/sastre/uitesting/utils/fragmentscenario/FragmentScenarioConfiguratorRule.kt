package sergio.sastre.uitesting.utils.fragmentscenario

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Lifecycle
import org.junit.rules.ExternalResource

class FragmentScenarioConfiguratorRule<F : Fragment>(
    fragmentClass: Class<F>,
    fragmentArgs: Bundle?,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    factory: FragmentFactory? = null,
    config: FragmentConfigItem? = null,
) : ExternalResource() {

    val fragmentScenario =
            FragmentScenarioConfigurator.apply {
                config?.orientation?.also { orientation -> setInitialOrientation(orientation) }
                config?.locale?.also { locale -> setLocale(locale) }
                config?.uiMode?.also { uiMode -> setUiMode(uiMode) }
                config?.fontWeight?.also { fontWeight -> setFontWeight(fontWeight) }
                config?.fontSize?.also { fontSize -> setFontSize(fontSize) }
                config?.displaySize?.also { displaySize -> setDisplaySize(displaySize) }
                config?.theme?.also { theme -> setTheme(theme) }
            }.launchInContainer(fragmentClass, fragmentArgs, initialState, factory)

    val fragment: Fragment by lazy {
        fragmentScenario.waitForFragment()
    }

    override fun after() {
        fragmentScenario.close()
    }
}