package sergio.sastre.uitesting.utils.common

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import java.lang.Exception
import java.util.*

object LocaleUtil {
    private val TAG = LocaleUtil::class.java.simpleName

    @SuppressLint("PrivateApi")
    fun changeDeviceLocaleTo(locale: LocaleListCompat?): LocaleListCompat? {
        if (locale == null) {
            Log.w(TAG, "Skipping setting device locale to null")
            return null
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = locale.localeList ?: LocaleList.getDefault()
            LocaleList.setDefault(localeList)
        } else {
            val defaultLocale = locale.locale ?: Locale.getDefault()
            Locale.setDefault(defaultLocale)
        }
        return try {
            var amnClass = Class.forName("android.app.ActivityManagerNative")
            val methodGetDefault = amnClass.getMethod("getDefault")
            methodGetDefault.isAccessible = true
            val activityManagerNative = methodGetDefault.invoke(amnClass)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // getConfiguration moved from ActivityManagerNative to ActivityManagerProxy
                amnClass = Class.forName(activityManagerNative.javaClass.name)
            }
            val methodGetConfiguration = amnClass.getMethod("getConfiguration")
            methodGetConfiguration.isAccessible = true
            val config = methodGetConfiguration.invoke(activityManagerNative) as Configuration
            config.javaClass.getField("userSetLocale").setBoolean(config, true)
            val ret: LocaleListCompat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleListCompat(config.locales)
            } else {
                LocaleListCompat(config.locale)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) config.setLocales(locale.localeList) else config.locale =
                locale.locale
            config.setLayoutDirection(locale.preferredLocale)
            val updateConfigurationMethod =
                amnClass.getMethod("updateConfiguration", Configuration::class.java)
            updateConfigurationMethod.isAccessible = true
            updateConfigurationMethod.invoke(activityManagerNative, config)
            Log.d(TAG, "Locale changed to $locale")
            ret
        } catch (e: Exception) {
            Log.e(TAG, "Failed to change device locale to $locale", e)
            // ignore the error, it happens for example if run from Android Studio rather than Fastlane
            null
        }
    }

    @Deprecated(
        message = "Prefer LocaleListCompat to Locale, which is used in this method under the hood",
        replaceWith = ReplaceWith("changeDeviceLocaleTo(locale: LocaleListCompat?)")
    )
    fun changeDeviceLocaleTo(locale: Locale?) {
        changeDeviceLocaleTo(LocaleListCompat(locale))
    }

    fun localePartsFrom(localeString: String?): Array<String>? {
        if (localeString == null) {
            return null
        }
        val localeParts = localeString.split("[_\\-]").toTypedArray()
        return if (localeParts.isEmpty() || localeParts.size > 3) {
            null
        } else localeParts
    }

    fun localeFromParts(localeParts: Array<String>?): Locale? {
        return if (localeParts == null || localeParts.isEmpty()) {
            null
        } else if (localeParts.size == 1) {
            Locale(localeParts[0])
        } else if (localeParts.size == 2) {
            Locale(localeParts[0], localeParts[1])
        } else {
            Locale(localeParts[0], localeParts[1], localeParts[2])
        }
    }

    fun localeFromString(locale: String?): Locale? {
        return localeFromParts(localePartsFrom(locale))
    }

    val testLocale: String?
        get() = InstrumentationRegistry.getArguments().getString("testLocale")
}