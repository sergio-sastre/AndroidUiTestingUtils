package sergio.sastre.uitesting.sharedtest.roborazzi.wrapper

sealed interface CaptureType {
    object Screenshot : CaptureType
    object Dump : CaptureType
}