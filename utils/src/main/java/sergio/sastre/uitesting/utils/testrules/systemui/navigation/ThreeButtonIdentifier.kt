package sergio.sastre.uitesting.utils.testrules.systemui.navigation

data class ThreeButtonIdentifier(
    val back: ButtonIdentifier,
    val home: ButtonIdentifier,
    val recents: ButtonIdentifier,
)

data class ButtonIdentifier(
    val resPackage: String,
    val resId: String,
)

internal val STANDARD_THREE_BUTTON_IDENTIFIER =
    ThreeButtonIdentifier(
        back = ButtonIdentifier("com.android.systemui", "back"),
        home = ButtonIdentifier("com.android.systemui","home"),
        recents = ButtonIdentifier("com.android.systemui","recent_apps")
    )

internal val NEXUS_THREE_BUTTON_IDENTIFIER =
    ThreeButtonIdentifier(
        back = ButtonIdentifier("com.google.android.apps.nexuslauncher", "back"),
        home = ButtonIdentifier("com.google.android.apps.nexuslauncher","home"),
        recents = ButtonIdentifier("com.google.android.apps.nexuslauncher","recent_apps")
    )