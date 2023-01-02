package  sergio.sastre.uitesting.utils.testrules.uiMode

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.UiMode

class UiModeStatement(
  private val base: Statement,
  private val description: Description,
  private val mode: UiMode
) : Statement() {

  override fun evaluate() {
      val initialUiMode = getDefaultNightMode()
      Handler(Looper.getMainLooper()).post {
          setDefaultNightMode(mode.appCompatDelegateInt)
      }
      try {
          base.evaluate()
      } catch (throwable: Throwable) {
          val testName = "${description.testClass.simpleName}\$${description.methodName}"
          val errorMessage =
              "Test $testName failed on UiMode: [${mode.name}]"
          Log.e("UiMode error", errorMessage)
          throw throwable
      } finally {
          Handler(Looper.getMainLooper()).post {
              setDefaultNightMode(initialUiMode)
          }
      }
  }
}
