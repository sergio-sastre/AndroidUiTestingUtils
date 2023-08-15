package sergio.sastre.uitesting.roborazzi

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioForComposableRule

fun RobolectricActivityScenarioForComposableRule.setContent(
    composable: @Composable () -> Unit,
): RobolectricActivityScenarioForComposableRule {
    this.activityScenario
        .onActivity {
            it.setContent { composable.invoke() }
        }

    return this
}