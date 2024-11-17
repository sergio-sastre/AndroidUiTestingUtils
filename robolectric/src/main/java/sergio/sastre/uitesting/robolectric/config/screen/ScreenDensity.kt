package sergio.sastre.uitesting.robolectric.config.screen

interface DpiDensity {
    val dpi: Int
    class Value(override val dpi: Int) : DpiDensity

    fun valueAsQualifier(): String =
        when (this is ScreenDensity) {
            true -> this.qualifier
            false -> this.dpi.toString() + "dpi"
        }
}

enum class ScreenDensity(val qualifier: String, override val dpi: Int): DpiDensity {
    XXXHDPI("xxxhdpi", 640),
    DPI_560("560dpi", 560),
    XXHDPI("xxhdpi", 480),
    DPI_440("440dpi", 440),
    DPI_420("420dpi", 420),
    DPI_400("400dpi", 400),
    DPI_360("360dpi", 360),
    XHDPI("xhdpi", 480),
    DPI_260("260dpi", 260),
    DPI_280("280dpi", 280),
    DPI_300("300dpi", 300),
    DPI_340("340dpi", 340),
    HDPI("hdpi", 240),
    DPI_220("220dpi", 220),
    TVDPI("tvdpi", 213),
    DPI_200("200dpi", 200),
    DPI_180("180dpi", 180),
    MDPI("mdpi", 160),
    DPI_140("140dpi", 140),
    LDPI("ldpi", 120),
    ANYDPI("anydpi", 0xFFFE),
    NODPI("nodpi", 0xFFFF),
}
