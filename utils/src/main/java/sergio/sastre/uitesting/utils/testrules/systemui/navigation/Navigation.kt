package sergio.sastre.uitesting.utils.testrules.systemui.navigation

enum class Navigation(val adbValue: String, val navModeValue: Int) {
    GESTURAL("com.android.internal.systemui.navbar.gestural", 2),
    THREE_BUTTON("com.android.internal.systemui.navbar.threebutton", 0)
}