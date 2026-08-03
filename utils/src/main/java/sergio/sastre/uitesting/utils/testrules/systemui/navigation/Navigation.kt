package sergio.sastre.uitesting.utils.testrules.systemui.navigation

import android.os.Build

enum class Navigation(
    val adbValue: String,
    val navModeValue: Int,
    val available: () -> Boolean
) {
    GESTURAL(
        adbValue = "com.android.internal.systemui.navbar.gestural",
        navModeValue = 2,
        available = { Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q }
    ),
    THREE_BUTTON(
        adbValue = "com.android.internal.systemui.navbar.threebutton",
        navModeValue = 0,
        available = { true }
    )
}