package sergio.sastre.uitesting.utils.common

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

enum class UiMode(val appCompatDelegateInt: Int, val configurationInt: Int) {
    NIGHT(AppCompatDelegate.MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_YES),
    DAY(AppCompatDelegate.MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_NO);
}
