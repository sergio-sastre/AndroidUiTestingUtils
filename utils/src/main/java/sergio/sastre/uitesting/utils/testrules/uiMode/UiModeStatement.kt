package  sergio.sastre.uitesting.utils.testrules.uiMode

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.UiMode

class UiModeStatement(
  private val base: Statement,
  private val description: Description,
  private val mode: UiMode
) : Statement() {

  override fun evaluate() {

      Handler(Looper.getMainLooper()).post {
        AppCompatDelegate.setDefaultNightMode(mode.appCompatDelegateInt)
      }
      try {
        base.evaluate()
      } catch (throwable: Throwable) {
        val testName = "${description.testClass.simpleName}\$${description.methodName}"
        val errorMessage = "Test $testName failed on UiMode: [${getDayNightModeName(mode.appCompatDelegateInt)}]"
        Log.e("UiMode error", errorMessage)
        throw throwable
      }
  }

  private fun getDayNightModeName(mode: Int): String = when (mode) {
    AppCompatDelegate.MODE_NIGHT_AUTO -> "MODE_NIGHT_AUTO"
    AppCompatDelegate.MODE_NIGHT_NO -> "MODE_NIGHT_NO"
    AppCompatDelegate.MODE_NIGHT_YES -> "MODE_NIGHT_YES"
    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> "MODE_NIGHT_FOLLOW_SYSTEM"
    else -> "Unknown"
  }
}