package sergio.sastre.uitesting.utils.testrules.locale

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.core.os.LocaleListCompat
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.LocaleUtil
import java.lang.StringBuilder
import java.util.*
import kotlin.Throws

/**
 * A TestRule to change the Locale of the application.
 */
class LocaleTestRule : TestRule {
    private val testLocale: Locale?
    private val testLocaleString: String?

    constructor(testLocale: String) {
        this.testLocale = LocaleUtil.localeFromString(testLocale)
        testLocaleString = testLocale
    }

    @Deprecated("Prefer using a String")
    constructor(testLocale: Locale) {
        val sb = StringBuilder(testLocale.language)
        val localeCountry = testLocale.country
        if (localeCountry.isNotEmpty()) {
            sb.append("-").append(localeCountry)
        }
        testLocaleString = sb.toString()
        this.testLocale = testLocale
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                val initial = getInitialLocale()
                setApplicationLocales(LocaleListCompat.create(testLocale))
                base.evaluate()
                setApplicationLocales(LocaleListCompat.create(initial))
            }
        }
    }

    private fun getInitialLocale(): Locale =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getInstrumentation().targetContext.resources.configuration.locales[0]
        } else {
            getInstrumentation().targetContext.resources.configuration.locale
        }
}