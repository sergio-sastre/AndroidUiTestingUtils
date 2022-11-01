package sergio.sastre.uitesting.utils.testrules.locale

import androidx.appcompat.app.AppCompatDelegate.getApplicationLocales
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.core.os.LocaleListCompat
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
                val initialLocales = getApplicationLocales()
                setApplicationLocales(LocaleListCompat.create(testLocale))
                base.evaluate()
                setApplicationLocales(initialLocales)
            }
        }
    }
}