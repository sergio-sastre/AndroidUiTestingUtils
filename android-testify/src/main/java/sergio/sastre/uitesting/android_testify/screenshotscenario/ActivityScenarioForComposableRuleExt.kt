package sergio.sastre.uitesting.android_testify.screenshotscenario

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import sergio.sastre.uitesting.utils.activityscenario.ActivityScenarioForComposableRule

fun ActivityScenarioForComposableRule.setContent(
    composable: @Composable () -> Unit,
): ActivityScenarioForComposableRule {
    this.activityScenario
        .onActivity {
            it.setContent { composable.invoke() }
        }

    return this
}
