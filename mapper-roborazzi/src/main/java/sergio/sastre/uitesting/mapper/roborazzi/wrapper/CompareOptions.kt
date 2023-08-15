package sergio.sastre.uitesting.mapper.roborazzi.wrapper

data class CompareOptions(
    val changeThreshold: Float = 0.01F,
    val outputDirectoryPath: String = DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH,
)

const val DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH = "build/outputs/roborazzi"
