package sergio.sastre.uitesting.utils.common;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

/**
 * Code strongly based on that from fastlane Screengrab, but
 * 1. adding a bugfix for calling the updateConfiguration method, which was added to
 * the non-SDK interface list since API 28:
 * https://github.com/fastlane/fastlane/blob/master/screengrab/screengrab-lib/src/main/java/tools.fastlane.screengrab/locale
 * 2. Add support for complex Locales like "sr-Latn-RS", "sr-Cyrl-RS" or "zh-Latn-TW-pinyin"
 * <p>
 * Converting it to Kotlin led to errors while using pseudlocales
 */
public final class LocaleUtil {
    private static final String TAG = LocaleUtil.class.getSimpleName();
    private static final String EN_XA = "en_XA";
    private static final String AR_XB = "ar_XB";
    private static final Set<String> PSEUDOLOCALES = new HashSet<>(Arrays.asList(EN_XA, AR_XB));

    @SuppressWarnings({"JavaReflectionMemberAccess", "deprecation", "RedundantSuppression"})
    @SuppressLint({"PrivateApi"})
    public static LocaleListCompat changeDeviceLocaleTo(LocaleListCompat locale) {

        if (locale == null) {
            Log.w(TAG, "Skipping setting device locale to null");
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.setDefault(locale.getLocaleList());
        } else {
            Locale.setDefault(locale.getLocale());
        }

        try {
            Class<?> amnClass = Class.forName("android.app.ActivityManagerNative");
            Method methodGetDefault = amnClass.getMethod("getDefault");
            methodGetDefault.setAccessible(true);
            Object activityManagerNative = methodGetDefault.invoke(amnClass);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                amnClass = Class.forName(activityManagerNative.getClass().getName());
            }

            Method methodGetConfiguration = amnClass.getMethod("getConfiguration");
            methodGetConfiguration.setAccessible(true);
            Configuration config =
                    (Configuration) methodGetConfiguration.invoke(activityManagerNative);

            LocaleListCompat ret = updateLocale(locale, config);
            updateConfiguration(amnClass, activityManagerNative, config);
            Log.d(TAG, "Locale changed to " + locale);
            return ret;
        } catch (Exception var8) {
            Log.e(TAG, "Failed to change device locale to " + locale, var8);
            return null;
        }
    }

    private static LocaleListCompat updateLocale(
            LocaleListCompat locale,
            Configuration config
    ) throws NoSuchFieldException, IllegalAccessException {
        config.getClass().getField("userSetLocale").setBoolean(config, true);
        LocaleListCompat ret;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ret = new LocaleListCompat(config.getLocales());
        } else {
            ret = new LocaleListCompat(config.locale);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(locale.getLocaleList());
        } else {
            config.locale = locale.getLocale();
        }
        config.setLayoutDirection(locale.getPreferredLocale());
        return ret;
    }

    private static void updateConfiguration(
            Class<?> amnClass,
            Object activityManagerNative,
            Configuration config
    ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.invoke(
                    amnClass,
                    activityManagerNative,
                    "updateConfiguration",
                    config
            );
        } else {
            Method updateConfigurationMethod =
                    amnClass.getMethod("updateConfiguration", Configuration.class);
            updateConfigurationMethod.setAccessible(true);
            updateConfigurationMethod.invoke(activityManagerNative, config);
        }
    }

    public static void changeDeviceLocaleTo(Locale locale) {
        changeDeviceLocaleTo(new LocaleListCompat(locale));
    }

    private static String[] localePartsFrom(String localeString) {
        if (localeString == null) {
            return null;
        } else {
            String[] localeParts = localeString.split("[_\\-]");
            return localeParts.length >= 1 && localeParts.length <= 3 ? localeParts : null;
        }
    }

    private static Locale localeFromParts(String[] localeParts) {
        if (localeParts != null && localeParts.length != 0) {
            if (localeParts.length == 1) {
                return new Locale(localeParts[0]);
            } else {
                return localeParts.length == 2
                        ? new Locale(localeParts[0], localeParts[1])
                        : new Locale(localeParts[0], localeParts[1], localeParts[2]);
            }
        } else {
            return null;
        }
    }

    public static Locale localeFromString(String locale) {
        if (PSEUDOLOCALES.contains(locale)) {
            return pseudoLocaleFromString(locale);
        }

        Locale localeForTag = Locale.forLanguageTag(locale);
        if (localeForTag.toString().isEmpty()) {
            return localeFromParts(localePartsFrom(locale));
        } else {
            // Locales passed in ISO form like "en_US"
            return localeForTag;
        }
    }

    private static Locale pseudoLocaleFromString(String pseudoLocale) {
        if (pseudoLocale.equals(EN_XA)) {
            return new Locale("en", "XA");
        } else if (pseudoLocale.equals(AR_XB)) {
            return new Locale("ar", "XB");
        }
        throw new StringNotParsableAsPseudoLocale(pseudoLocale);
    }

    private LocaleUtil() {
    }

    private static class StringNotParsableAsPseudoLocale extends RuntimeException {
        public StringNotParsableAsPseudoLocale(String pseudoLocale) {
            super(pseudoLocale + " does not correspond to any pseudolocale, namely en_XA or ar_XB");
        }
    }
}