package sergio.sastre.uitesting.utils.testrules.uiMode

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import org.junit.runner.Description
import org.junit.runners.model.Statement
import sergio.sastre.uitesting.utils.common.UiMode

internal class AppCompatDelegateUiModeSetter(
    val base: Statement,
    val description: Description
): UiModeSetter {

    companion object {
        private val TAG = AppCompatDelegateUiModeSetter::class.java.simpleName
    }

    override fun setUiModeDuringTestOnly(uiMode: UiMode){
        var initialUiMode = UiMode.DAY.appCompatDelegateInt
        try {
            Handler(Looper.getMainLooper()).post {
                initialUiMode = AppCompatDelegate.getDefaultNightMode()
                AppCompatDelegate.setDefaultNightMode(uiMode.appCompatDelegateInt)
            }
            base.evaluate()
        } catch (throwable: Throwable) {
            val testName = "${description.testClass.simpleName}\$${description.methodName}"
            val errorMessage =
                "Test $testName failed on setting uiMode to ${uiMode.name}"
            Log.e(TAG, errorMessage)
            throw throwable
        } finally {
            Handler(Looper.getMainLooper()).post {
                AppCompatDelegate.setDefaultNightMode(initialUiMode)
            }
        }
    }
}