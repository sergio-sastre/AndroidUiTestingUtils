package sergio.sastre.uitesting.sharedtest.roborazzi.wrapper

data class RoborazziOptions(
    val captureType: CaptureType = CaptureType.Screenshot,
    val compareChangeThreshold: Float = 0.01F,
)
