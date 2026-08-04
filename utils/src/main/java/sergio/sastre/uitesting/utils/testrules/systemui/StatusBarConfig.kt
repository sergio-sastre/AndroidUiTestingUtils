package sergio.sastre.uitesting.utils.testrules.systemui

import sergio.sastre.uitesting.utils.testrules.systemui.statusbar.ClockTime

data class StatusBarConfig(
    val clockTime: ClockTime = ClockTime(12, 30),
    val showWifiIcon: Boolean = true,
)