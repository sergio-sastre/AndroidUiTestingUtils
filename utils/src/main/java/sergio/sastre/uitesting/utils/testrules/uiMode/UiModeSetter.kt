package sergio.sastre.uitesting.utils.testrules.uiMode

import sergio.sastre.uitesting.utils.common.UiMode

interface UiModeSetter {
    fun setUiModeDuringTestOnly(uiMode: UiMode)
}