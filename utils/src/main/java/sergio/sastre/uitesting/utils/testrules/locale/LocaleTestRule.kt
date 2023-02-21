package sergio.sastre.uitesting.utils.testrules.locale

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.Discouraged
import androidx.appcompat.app.AppCompatDelegate
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
 *
 * WARNING: This TestRule works on API 32 or lower if autoStoreLocale = false.
 * Otherwise, tt is not ensured to work as expected
 * That's why it's strongly recommended to disable it in your debug-manifest.
 * For instance, by including the following in your debug-manifest:
 *
 * <service android:name="androidx.appcompat.app.AppLocalesMetadataHolderService"
 *    android:enabled="false"
 *    android:exported="false"
 *    tools:node="replace"
 * >
 *    <meta-data
 *       android:name="autoStoreLocales"
 *       android:value="false"
 *    />
 *</service>
 **/
@Discouraged(
    message = "Consider using InAppLocaleTestRule instead, this Class will be removed in future versions and behaves exactly the same"
)
class LocaleTestRule constructor(val locale: Locale) : TestRule {

    companion object {
        @SuppressLint("DiscouragedApi")
        private val TAG = LocaleTestRule::class.java.simpleName
    }

    private lateinit var initialLocales: LocaleListCompat

    @SuppressLint("DiscouragedApi")
    constructor(testLocale: String) : this(LocaleUtil.localeFromString(testLocale))

    private val appLocalesLanguageTags
        get() = AppCompatDelegate.getApplicationLocales().toLanguageTags().ifBlank { "empty" }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @SuppressLint("DiscouragedApi")
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    // From API 33 we need to ensure that AppCompatDelegate.setApplicationLocales
                    // is called after onActivityCreated
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        setApplicationLocaleAfterActivityOnCreate(locale)
                    } else {
                        setApplicationLocaleInLooper(Looper.getMainLooper(), locale)
                    }
                    base.evaluate()
                } finally {
                    // must run on Main thread to avoid IllegalStateExceptions
                    Handler(Looper.getMainLooper()).post {
                        AppCompatDelegate.setApplicationLocales(initialLocales)
                        Log.d(TAG, "in-app locales reset to $appLocalesLanguageTags")
                    }
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun setApplicationLocaleInLooper(looper: Looper, locale: Locale?) {
        Handler(looper).post {
            initialLocales = AppCompatDelegate.getApplicationLocales()
            Log.d(TAG, "initial in-app locales is $appLocalesLanguageTags")
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
            Log.d(TAG, "in-app locales set to $appLocalesLanguageTags")
        }
    }


    private fun setApplicationLocaleAfterActivityOnCreate(locale: Locale?) {
        ApplicationProvider.getApplicationContext<Application>().apply {
            registerActivityLifecycleCallbacks(
                object : OnActivityCreatedCallback {
                    @SuppressLint("DiscouragedApi")
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
