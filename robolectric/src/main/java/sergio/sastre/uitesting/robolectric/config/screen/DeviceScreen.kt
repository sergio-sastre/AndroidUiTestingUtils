package sergio.sastre.uitesting.robolectric.config.screen

/**
 * DeviceScreen Robolectric configurations taken from:
 * https://github.com/takahirom/roborazzi/blob/aa840cb0078d69dbcca5f02e339637e741ed54f5/roborazzi/src/main/java/com/github/takahirom/roborazzi/RobolectricDeviceQualifiers.kt
 */
data class DeviceScreen(
    val widthDp: Int,
    val heightDp: Int,
    val size: ScreenSize = ScreenSize.NORMAL,
    val aspect: ScreenAspect = ScreenAspect.NOTLONG,
    val density: DpiDensity = ScreenDensity.MDPI,
    val defaultOrientation: ScreenOrientation = ScreenOrientation.PORTRAIT,
    val round: RoundScreen? = null,
    val type: ScreenType? = null,
    val name: String = ""
) {

    object Phone {
        @JvmField
        val NEXUS_ONE = DeviceScreen(
            widthDp = 320,
            heightDp = 533,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.HDPI,
            name = "NEXUS_ONE"
        )

        @JvmField
        val NEXUS_4 = DeviceScreen(
            widthDp = 384,
            heightDp = 640,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.XHDPI,
            name = "NEXUS_4"
        )

        @JvmField
        val NEXUS_7 = DeviceScreen(
            widthDp = 600,
            heightDp = 960,
            size = ScreenSize.LARGE,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.XHDPI,
            name = "NEXUS_7"
        )

        @JvmField
        val NEXUS_9 = DeviceScreen(
            widthDp = 1024,
            heightDp = 768,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.XHDPI,
            name = "NEXUS_9"
        )

        @JvmField
        val PIXEL_C = DeviceScreen(
            widthDp = 1280,
            heightDp = 900,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.XHDPI,
            name = "PIXEL_C"
        )

        @JvmField
        val PIXEL_XL = DeviceScreen(
            widthDp = 411,
            heightDp = 731,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.DPI_560,
            name = "PIXEL_XL"
        )

        @JvmField
        val PIXEL_4 = DeviceScreen(
            widthDp = 393,
            heightDp = 829,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.DPI_440,
            name = "PIXEL_4"
        )

        @JvmField
        val PIXEL_4_XL = DeviceScreen(
            widthDp = 411,
            heightDp = 869,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.DPI_560,
            name = "PIXEL_4_XL"
        )

        @JvmField
        val PIXEL_4A = DeviceScreen(
            widthDp = 393,
            heightDp = 851,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.DPI_440,
            name = "PIXEL_4A"
        )

        @JvmField
        val PIXEL_5 = DeviceScreen(
            widthDp = 393,
            heightDp = 851,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.DPI_440,
            name = "PIXEL_5"
        )

        @JvmField
        val SMALL_PHONE = DeviceScreen(
            widthDp = 360,
            heightDp = 640,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.XHDPI,
            name = "SMALL_PHONE"
        )

        @JvmField
        val MEDIUM_PHONE = DeviceScreen(
            widthDp = 411,
            heightDp = 914,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.DPI_420,
            name = "MEDIUM_PHONE"
        )
    }

    object Tablet {
        @JvmField
        val MEDIUM_TABLET = DeviceScreen(
            widthDp = 1280,
            heightDp = 800,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.XHDPI,
            name = "MEDIUM_TABLET"
        )
    }

    object Desktop {
        @JvmField
        val SMALL_DESKTOP = DeviceScreen(
            widthDp = 1366,
            heightDp = 768,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.MDPI,
            name = "SMALL_DESKTOP"
        )

        @JvmField
        val MEDIUM_DESKTOP = DeviceScreen(
            widthDp = 1920,
            heightDp = 1080,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.XHDPI,
            name = "MEDIUM_DESKTOP"
        )

        @JvmField
        val LARGE_DESKTOP = DeviceScreen(
            widthDp = 1920,
            heightDp = 1080,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.MDPI,
            name = "LARGE_DESKTOP"
        )
    }

    object Television {
        @JvmField
        val ANDROID_TV_4k = DeviceScreen(
            widthDp = 960,
            heightDp = 540,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.LONG,
            type = ScreenType.TV,
            density = ScreenDensity.XXXHDPI,
            name = "ANDROID_TV_4k"
        )

        @JvmField
        val ANDROID_TV_1080p = DeviceScreen(
            widthDp = 960,
            heightDp = 540,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            type = ScreenType.TV,
            density = ScreenDensity.XHDPI,
            name = "ANDROID_TV_1080p"
        )

        @JvmField
        val ANDROID_TV_720p = DeviceScreen(
            widthDp = 962,
            heightDp = 541,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            type = ScreenType.TV,
            density = ScreenDensity.TVDPI,
            name = "ANDROID_TV_720p"
        )
    }

    object Watch {
        val WEAR_OS_SQUARE = DeviceScreen(
            widthDp = 180,
            heightDp = 180,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.NOTROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.XHDPI,
            name = "WEAR_OS_SQUARE"
        )

        val WEAR_OS_LARGE_ROUND = DeviceScreen(
            widthDp = 227,
            heightDp = 227,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.ROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.XHDPI,
            name = "WEAR_OS_LARGE_ROUND"
        )

        val WEAR_OS_SMALL_ROUND = DeviceScreen(
            widthDp = 192,
            heightDp = 192,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.ROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.XHDPI,
            name = "WEAR_OS_SMALL_ROUND"
        )

        val WEAR_OS_RECTANGULAR = DeviceScreen(
            widthDp = 201,
            heightDp = 238,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.NOTROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.XHDPI,
            name = "WEAR_OS_RECTANGULAR"
        )
    }

    object Car {
        val ANDROID_AUTO_1024p = DeviceScreen(
            widthDp = 1024,
            heightDp = 768,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.NOTLONG,
            round = RoundScreen.NOTROUND,
            type = ScreenType.CAR,
            density = ScreenDensity.MDPI,
            name = "ANDROID_AUTO_1024p"
        )
    }
}
