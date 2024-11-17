package sergio.sastre.uitesting.mapper.paparazzi.wrapper

interface DpiDensity {
    val dpi: Int
    class Value(override val dpi: Int) : DpiDensity
}

enum class Density(override val dpi: Int) : DpiDensity {
    XXXHIGH(640),
    DPI_560(560),
    XXHIGH(480),
    DPI_440(440),
    DPI_420(420),
    DPI_400(400),
    DPI_360(360),
    XHIGH(480),
    DPI_260(260),
    DPI_280(280),
    DPI_300(300),
    DPI_340(340),
    HIGH(240),
    DPI_220(220),
    TV(213),
    DPI_200(200),
    DPI_180(180),
    MEDIUM(160),
    DPI_140(140),
    LOW(120),
    ANYDPI(0xFFFE),
    NODPI(0xFFFF)
}
