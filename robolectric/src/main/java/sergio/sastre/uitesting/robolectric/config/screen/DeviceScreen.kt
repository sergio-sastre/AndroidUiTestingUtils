package sergio.sastre.uitesting.robolectric.config.screen

import sergio.sastre.uitesting.utils.common.Orientation

data class DeviceScreen(
    val widthDp: Int,
    val heightDp: Int,
    val size: ScreenSize = ScreenSize.NORMAL,
    val aspect: ScreenAspect = ScreenAspect.NOTLONG,
    val density: ScreenDensity = ScreenDensity.MDPI,
    val defaultOrientation: ScreenOrientation = ScreenOrientation.PORTRAIT,
    val round: RoundScreen? = null,
    val type: ScreenType? = null,
) {

    object Phone {
        @JvmField
        val NEXUS_ONE = DeviceScreen(
            widthDp = 320,
            heightDp = 533,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.HDPI,
        )

        @JvmField
        val NEXUS_4 = DeviceScreen(
            widthDp = 384,
            heightDp = 640,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.XHDPI,
        )

        @JvmField
        val NEXUS_7 = DeviceScreen(
            widthDp = 600,
            heightDp = 960,
            size = ScreenSize.LARGE,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.XHDPI,
        )

        @JvmField
        val NEXUS_9 = DeviceScreen(
            widthDp = 1024,
            heightDp = 768,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.XHDPI,
        )

        @JvmField
        val PIXEL_C = DeviceScreen(
            widthDp = 1280,
            heightDp = 900,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.XHDPI,
        )

        @JvmField
        val PIXEL_XL = DeviceScreen(
            widthDp = 411,
            heightDp = 731,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.DPI_560,
        )

        @JvmField
        val PIXEL_4 = DeviceScreen(
            widthDp = 393,
            heightDp = 829,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.DPI_440,
        )

        @JvmField
        val PIXEL_4_XL = DeviceScreen(
            widthDp = 411,
            heightDp = 869,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.DPI_560,
        )

        @JvmField
        val PIXEL_4A = DeviceScreen(
            widthDp = 393,
            heightDp = 851,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.DPI_440,
        )

        @JvmField
        val PIXEL_5 = DeviceScreen(
            widthDp = 393,
            heightDp = 851,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.LONG,
            density = ScreenDensity.DPI_440,
        )
    }

    object Television {
        @JvmField
        val ANDROID_TV_1080p = DeviceScreen(
            widthDp = 960,
            heightDp = 540,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            type = ScreenType.TV,
            density = ScreenDensity.XHDPI,
        )

        @JvmField
        val ANDROID_TV_720p = DeviceScreen(
            widthDp = 962,
            heightDp = 541,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            type = ScreenType.TV,
            density = ScreenDensity.TVDPI,
        )
    }

    object Watch {
        val WEAR_OS_SQUARE = DeviceScreen(
            widthDp = 187,
            heightDp = 187,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.ROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.HDPI,
        )

        val WEAR_OS_LARGE_ROUND = DeviceScreen(
            widthDp = 213,
            heightDp = 213,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.ROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.HDPI,
        )

        val WEAR_OS_SMALL_ROUND = DeviceScreen(
            widthDp = 240,
            heightDp = 218,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.NOTLONG,
            round = RoundScreen.NOTROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.TVDPI,
        )
    }
}
