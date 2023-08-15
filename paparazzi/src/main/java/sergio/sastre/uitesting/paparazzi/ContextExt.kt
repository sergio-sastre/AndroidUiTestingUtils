package sergio.sastre.uitesting.paparazzi

import android.content.Context
import android.content.res.Configuration
import sergio.sastre.uitesting.utils.common.DisplaySize

@Suppress("DEPRECATION")
fun Context.setDisplaySize(displaySize: DisplaySize) =
    this.apply {
        val density = resources.configuration.densityDpi
        val config = Configuration(resources.configuration)
        config.densityDpi = (density * displaySize.value.toFloat()).toInt()
        resources.updateConfiguration(config, resources.displayMetrics)
    }
