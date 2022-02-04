package sergio.sastre.uitesting.utils.activityscenario

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import sergio.sastre.uitesting.utils.common.FontScale
import sergio.sastre.uitesting.utils.common.LocaleUtil
import java.util.*

object ActivityScenarioConfigurator {
    private var fontScale: FontScale? = null
    private var locale: Locale? = null

    @JvmInline
    value class StringLocale(val locale: String)

    class Builder {
        fun setFontSize(fontScale: FontScale): Builder {
            ActivityScenarioConfigurator.fontScale = fontScale
            return this
        }

        fun setLocale(locale: String): Builder {
            ActivityScenarioConfigurator.locale = LocaleUtil.localeFromString(locale)
            return this
        }

        fun setLocale(stringLocale: StringLocale): Builder {
            ActivityScenarioConfigurator.locale = LocaleUtil.localeFromString(stringLocale.locale)
            return this
        }

        fun setLocale(locale: Locale): Builder {
            ActivityScenarioConfigurator.locale = locale
            return this
        }

        fun <T> launchTestActivity(clazz: Class<T>): ActivityScenario<T>
                where T : Activity,
                      T : SnapshotConfigurableActivity = ActivityScenario.launch(clazz)

        fun launchConfiguredActivity(): ActivityScenario<SnapshotConfiguredActivity> =
            ActivityScenario.launch(SnapshotConfiguredActivity::class.java)
    }

    fun Context.wrap(): Context {
        val newConfig = Configuration(resources.configuration)
        fontScale?.run { newConfig.fontScale = value.toFloat() }
        locale?.run { newConfig.setLocale(this) }
        fontScale = null
        locale = null
        return createConfigurationContext(newConfig)
    }

    fun <T : Activity> launch(clazz: Class<T>): ActivityScenario<T> {
        if (fontScale != null || locale != null) {
            fontScale = null; locale = null
            throw RuntimeException("Do use FontSizeTestRule or/and LocaleTestRule instead")
        }
        return ActivityScenario.launch(clazz)
    }

    class SnapshotConfiguredActivity : AppCompatActivity() {
        override fun attachBaseContext(newBase: Context?) {
            super.attachBaseContext(newBase?.wrap())
        }
    }
}