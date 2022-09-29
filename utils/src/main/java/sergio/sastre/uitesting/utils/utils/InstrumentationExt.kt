package sergio.sastre.uitesting.utils.utils

import android.Manifest
import android.app.Instrumentation
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Instrumentation.grantChangeConfigurationIfNeeded() {
    if (ContextCompat.checkSelfPermission(
            targetContext,
            Manifest.permission.CHANGE_CONFIGURATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        /*
      The following fails on API 27 or before
      UiAutomation.grantRuntimePermission(...)

      Therefore better to use .executeShellCommand(..)
     */
        uiAutomation.executeShellCommand(
            "pm grant ${targetContext.packageName} android.permission.CHANGE_CONFIGURATION"
        )
    }
}
