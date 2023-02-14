package sergio.sastre.uitesting.inapplocale

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate.getApplicationLocales
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.LocaleUtil
import java.util.*

/**
 * A TestRule to change the in-app locales of the application ONLY.
 * The Locale of the System does not change. Use SystemLocaleTestRule instead for that.
 * Beware that in-app locales prevail over the system locale while displaying texts.
 */
class InAppLocaleTestRule : TestRule {

    companion object {
        private val TAG = InAppLocaleTestRule::class.java.simpleName
    }

    private val testLocale: Locale?

    private lateinit var initialLocales: LocaleListCompat

    constructor(testLocale: String) {
        this.testLocale = LocaleUtil.localeFromString(testLocale)
    }

    constructor(testLocale: Locale) {
        this.testLocale = testLocale
    }

    private val appLocalesLanguageTags
        get() = getApplicationLocales().toLanguageTags().ifBlank { "empty" }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    // From API 33 we need to ensure that AppCompatDelegate.setApplicationLocales
                    // is called after onActivityCreated
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        setApplicationLocaleAfterActivityOnCreate(testLocale)
                    } else {
                        setApplicationLocaleInLooper(Looper.getMainLooper(), testLocale)
                    }
                    base.evaluate()
                } finally {
                    // must run on Main thread to avoid IllegalStateExceptions
                    Handler(Looper.getMainLooper()).post {
                        setApplicationLocales(initialLocales)
                        Log.d(TAG, "in-app locales reset to $appLocalesLanguageTags")
                    }
                }
            }
        }
    }

    private fun setApplicationLocaleInLooper(looper: Looper, locale: Locale?) {
        Handler(looper).post {
            initialLocales = getApplicationLocales()
            Log.d(TAG, "initial in-app locales is $appLocalesLanguageTags")
            setApplicationLocales(LocaleListCompat.create(locale))
            Log.d(TAG, "in-app locales set to $appLocalesLanguageTags")
        }
    }


    private fun setApplicationLocaleAfterActivityOnCreate(locale: Locale?) {
        ApplicationProvider.getApplicationContext<Application>().apply {
            registerActivityLifecycleCallbacks(
                object : OnActivityCreatedCallback {
                    override fun onActivityCreated(
                        activity: Activity,
                        savedInstanceState: Bundle?
                    ) {
                        unregisterActivityLifecycleCallbacks(this)
                        setApplicationLocaleInLooper(activity.mainLooper, locale)
                    }
                }
            )
        }
    }
}