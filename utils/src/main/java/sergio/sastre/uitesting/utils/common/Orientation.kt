package sergio.sastre.uitesting.utils.common

import android.content.pm.ActivityInfo

enum class Orientation(val activityInfo: Int) {
    PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
    LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
}
