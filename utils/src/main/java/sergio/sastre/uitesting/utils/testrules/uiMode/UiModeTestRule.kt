package sergio.sastre.uitesting.utils.testrules.uiMode

import android.os.Build
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.UiMode

/**
 * A TestRule to change the UiMode to day or night.
 *
 * It is set via Adb from API 30+. For older APIs, it uses AppCompatDelegate, based on Adevinta/Barista DayNightRule.
 * https://github.com/AdevintaSpain/Barista/tree/master/library%2Fsrc%2Fmain%2Fjava%2Fcom%2Fadevinta%2Fandroid%2Fbarista%2Frule%2Ftheme
 *
 * WARNING: It's not compatible with Robolectric
 */
class UiModeTestRule(private val mode: UiMode) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    true -> AdbUiModeSetter(base, description)
                    false -> AppCompatDelegateUiModeSetter(base, description)
                }.setUiModeDuringTestOnly(mode)
            }
        }
    }
}