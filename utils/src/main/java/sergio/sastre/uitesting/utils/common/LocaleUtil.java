package sergio.sastre.uitesting.utils.common;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Code from fastlane Screengrab
 * https://github.com/fastlane/fastlane/blob/master/screengrab/screengrab-lib/src/main/java/tools.fastlane.screengrab/locale
 *
 * Converting it to Kotlin led to errors while using pseudlocales
 */
public final class LocaleUtil {
    private static final String TAG = LocaleUtil.class.getSimpleName();

    @SuppressLint({"PrivateApi"})
    public static LocaleListCompat changeDeviceLocaleTo(LocaleListCompat locale) {
        if (locale == null) {
            Log.w(TAG, "Skipping setting device locale to null");
            return null;
        } else {
            if (Build.VERSION.SDK_INT >= 24) {
                LocaleList.setDefault(locale.getLocaleList());
            } else {
                Locale.setDefault(locale.getLocale());
            }

            try {
                Class<?> amnClass = Class.forName("android.app.ActivityManagerNative");
                Method methodGetDefault = amnClass.getMethod("getDefault");
                methodGetDefault.setAccessible(true);
                Object activityManagerNative = methodGetDefault.invoke(amnClass);
                if (Build.VERSION.SDK_INT >= 26) {
                    amnClass = Class.forName(activityManagerNative.getClass().getName());
                }

                Method methodGetConfiguration = amnClass.getMethod("getConfiguration");
                methodGetConfiguration.setAccessible(true);
                Configuration config = (Configuration)methodGetConfiguration.invoke(activityManagerNative);
                config.getClass().getField("userSetLocale").setBoolean(config, true);
                LocaleListCompat ret;
                if (Build.VERSION.SDK_INT >= 24) {
                    ret = new LocaleListCompat(config.getLocales());
                } else {
                    ret = new LocaleListCompat(config.locale);
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    config.setLocales(locale.getLocaleList());
                } else {
                    config.locale = locale.getLocale();
                }

                config.setLayoutDirection(locale.getPreferredLocale());
                Method updateConfigurationMethod = amnClass.getMethod("updateConfiguration", Configuration.class);
                updateConfigurationMethod.setAccessible(true);
                updateConfigurationMethod.invoke(activityManagerNative, config);
                Log.d(TAG, "Locale changed to " + locale);
                return ret;
            } catch (Exception var8) {
                Log.e(TAG, "Failed to change device locale to " + locale, var8);
                return null;
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public static void changeDeviceLocaleTo(Locale locale) {
        changeDeviceLocaleTo(new LocaleListCompat(locale));
    }

    public static String[] localePartsFrom(String localeString) {
        if (localeString == null) {
            return null;
        } else {
            String[] localeParts = localeString.split("[_\\-]");
            return localeParts.length >= 1 && localeParts.length <= 3 ? localeParts : null;
        }
    }

    public static Locale localeFromParts(String[] localeParts) {
        if (localeParts != null && localeParts.length != 0) {
            if (localeParts.length == 1) {
                return new Locale(localeParts[0]);
            } else {
                return localeParts.length == 2 ? new Locale(localeParts[0], localeParts[1]) : new Locale(localeParts[0], localeParts[1], localeParts[2]);
            }
        } else {
            return null;
        }
    }

    public static Locale localeFromString(String locale) {
        return localeFromParts(localePartsFrom(locale));
    }

    public static String getTestLocale() {
        return InstrumentationRegistry.getArguments().getString("testLocale");
    }

    private LocaleUtil() {
    }
}
