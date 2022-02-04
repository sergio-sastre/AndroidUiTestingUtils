package sergio.sastre.uitesting.utils.common

enum class FontScale(val value: String) {
    SMALL(0.85f.toString()),
    NORMAL(1f.toString()),
    LARGE(1.15f.toString()),
    HUGE(1.3f.toString());

    companion object {
        @JvmStatic
        fun from(scale: Float): FontScale {
            for (fontScale in values()) {
                if (fontScale.value == scale.toString()) {
                    return fontScale
                }
            }
            throw IllegalArgumentException("Unknown scale: $scale")
        }
    }
}