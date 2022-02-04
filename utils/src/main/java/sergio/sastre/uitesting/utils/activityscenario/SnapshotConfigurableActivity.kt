package sergio.sastre.uitesting.utils.activityscenario

import android.content.Context
import androidx.annotation.CallSuper

interface SnapshotConfigurableActivity {

    @CallSuper
    fun attachBaseContext(newBase: Context?)
}