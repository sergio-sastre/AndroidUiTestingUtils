package sergio.sastre.uitesting.utils.common

import android.os.LocaleList
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class LocaleListCompat {
    var locale: Locale? = null
        private set

    @get:RequiresApi(Build.VERSION_CODES.N)
    var localeList: LocaleList? = null
        private set

    constructor(locale: Locale?) {
        this.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) localeList = LocaleList(locale)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    constructor(localeList: LocaleList?) {
        this.localeList = localeList
    }

    val preferredLocale: Locale?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && localeList != null) {
            localeList!![0]
        } else {
            locale
        }
}