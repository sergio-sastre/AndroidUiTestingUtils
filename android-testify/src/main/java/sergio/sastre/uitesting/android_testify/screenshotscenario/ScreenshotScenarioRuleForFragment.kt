package sergio.sastre.uitesting.android_testify.screenshotscenario

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import dev.testify.core.TestifyConfiguration
import dev.testify.scenario.ScreenshotScenarioRule

class ScreenshotScenarioRuleForFragment<F : Fragment>(
    configuration: TestifyConfiguration,
    enableReporter: Boolean = false,
    private val fragmentClass: Class<F>,
    private val fragmentArgs: Bundle? = null,
    private val factory: FragmentFactory? = null,
) : ScreenshotScenarioRule(
    enableReporter = enableReporter,
    configuration = configuration
) {

    private companion object {
        const val FRAGMENT_TAG = "Android-Testify-Fragment"
    }

    override fun <TActivity : Activity> withScenario(scenario: ActivityScenario<TActivity>): ScreenshotScenarioRule {
        return super.withScenario(scenario).also { setFragmentForScreenshot() }
    }

    private fun setFragmentForScreenshot(){
        setViewModifications {
            if (factory != null) {
                FragmentFactoryHolderViewModel.getInstance(activity as FragmentActivity).fragmentFactory = factory
                (activity as FragmentActivity).supportFragmentManager.fragmentFactory = factory
            }

            val fragment = (activity as FragmentActivity).supportFragmentManager.fragmentFactory
                .instantiate(requireNotNull(fragmentClass.classLoader), fragmentClass.name)

            if (fragmentArgs != null) {
                fragment.arguments = fragmentArgs
            }

            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .add(
                    android.R.id.content,
                    fragment,
                    FRAGMENT_TAG
                )
                .commitNow()

            // by default, the fragment is what we screenshot
        }.setScreenshotViewProvider {
            (activity as FragmentActivity).supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)!!.requireView()
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