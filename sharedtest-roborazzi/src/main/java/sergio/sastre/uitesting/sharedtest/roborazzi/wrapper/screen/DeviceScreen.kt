package sergio.sastre.uitesting.sharedtest.roborazzi.wrapper.screen

data class DeviceScreen(
    val widthDp: Int,
    val heightDp: Int,
    val size: ScreenSize = ScreenSize.NORMAL,
    val aspect: ScreenAspect = ScreenAspect.NOTLONG,
    val density: ScreenDensity = ScreenDensity.MDPI,
    val defaultOrientation: ScreenOrientation = ScreenOrientation.PORTRAIT,
    val round: RoundScreen? = null,
    val type: ScreenType? = null,
){
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
        val TV_4k = DeviceScreen(
            widthDp = 960,
            heightDp = 540,
            size = ScreenSize.XLARGE,
            aspect = ScreenAspect.LONG,
            type = ScreenType.TV,
            density = ScreenDensity.XXXHDPI,
        )

        @JvmField
        val TV_1080p = DeviceScreen(
            widthDp = 960,
            heightDp = 540,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.LONG,
            type = ScreenType.TV,
            density = ScreenDensity.XHDPI,
        )

        @JvmField
        val TV_720p = DeviceScreen(
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
            widthDp = 180,
            heightDp = 180,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.NOTROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.XHDPI,
        )

        val WEAR_OS_LARGE_ROUND = DeviceScreen(
            widthDp = 227,
            heightDp = 227,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.ROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.XHDPI,
        )

        val WEAR_OS_SMALL_ROUND = DeviceScreen(
            widthDp = 192,
            heightDp = 192,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.ROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.XHDPI,
        )

        val WEAR_OS_RECTANGULAR = DeviceScreen(
            widthDp = 201,
            heightDp = 238,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.LONG,
            round = RoundScreen.NOTROUND,
            type = ScreenType.WATCH,
            density = ScreenDensity.XHDPI,
        )
    }

    object Car {
        val AUTOMOTIVE_1024p = DeviceScreen(
            widthDp = 1024,
            heightDp = 768,
            size = ScreenSize.NORMAL,
            aspect = ScreenAspect.NOTLONG,
            round = RoundScreen.NOTROUND,
            type = ScreenType.CAR,
            density = ScreenDensity.MDPI,
            defaultOrientation = ScreenOrientation.LANDSCAPE,
        )
    }
}
