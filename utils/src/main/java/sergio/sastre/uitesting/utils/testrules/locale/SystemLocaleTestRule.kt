package sergio.sastre.uitesting.utils.testrules.locale

import android.content.pm.PackageManager.*
import androidx.core.content.ContextCompat.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.LocaleListCompat
import sergio.sastre.uitesting.utils.common.LocaleUtil
import sergio.sastre.uitesting.utils.utils.grantChangeConfigurationIfNeeded
import java.util.*
import kotlin.Throws

/**
 * A TestRule to change the Locale of the device via reflection and non-SDK APIs
 * (Configuration#updateConfiguration). It grants the CHANGE_CONFIGURATION permission via adb
 *
 * Code from fastlane Screengrab
 * @see https://github.com/fastlane/fastlane/blob/master/screengrab/screengrab-lib/src/main/java/tools.fastlane.screengrab/locale/LocaleTestRule.java
 *
 * The code was converted to Kotlin, and added the [grantChangeConfigurationIfNeeded] method
 * to enable locale change
 */
class SystemLocaleTestRule : TestRule {
    private val testLocale: Locale?

    init {
        grantChangeConfigurationIfNeeded()
    }

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
}
