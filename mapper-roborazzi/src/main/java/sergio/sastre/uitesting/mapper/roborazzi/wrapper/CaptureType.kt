package sergio.sastre.uitesting.mapper.roborazzi.wrapper

import sergio.sastre.uitesting.mapper.roborazzi.wrapper.DumpExplanation.DefaultExplanation

sealed interface CaptureType {
    object Screenshot : CaptureType
    data class Dump(val explanation: DumpExplanation = DefaultExplanation) : CaptureType
}

enum class DumpExplanation {
    AccessibilityExplanation,
    DefaultExplanation,
}
