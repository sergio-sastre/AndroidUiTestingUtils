package sergio.sastre.uitesting.utils.activityscenario

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.testrules.accessibility.FontWeightTestRule
import sergio.sastre.uitesting.utils.testrules.displaysize.DisplaySizeTestRule
import sergio.sastre.uitesting.utils.testrules.fontsize.FontSizeTestRule
import sergio.sastre.uitesting.utils.testrules.locale.SystemLocaleTestRule
import sergio.sastre.uitesting.utils.testrules.uiMode.UiModeTestRule
import sergio.sastre.uitesting.utils.utils.waitForActivity

class ActivityScenarioForActivityRule<T : Activity> private constructor() : ExternalResource() {

    lateinit var activityScenario: ActivityScenario<T>
        private set

    private var fontSizeTestRule: FontSizeTestRule? = null
    private var systemLocaleTestRule: SystemLocaleTestRule? = null
    private var displaySizeTestRule: DisplaySizeTestRule? = null
    private var uiModeTestRule: UiModeTestRule? = null

    private var fontWeightTestRule: FontWeightTestRule? = null

    val activity: Activity by lazy { activityScenario.waitForActivity() }

    constructor(
        intent: Intent,
        activityOptions: Bundle? = null,
        config: ActivityConfigItem? = null,
    ) : this() {
        activityScenario = activityScenarioConfig(config).launch<T>(intent, activityOptions)
            .onActivity {
                it.runOnUiThread { createRules(config) }
            }
    }

    constructor(
        clazz: Class<T>,
        config: ActivityConfigItem? = null,
    ) : this() {
        activityScenario = activityScenarioConfig(config).launch(clazz)
            .onActivity {
                it.runOnUiThread { createRules(config) }
            }
    }

    override fun apply(base: Statement, description: Description): Statement {
        val listRule = listOfNotNull(
            fontWeightTestRule,
            fontSizeTestRule,
            displaySizeTestRule,
            systemLocaleTestRule,
            uiModeTestRule,
        )

        // we need to call super, otherwise after() will not be called
        val externalResourceStatement = super.apply(base, description)
        return if (listRule.isEmpty()) {
            externalResourceStatement
        } else {
            listRule.buildRuleChain().apply(externalResourceStatement, description)
        }
    }

    private fun List<TestRule>.buildRuleChain(): TestRule {
        var rule = RuleChain.outerRule(first())
        if (size == 1) {
            return rule
        }
        subList(1, size).forEach {
            rule = rule.around(it)
        }
        return rule
    }

    private fun createRules(config: ActivityConfigItem?) {
        config?.apply {
            fontWeightTestRule = fontWeight?.let { FontWeightTestRule(it) }
            fontSizeTestRule = fontSize?.let { FontSizeTestRule(it) }
            displaySizeTestRule = displaySize?.let { DisplaySizeTestRule(it) }
            systemLocaleTestRule = systemLocale?.let { SystemLocaleTestRule(it) }
            uiModeTestRule = uiMode?.let { UiModeTestRule(it) }
        }
    }

    private fun activityScenarioConfig(config: ActivityConfigItem?) =
        ActivityScenarioConfigurator.ForActivity().apply {
            config?.orientation?.also { orientation -> setOrientation(orientation) }
        }

    override fun after() {
        activityScenario.close()
    }
}
