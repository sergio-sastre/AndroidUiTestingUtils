package sergio.sastre.uitesting.utils.testrules.locale

import android.Manifest.permission.CHANGE_CONFIGURATION
import android.content.pm.PackageManager.*
import androidx.core.content.ContextCompat.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.LocaleListCompat
import sergio.sastre.uitesting.utils.common.LocaleUtil
import java.lang.StringBuilder
import java.util.*
import kotlin.jvm.JvmOverloads
import kotlin.Throws

class LocaleTestRule : TestRule {
    private val testLocale: Locale?
    private val testLocaleString: String?
    private val uiAutomation = getInstrumentation().uiAutomation
    private val targetContext = getInstrumentation().targetContext
    private val packageName = getInstrumentation().targetContext.packageName

    @JvmOverloads
    constructor(testLocale: String? = LocaleUtil.testLocale) {
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
                grantChangeConfigurationIfNeeded()
                var original: LocaleListCompat? = null
                try {
                    if (testLocale != null) {
                        original = LocaleUtil.changeDeviceLocaleTo(LocaleListCompat(testLocale))
                    }
                    base.evaluate()
                } finally {
                    if (original != null) {
                        LocaleUtil.changeDeviceLocaleTo(original)
                    }
                }
            }
        }
    }

    private fun grantChangeConfigurationIfNeeded() {
        if (checkSelfPermission(targetContext, CHANGE_CONFIGURATION) != PERMISSION_GRANTED) {
            /*
          The following fails on API 27 or before
          UiAutomation.grantRuntimePermission(...)

          Therefore better to use .executeShellCommand(..)
         */
            uiAutomation.executeShellCommand(
                "pm grant $packageName android.permission.CHANGE_CONFIGURATION"
            )
        }
    }
}