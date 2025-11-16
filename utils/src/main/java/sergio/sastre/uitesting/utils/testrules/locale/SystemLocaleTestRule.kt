package sergio.sastre.uitesting.utils.testrules.locale

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
 *
 * WARNING 1: It's not compatible with Robolectric
 * WARNING 2: If you are also using [InAppLocaleTestRule], make sure that [SystemLocaleTestRule] is applied before it (e.g. has a lower order number).
 *            Otherwise, the System Locale is not reset correctly on API 36+
 */
class SystemLocaleTestRule constructor(private val locale: Locale) : TestRule {

    constructor(testLocale: String) : this(LocaleUtil.localeFromString(testLocale))

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                var original: LocaleListCompat? = null
                try {
                    grantChangeConfigurationIfNeeded()
                    original = LocaleUtil.changeDeviceLocaleTo(LocaleListCompat(locale))
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
