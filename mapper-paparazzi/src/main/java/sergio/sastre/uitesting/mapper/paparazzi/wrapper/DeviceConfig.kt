package sergio.sastre.uitesting.mapper.paparazzi.wrapper

data class DeviceConfig(
    val screenHeight: Int = 1280,
    val screenWidth: Int = 768,
    val xdpi: Int = 320,
    val ydpi: Int = 320,
    val density: DpiDensity = Density.XHIGH,
    val ratio: ScreenRatio = ScreenRatio.NOTLONG,
    val size: ScreenSize = ScreenSize.NORMAL,
    val navigation: Navigation = Navigation.NONAV,
    val released: String = "November 13, 2012"
) {

    /**
     * The accessibilityExtension takes up half the width, therefore use this to expand it
     */
    fun increaseWidthForAccessibilityExtension() : DeviceConfig {
        return copy(
            screenWidth = this.screenWidth * 2
        )
    }

    companion object {
        @JvmField
        val NEXUS_4 = DeviceConfig()

        @JvmField
        val NEXUS_5 = DeviceConfig(
            screenHeight = 1920,
            screenWidth = 1080,
            xdpi = 445,
            ydpi = 445,
            density = Density.XXHIGH,
            ratio = ScreenRatio.NOTLONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 31, 2013"
        )

        @JvmField
        val NEXUS_7_2012 = DeviceConfig(
            screenHeight = 1280,
            screenWidth = 800,
            xdpi = 195,
            ydpi = 200,
            density = Density.TV,
            ratio = ScreenRatio.NOTLONG,
            size = ScreenSize.LARGE,
            navigation = Navigation.NONAV,
            released = "July 13, 2012"
        )

        @JvmField
        val PIXEL = DeviceConfig(
            screenHeight = 1920,
            screenWidth = 1080,
            xdpi = 440,
            ydpi = 440,
            density = Density.DPI_420,
            ratio = ScreenRatio.NOTLONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 20, 2016"
        )

        @JvmField
        val PIXEL_XL = DeviceConfig(
            screenHeight = 2560,
            screenWidth = 1440,
            xdpi = 534,
            ydpi = 534,
            density = Density.DPI_560,
            ratio = ScreenRatio.NOTLONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 20, 2016"
        )

        @JvmField
        val PIXEL_2 = DeviceConfig(
            screenHeight = 1920,
            screenWidth = 1080,
            xdpi = 442,
            ydpi = 443,
            density = Density.DPI_420,
            ratio = ScreenRatio.NOTLONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 19, 2017"
        )

        @JvmField
        val PIXEL_2_XL = DeviceConfig(
            screenHeight = 2880,
            screenWidth = 1440,
            xdpi = 537,
            ydpi = 537,
            density = Density.DPI_560,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 19, 2017"
        )

        @JvmField
        val PIXEL_3 = DeviceConfig(
            screenHeight = 2160,
            screenWidth = 1080,
            xdpi = 442,
            ydpi = 442,
            density = Density.DPI_440,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 18, 2018"
        )

        @JvmField
        val PIXEL_3_XL = DeviceConfig(
            screenHeight = 2960,
            screenWidth = 1440,
            xdpi = 522,
            ydpi = 522,
            density = Density.DPI_560,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 18, 2018"
        )

        @JvmField
        val PIXEL_3A = DeviceConfig(
            screenHeight = 2220,
            screenWidth = 1080,
            xdpi = 442,
            ydpi = 444,
            density = Density.DPI_440,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "May 7, 2019"
        )

        @JvmField
        val PIXEL_3A_XL = DeviceConfig(
            screenHeight = 2160,
            screenWidth = 1080,
            xdpi = 397,
            ydpi = 400,
            density = Density.DPI_400,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "May 7, 2019"
        )

        @JvmField
        val PIXEL_4 = DeviceConfig(
            screenHeight = 2280,
            screenWidth = 1080,
            xdpi = 444,
            ydpi = 444,
            density = Density.DPI_440,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 24, 2019"
        )

        @JvmField
        val PIXEL_4_XL = DeviceConfig(
            screenHeight = 3040,
            screenWidth = 1440,
            xdpi = 537,
            ydpi = 537,
            density = Density.DPI_560,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 24, 2019"
        )

        @JvmField
        val PIXEL_4A = DeviceConfig(
            screenHeight = 2340,
            screenWidth = 1080,
            xdpi = 442,
            ydpi = 444,
            density = Density.DPI_440,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "August 20, 2020"
        )

        @JvmField
        val PIXEL_5 = DeviceConfig(
            screenHeight = 2340,
            screenWidth = 1080,
            xdpi = 442,
            ydpi = 444,
            density = Density.DPI_440,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 15, 2020"
        )

        @JvmField
        val PIXEL_6 = DeviceConfig(
            screenHeight = 2400,
            screenWidth = 1080,
            xdpi = 406,
            ydpi = 411,
            density = Density.DPI_420,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 28, 2021"
        )

        @JvmField
        val PIXEL_6_PRO = DeviceConfig(
            screenHeight = 3120,
            screenWidth = 1440,
            xdpi = 512,
            ydpi = 512,
            density = Density.DPI_560,
            ratio = ScreenRatio.LONG,
            size = ScreenSize.NORMAL,
            navigation = Navigation.NONAV,
            released = "October 28, 2021"
        )
    }
}
