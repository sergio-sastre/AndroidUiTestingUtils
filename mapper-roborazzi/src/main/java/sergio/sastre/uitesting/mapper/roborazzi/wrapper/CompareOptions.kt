package sergio.sastre.uitesting.mapper.roborazzi.wrapper

data class CompareOptions(
    val changeThreshold: Float = 0.0F,
    val outputDirectoryPath: String = DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH,
    val comparisonStyle: ComparisonStyle = ComparisonStyle.Grid(),
    val simpleImageComparator: SimpleImageComparator = SimpleImageComparator()
)

class SimpleImageComparator(
    val maxDistance: Float = 0.007F,
    val hShift: Int = 0,
    val vShift: Int = 0,
)

sealed interface ComparisonStyle {
    data class Grid(
        val bigLineSpaceDp: Int? = 16,
        val smallLineSpaceDp: Int? = 4,
        val hasLabel: Boolean = true
    ) : ComparisonStyle

    data object Simple : ComparisonStyle
}

const val DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH = "build/outputs/roborazzi"
