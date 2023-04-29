package sergio.sastre.uitesting.robolectric.fragmentscenario

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Lifecycle
import org.junit.rules.ExternalResource
import sergio.sastre.uitesting.robolectric.config.RobolectricQualifiersBuilder
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.fragmentscenario.FragmentConfigItem

class RobolectricFragmentScenarioConfiguratorRule<F : Fragment>(
    fragmentClass: Class<F>,
    fragmentArgs: Bundle?,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    factory: FragmentFactory? = null,
    val deviceScreen: DeviceScreen? = null,
    val config: FragmentConfigItem? = null,
) : ExternalResource() {

    val fragmentScenario =
        RobolectricFragmentScenarioConfigurator.ForFragment()
            .apply {
                RobolectricQualifiersBuilder.setQualifiers(
                    deviceScreen = deviceScreen,
                    configOrientation = config?.orientation,
                )
            }
            .apply {
                config?.orientation?.also { orientation -> setInitialOrientation(orientation) }
                config?.locale?.also { locale -> setLocale(locale) }
                config?.uiMode?.also { uiMode -> setUiMode(uiMode) }
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
