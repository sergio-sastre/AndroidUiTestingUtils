package sergio.sastre.uitesting.roborazzi.config

import com.github.takahirom.roborazzi.RoborazziOptions
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.robolectric.config.screen.RoundScreen
import sergio.sastre.uitesting.robolectric.config.screen.ScreenAspect
import sergio.sastre.uitesting.robolectric.config.screen.ScreenDensity
import sergio.sastre.uitesting.robolectric.config.screen.ScreenOrientation
import sergio.sastre.uitesting.robolectric.config.screen.ScreenSize
import sergio.sastre.uitesting.robolectric.config.screen.ScreenType
import sergio.sastre.uitesting.sharedtest.roborazzi.RoborazziConfig
import sergio.sastre.uitesting.sharedtest.roborazzi.wrapper.CaptureType as WrapperCaptureType
import sergio.sastre.uitesting.sharedtest.roborazzi.wrapper.screen.RoundScreen as WrapperRoundScreen
import sergio.sastre.uitesting.sharedtest.roborazzi.wrapper.screen.ScreenDensity as WrapperDensity
import sergio.sastre.uitesting.sharedtest.roborazzi.wrapper.screen.ScreenSize as WrapperScreenSize
import sergio.sastre.uitesting.sharedtest.roborazzi.wrapper.screen.ScreenType as WrapperScreenType
import sergio.sastre.uitesting.sharedtest.roborazzi.wrapper.screen.ScreenAspect as WrapperScreenAspect
import sergio.sastre.uitesting.sharedtest.roborazzi.wrapper.screen.ScreenOrientation as WrapperScreenOrientation

internal class RoborazziSharedTestAdapter(
    private val roborazziConfig: RoborazziConfig,
) {

    private val config
        get() = roborazziConfig.deviceScreen

    fun asDeviceScreen(): DeviceScreen? =
        config?.let {
            DeviceScreen(
                heightDp = it.heightDp,
                widthDp = it.widthDp,
                density = asDensity(),
                size = asSize(),
                round = asRoundScreen(),
                type = asType(),
                aspect = asAspect(),
                defaultOrientation = asDefaultOrientation(),
            )
        }

    fun asRoborazziOptions(): RoborazziOptions {
        roborazziConfig.roborazziOptions.let {
            val adaptedCaptureType: RoborazziOptions.CaptureType =
                when (it.captureType) {
                    WrapperCaptureType.Dump -> RoborazziOptions.CaptureType.Dump()
                    WrapperCaptureType.Screenshot -> RoborazziOptions.CaptureType.Screenshot()
                }
            val adaptedCompareOptions: RoborazziOptions.CompareOptions =
                RoborazziOptions.CompareOptions(
                    changeThreshold = it.compareChangeThreshold
                )
            return RoborazziOptions(
                captureType = adaptedCaptureType,
                compareOptions = adaptedCompareOptions
            )
        }
    }

    private fun asDefaultOrientation(): ScreenOrientation =
        config?.let {
            when (it.defaultOrientation) {
                WrapperScreenOrientation.PORTRAIT -> ScreenOrientation.PORTRAIT
                WrapperScreenOrientation.LANDSCAPE -> ScreenOrientation.LANDSCAPE
            }
        } ?: ScreenOrientation.PORTRAIT

    private fun asAspect(): ScreenAspect =
        config?.let {
            when (it.aspect) {
                WrapperScreenAspect.LONG -> ScreenAspect.LONG
                WrapperScreenAspect.NOTLONG -> ScreenAspect.NOTLONG
            }
        } ?: ScreenAspect.NOTLONG

    private fun asType(): ScreenType? =
        config?.let {
            when (it.type) {
                WrapperScreenType.WATCH -> ScreenType.WATCH
                WrapperScreenType.TV -> ScreenType.TV
                WrapperScreenType.CAR -> ScreenType.CAR
                null -> null
            }
        }

    private fun asSize(): ScreenSize =
        config?.let {
            when (it.size) {
                WrapperScreenSize.SMALL -> ScreenSize.SMALL
                WrapperScreenSize.NORMAL -> ScreenSize.NORMAL
                WrapperScreenSize.LARGE -> ScreenSize.LARGE
                WrapperScreenSize.XLARGE -> ScreenSize.XLARGE
            }
        } ?: ScreenSize.NORMAL

    private fun asDensity(): ScreenDensity =
        config?.let {
            when (it.density) {
                WrapperDensity.XXXHDPI -> ScreenDensity.XXXHDPI
                WrapperDensity.DPI_560 -> ScreenDensity.DPI_560
                WrapperDensity.XXHDPI -> ScreenDensity.XXXHDPI
                WrapperDensity.DPI_440 -> ScreenDensity.DPI_440
                WrapperDensity.DPI_420 -> ScreenDensity.DPI_420
                WrapperDensity.DPI_400 -> ScreenDensity.DPI_400
                WrapperDensity.DPI_360 -> ScreenDensity.DPI_360
                WrapperDensity.XHDPI -> ScreenDensity.XHDPI
                WrapperDensity.DPI_260 -> ScreenDensity.DPI_260
                WrapperDensity.DPI_280 -> ScreenDensity.DPI_280
                WrapperDensity.DPI_300 -> ScreenDensity.DPI_300
                WrapperDensity.DPI_340 -> ScreenDensity.DPI_340
                WrapperDensity.HDPI -> ScreenDensity.HDPI
                WrapperDensity.DPI_220 -> ScreenDensity.DPI_220
                WrapperDensity.TVDPI -> ScreenDensity.TVDPI
                WrapperDensity.DPI_200 -> ScreenDensity.DPI_200
                WrapperDensity.DPI_180 -> ScreenDensity.DPI_180
                WrapperDensity.MDPI -> ScreenDensity.MDPI
                WrapperDensity.DPI_140 -> ScreenDensity.DPI_140
                WrapperDensity.LDPI -> ScreenDensity.LDPI
                WrapperDensity.ANYDPI -> ScreenDensity.ANYDPI
                WrapperDensity.NODPI -> ScreenDensity.NODPI
            }
        } ?: ScreenDensity.MDPI

    private fun asRoundScreen(): RoundScreen =
        config?.let {
            when (it.round) {
                WrapperRoundScreen.ROUND -> RoundScreen.ROUND
                WrapperRoundScreen.NOTROUND -> RoundScreen.NOTROUND
                null -> RoundScreen.NOTROUND
            }
        } ?: RoundScreen.NOTROUND
}