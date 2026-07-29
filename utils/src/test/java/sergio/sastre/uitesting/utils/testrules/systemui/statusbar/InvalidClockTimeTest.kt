package sergio.sastre.uitesting.utils.testrules.systemui.statusbar

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class InvalidClockTimeTest(private val invalidTime: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "from {0} throws exception")
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf("1:30"),    // too short
            arrayOf("12:300"),  // too long
            arrayOf("12330"),   // missing colon
            arrayOf("123:0"),   // wrong colon position
            arrayOf("+1:30"),   // plus sign
            arrayOf("-1:30"),   // minus sign
            arrayOf("aa:bb"),   // non-numeric
            arrayOf("24:00"),   // invalid hour
            arrayOf("12:60")    // invalid minute
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `from invalid string throws exception`() {
        ClockTime.from(invalidTime)
    }
}
