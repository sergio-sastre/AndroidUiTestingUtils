package sergio.sastre.uitesting.utils.testrules.accessibility.colorcontrast

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import sergio.sastre.uitesting.utils.utils.waitForExecuteShellCommand

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
internal object ColorContrastSetting {
    fun getColorContrast(): ColorContrast {
        val uiModeManager = getInstrumentation().targetContext
            .getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val contrastValue = uiModeManager.contrast
        return ColorContrast.entries.firstOrNull { it.value == contrastValue } ?: ColorContrast.DEFAULT
    }

    fun setColorContrast(colorContrast: ColorContrast) {
        getInstrumentation().waitForExecuteShellCommand("settings put secure contrast_level ${colorContrast.value}")
    }
}