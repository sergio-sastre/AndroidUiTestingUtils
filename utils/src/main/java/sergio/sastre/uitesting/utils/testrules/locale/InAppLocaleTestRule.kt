package sergio.sastre.uitesting.utils.testrules.locale

import android.os.Build
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForActivityRule
import sergio.sastre.uitesting.utils.common.LocaleUtil
import java.util.*

/**
 * A TestRule to change the in-app locales of the application ONLY.
 * The Locale of the System does not change. Use SystemLocaleTestRule instead for that.
 * Beware that in-app locales prevail over the system locale while displaying texts.
 *
 * WARNING: This TestRule works on API 32 or lower if autoStoreLocale = false.
 * Otherwise, it is not ensured to work as expected
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
 *
 * WARNING 1: It's not compatible with Robolectric
 * WARNING 2: If you are also using [SystemLocaleTestRule], make sure that [InAppLocaleTestRule] is applied after it (e.g. has a higher order number).
 *            Otherwise, the System Locale is not reset correctly on API 33+
 **/
class InAppLocaleTestRule private constructor(locale: Locale) : TestRule {

    private var testRule: TestRule? = null
    /**
     * Applies [locale] as in-app-locale.
     * By passing the [activityScenarioRule], it can set in-app-locale properly regardless of the API level
     */
    constructor(
        locale: Locale,
        activityScenarioRule: ActivityScenarioRule<*>
    ) : this(locale) {
        this.testRule = activityScenarioRule
    }

    /**
     * Applies [locale] as in-app-locale.
     * By passing the [activityScenarioRule], it can set in-app-locale properly regardless of the API level
     */
    constructor(
        locale: String,
        activityScenarioRule: ActivityScenarioRule<*>
    ) : this(LocaleUtil.localeFromString(locale)) {
        this.testRule = activityScenarioRule
    }

    /**
     * Applies [locale] as in-app-locale.
     * By passing the [activityScenarioForActivityRule], it can set in-app-locale properly regardless of the API level
     */
    constructor(
        locale: Locale,
        activityScenarioForActivityRule: ActivityScenarioForActivityRule<*>
    ) : this(locale) {
        this.testRule = activityScenarioForActivityRule
    }

    /**
     * Applies [locale] as in-app-locale.
     * By passing the [activityScenarioForActivityRule], it can set in-app-locale properly regardless of the API level
     */
    constructor(
        locale: String,
        activityScenarioForActivityRule: ActivityScenarioForActivityRule<*>
    ) : this(LocaleUtil.localeFromString(locale)) {
        this.testRule = activityScenarioForActivityRule
    }

    private val inAppLocaleRule = ApiDependentInAppLocaleTestRule(locale)

    override fun apply(
        base: Statement,
        description: Description
    ): Statement {
        val ruleToApply = when {
            testRule == null -> inAppLocaleRule
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ->
                RuleChain
                    .outerRule(inAppLocaleRule)
                    .around(testRule)

            else ->
                RuleChain
                    .outerRule(testRule)
                    .around(inAppLocaleRule)
        }
        return ruleToApply.apply(base, description)
    }
}