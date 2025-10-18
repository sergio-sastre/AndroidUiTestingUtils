package sergio.sastre.uitesting.utils.testrules.locale

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate.getApplicationLocales
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.core.os.LocaleListCompat
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.LocaleUtil
import java.util.Locale

internal class ApiDependentInAppLocaleTestRule constructor(
    private val locale: Locale
) : TestRule {

    companion object Companion {
        private val TAG = InAppLocaleTestRule::class.java.simpleName
    }

    private lateinit var initialLocales: LocaleListCompat

    constructor(
        testLocale: String
    ) : this(LocaleUtil.localeFromString(testLocale))

    private val appLocalesLanguageTags
        get() = getApplicationLocales().toLanguageTags().ifBlank { "empty" }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    initialLocales = getApplicationLocales()
                    val targetLocale = LocaleListCompat.create(locale)
                    Log.d(TAG, "initial in-app locales is $appLocalesLanguageTags")
                    setApplicationLocaleInLooper(Looper.getMainLooper(), targetLocale)
                    base.evaluate()
                } catch (throwable: Throwable) {
                    val testName = "${description.testClass.simpleName}\$${description.methodName}"
                    val errorMessage =
                        "Test $testName failed on setting inAppLocale to ${locale.toLanguageTag()}"
                    Log.e(TAG, errorMessage)
                    throw throwable
                } finally {
                    setApplicationLocaleInLooper(Looper.getMainLooper(), initialLocales)
                }
            }
        }
    }

    private fun setApplicationLocaleInLooper(looper: Looper, locale: LocaleListCompat) {
        Handler(looper).post {
            setApplicationLocales(locale)
            Log.d(TAG, "in-app locales set to $appLocalesLanguageTags")
        }
    }
}
