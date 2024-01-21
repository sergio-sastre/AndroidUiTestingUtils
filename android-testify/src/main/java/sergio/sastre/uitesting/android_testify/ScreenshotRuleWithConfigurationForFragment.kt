package sergio.sastre.uitesting.android_testify

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.testify.ScreenshotRule
import dev.testify.core.TestifyConfiguration
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioConfigurator
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.fragmentscenario.FragmentConfigItem

class ScreenshotRuleWithConfigurationForFragment<F : Fragment>(
    exactness: Float? = null,
    enableReporter: Boolean = false,
    initialTouchMode: Boolean = false,
    config: FragmentConfigItem? = null,
    @ColorInt activityBackgroundColor: Int? = null,
    private val fragmentClass: Class<F>,
    private val fragmentArgs: Bundle? = null,
    private val factory: FragmentFactory? = null,
) : ScreenshotRule<FragmentActivity>(
    activityClass = getActivityClassFor(config?.orientation),
    enableReporter = enableReporter,
    initialTouchMode = initialTouchMode,
    configuration = TestifyConfiguration(exactness = exactness)
) {

    private companion object {
        const val FRAGMENT_TAG = "Android-Testify-Fragment"
    }

    init {
        ActivityScenarioConfigurator.ForView()
            .apply {
                config?.uiMode?.run { setUiMode(this) }
                config?.locale?.run { setLocale(this) }
                config?.fontSize?.run { setFontSize(this) }
                config?.displaySize?.run { setDisplaySize(this) }
                config?.theme?.run { setTheme(this) }
                activityBackgroundColor?.run { setActivityBackgroundColor(this) }
            }

        setFragmentForScreenshot()
    }

    private fun setFragmentForScreenshot(){
        setViewModifications {
            if (factory != null) {
                FragmentFactoryHolderViewModel.getInstance(activity).fragmentFactory = factory
                activity.supportFragmentManager.fragmentFactory = factory
            }

            val fragment = activity.supportFragmentManager.fragmentFactory
                .instantiate(requireNotNull(fragmentClass.classLoader), fragmentClass.name)

            if (fragmentArgs != null) {
                fragment.arguments = fragmentArgs
            }

            activity.supportFragmentManager.beginTransaction()
                .add(
                    android.R.id.content,
                    fragment,
                    FRAGMENT_TAG
                )
                .commitNow()

        // by default, the fragment is what we screenshot
        }.setScreenshotViewProvider {
            activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)!!.requireView()
        }
    }

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
}

@Suppress("UNCHECKED_CAST")
private fun getActivityClassFor(orientation: Orientation?): Class<FragmentActivity> =
    when (orientation == Orientation.LANDSCAPE) {
        true -> ActivityScenarioConfigurator.LandscapeSnapshotConfiguredActivity::class.java
        false -> ActivityScenarioConfigurator.PortraitSnapshotConfiguredActivity::class.java
    } as Class<FragmentActivity>
