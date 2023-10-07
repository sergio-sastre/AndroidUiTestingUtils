package sergio.sastre.uitesting.utils.testrules.uiMode

import sergio.sastre.uitesting.utils.common.UiMode

internal interface UiModeSetting {
    fun changeUiMode(uiMode: UiMode)
    fun resetUiMode(uiModeInt: Int)
}
