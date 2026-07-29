package sergio.sastre.uitesting.utils.testrules.systemui.statusbar

import org.junit.Assert.assertEquals
import org.junit.Test

class ValidClockTimeTest {

    @Test
    fun `from valid string returns ClockTime`() {
        val time = ClockTime.from("12:30")
        assertEquals(12, time.hour)
        assertEquals(30, time.minute)
    }

    @Test
    fun `from boundary valid strings returns ClockTime`() {
        val startOfDay = ClockTime.from("00:00")
        assertEquals(0, startOfDay.hour)
        assertEquals(0, startOfDay.minute)

        val endOfDay = ClockTime.from("23:59")
        assertEquals(23, endOfDay.hour)
        assertEquals(59, endOfDay.minute)
    }

    @Test
    fun `toString returns formatted string`() {
        assertEquals("12:30", ClockTime(12, 30).toString())
        assertEquals("09:05", ClockTime(9, 5).toString())
    }

    @Test
    fun `toHhmmString returns formatted string without colon`() {
        assertEquals("1230", ClockTime(12, 30).toHhmmString())
        assertEquals("0905", ClockTime(9, 5).toHhmmString())
    }
}
