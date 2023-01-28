package sergio.sastre.uitesting.utils.testrules.locale

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate.getApplicationLocales
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.LocaleUtil
import java.util.*
import kotlin.Throws

/**
 * A TestRule to change the Locale of the application ONLY.
 *
 * The Locale of the System does not change. Use SystemLocaleTestRule instead for that.
 *
 * Warning: it currently does NOT work together with ActivityScenarioForActivityRule.
 * However, you can use it together with ActivityScenarioConfigurator.ForActivity() for
 * testing activities
 */
class LocaleTestRule : TestRule {
    private val testLocale: Locale?

    constructor(testLocale: String) {
        this.testLocale = LocaleUtil.localeFromString(testLocale)
    }

    constructor(testLocale: Locale) {
        this.testLocale = testLocale
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                setAppLocale(testLocale)
                base.evaluate()
            }
        }
    }

    private fun setAppLocale(locale: Locale?) {
        val initialLocales = getApplicationLocales()
        ApplicationProvider.getApplicationContext<Application>().apply {
            registerActivityLifecycleCallbacks(
                object : Application.ActivityLifecycleCallbacks {
                    override fun onActivityCreated(
                        activity: Activity,
                        savedInstanceState: Bundle?
                    ) {
                        setApplicationLocales(LocaleListCompat.create(locale))
                    }

                    override fun onActivityStarted(activity: Activity) {}

                    override fun onActivityResumed(activity: Activity) {}

                    override fun onActivityPaused(activity: Activity) {}

                    override fun onActivityStopped(activity: Activity) {}

                    override fun onActivitySaveInstanceState(
                        activity: Activity,
                        outState: Bundle
                    ) {
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                        setApplicationLocales(initialLocales)
                        unregisterActivityLifecycleCallbacks(this)
                    }
                })
        }
    }
}