package sergio.sastre.uitesting.paparazzi

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import sergio.sastre.uitesting.utils.common.DisplaySize
import sergio.sastre.uitesting.utils.common.FontWeight

@Suppress("DEPRECATION")
internal fun Context.setDisplaySize(displaySize: DisplaySize) =
    this.apply {
        val density = resources.configuration.densityDpi
        val config = Configuration(resources.configuration)
        config.densityDpi = (density * displaySize.value.toFloat()).toInt()
        resources.updateConfiguration(config, resources.displayMetrics)
    }

@Suppress("DEPRECATION")
internal fun Context.setFontWeight(fontWeight: FontWeight) =
    this.apply {
        when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            true -> {
                val config = Configuration(resources.configuration)
                config.fontWeightAdjustment = fontWeight.value
                resources.updateConfiguration(config, resources.displayMetrics)
            }

            false -> Log.d(
                this.javaClass.simpleName,
                "Skipping FontWeightAdjustment. It can only be used on API 31+, and the current API is ${Build.VERSION.SDK_INT}"
            )
        }
    }
