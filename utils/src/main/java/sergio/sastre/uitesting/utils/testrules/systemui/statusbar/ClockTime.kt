package sergio.sastre.uitesting.utils.testrules.systemui.statusbar

import java.util.Locale

/**
 * A data class representing a valid clock time in 24h format.
 */
data class ClockTime(
    val hour: Int,
    val minute: Int,
) {
    init {
        require(hour in 0..23) { "Hour must be between 0 and 23. It was $hour" }
        require(minute in 0..59) { "Minute must be between 0 and 59. It was $minute" }
    }

    override fun toString(): String = String.format(Locale.US, "%02d:%02d", hour, minute)

    internal fun toHhmmString(): String = String.format(Locale.US, "%02d%02d", hour, minute)

    companion object {

        /**
         * Creates a [ClockTime] from a string in "hh:mm" format.
         */
        fun from(time: String): ClockTime {
            val parts = time.split(":")
            require(parts.size == 2) { "Time must be in hh:mm format (e.g., 12:30). It was $time" }
            return try {
                ClockTime(parts[0].toInt(), parts[1].toInt())
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid time format: $time", e)
            }
        }
    }
}
