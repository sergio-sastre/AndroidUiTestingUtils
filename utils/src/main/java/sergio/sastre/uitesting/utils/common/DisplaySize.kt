package sergio.sastre.uitesting.utils.common

enum class DisplaySize(val value: String) {
    SMALL(0.85f.toString()),
    NORMAL(1f.toString()),
    LARGE(1.1f.toString()),
    LARGER(1.2f.toString()),
    LARGEST(1.3f.toString());

    companion object {
        @JvmStatic
        fun from(scale: Float): DisplaySize {
            for (displaySize in values()) {
                if (displaySize.value == scale.toString()) {
                    return displaySize
                }
            }
            throw IllegalArgumentException("Unknown display scale: $scale")
        }
    }
}
