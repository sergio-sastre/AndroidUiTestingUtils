package sergio.sastre.uitesting.mapper.roborazzi.wrapper

data class RoborazziOptions(
    val captureType: CaptureType = CaptureType.Screenshot,
    val compareOptions: CompareOptions = CompareOptions(),
)
