package sergio.sastre.uitesting.mapper.roborazzi.wrapper

data class RecordOptions(
    val resizeScale: Double =
        checkNotNull(System.getProperty("roborazzi.record.resizeScale", "1.0")).toDouble(),
    val applyDeviceCrop: Boolean = false
)
