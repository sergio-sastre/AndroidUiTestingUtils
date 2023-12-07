package sergio.sastre.uitesting.mapper.roborazzi.wrapper

data class CompareOptions(
    val changeThreshold: Float = 0.01F,
    val outputDirectoryPath: String = DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH,
    val comparisonStyle: ComparisonStyle = ComparisonStyle.Grid(),
)

sealed interface ComparisonStyle {
    data class Grid(
        val bigLineSpaceDp: Int? = 16,
        val smallLineSpaceDp: Int? = 4,
        val hasLabel: Boolean = true
    ) : ComparisonStyle

    object Simple : ComparisonStyle
}

const val DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH = "build/outputs/roborazzi"
