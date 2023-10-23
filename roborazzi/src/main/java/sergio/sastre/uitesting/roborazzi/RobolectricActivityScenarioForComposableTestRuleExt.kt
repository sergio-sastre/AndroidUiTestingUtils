package sergio.sastre.uitesting.roborazzi

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.provideRoborazziContext
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

fun RobolectricActivityScenarioForComposableRule.captureRoboImage(
    composable: @Composable () -> Unit,
){
    activityScenario
        .onActivity {
            it.setContent { composable.invoke() }
        }

    composeRule.onRoot().captureRoboImage()
}

fun RobolectricActivityScenarioForComposableRule.captureRoboImage(
    filePath: String,
    composable: @Composable () -> Unit,
){
    activityScenario
        .onActivity {
            it.setContent { composable.invoke() }
        }

    composeRule.onRoot().captureRoboImage(filePath = filePath)
}

fun RobolectricActivityScenarioForComposableRule.captureRoboImage(
    filePath: String,
    roborazziOptions: RoborazziOptions,
    composable: @Composable () -> Unit,
){
    activityScenario
        .onActivity {
            it.setContent { composable.invoke() }
        }

    composeRule.onRoot().captureRoboImage(
        filePath = filePath,
        roborazziOptions = roborazziOptions,
    )
}