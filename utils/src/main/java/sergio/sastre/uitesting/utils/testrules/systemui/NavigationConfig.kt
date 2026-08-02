package sergio.sastre.uitesting.utils.testrules.systemui

import sergio.sastre.uitesting.utils.testrules.systemui.navigation.Navigation
import sergio.sastre.uitesting.utils.testrules.systemui.navigation.ThreeButtonIdentifier

data class NavigationConfig(
    val mode: Navigation = Navigation.GESTURAL,
    val customThreeButtonIdentifiers: Set<ThreeButtonIdentifier> = emptySet(),
)