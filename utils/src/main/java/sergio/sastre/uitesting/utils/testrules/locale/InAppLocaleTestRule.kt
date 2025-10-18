package sergio.sastre.uitesting.utils.testrules.locale

import android.os.Build
import org.junit.rules.RuleChain
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
 * WARNING: It's not compatible with Robolectric
 **/
class InAppLocaleTestRule
/**
 * Applies [locale] as in-app-locale.
 *
 * WARNING: The order in which this rule applies is important:
 * 1. For API < 33, the order must be lower than that of the ActivityScenarioRule
 * 2. For API 33+, the order must be greater than that of the ActivityScenarioRule
 * Alternatively, use InAppLocaleTestRule(locale, activityScenarioRule),
 * which handles order correctly on its own
 */
@Deprecated(
    message = "Use the (locale: Locale, activityScenarioRule: TestRule) constructor instead. This will be removed in version 2.9.0",
    replaceWith = ReplaceWith("InAppLocaleTestRule(locale, activityScenarioRule)")
)
constructor(private val locale: Locale) : TestRule {

    private var activityScenarioRule: TestRule? = null

    /**
     * Applies [testLocale] as in-app-locale.
     *
     * WARNING: The order in which this rule applies is important:
     * 1. For API < 33, the order must be lower than that of the ActivityScenarioRule
     * 2. For API 33+, the order must be greater than that of the ActivityScenarioRule
     * Alternatively, use InAppLocaleTestRule(locale, activityScenarioRule),
     * which handles order correctly on its own
     */
    @Deprecated(
        message = "Use the (locale: String, activityScenarioRule: TestRule) constructor instead. This will be removed in version 2.9.0",
        replaceWith = ReplaceWith("InAppLocaleTestRule(locale, activityScenarioRule)")
    )
    constructor(
        testLocale: String
    ) : this(LocaleUtil.localeFromString(testLocale)) {
        this.activityScenarioRule = null
    }

    /**
     * Applies [locale] as in-app-locale.
     * By passing the [activityScenarioRule], it can set in-app-locale properly regardless of the API level
     */
    constructor(
        locale: Locale,
        activityScenarioRule: TestRule
    ) : this(locale) {
        this.activityScenarioRule = activityScenarioRule
    }

    /**
     * Applies [locale] as in-app-locale.
     * By passing the [activityScenarioRule], it can set in-app-locale properly regardless of the API level
     */
    constructor(
        locale: String,
        activityScenarioRule: TestRule
    ) : this(LocaleUtil.localeFromString(locale)) {
        this.activityScenarioRule = activityScenarioRule
    }

    private val inAppLocaleRule = ApiDependentInAppLocaleTestRule(locale)

    override fun apply(
        base: Statement,
        description: Description
    ): Statement {
        val testRule = when {
            activityScenarioRule == null -> inAppLocaleRule
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ->
                RuleChain
                    .outerRule(inAppLocaleRule)
                    .around(activityScenarioRule)

            else ->
                RuleChain
                    .outerRule(activityScenarioRule)
                    .around(inAppLocaleRule)
        }
        return testRule.apply(base, description)
    }
}