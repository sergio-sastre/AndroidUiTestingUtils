package sergio.sastre.uitesting.paparazzi.config

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Environment
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.Density
import com.android.resources.Navigation
import com.android.resources.ScreenRatio
import com.android.resources.ScreenSize
import sergio.sastre.uitesting.sharedtest.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.sharedtest.paparazzi.wrapper.RenderingMode
import sergio.sastre.uitesting.sharedtest.paparazzi.wrapper.Density as PaparazziWrapperDensity
import sergio.sastre.uitesting.sharedtest.paparazzi.wrapper.ScreenRatio as PaparazziWrapperScreenRatio
import sergio.sastre.uitesting.sharedtest.paparazzi.wrapper.ScreenSize as PaparazziWrapperScreenSize
import sergio.sastre.uitesting.sharedtest.paparazzi.wrapper.Navigation as PaparazziWrapperNavigation

internal class PaparazziSharedTestAdapter(
    private val paparazziConfig: PaparazziConfig,
) {

    private val config
        get() = paparazziConfig.deviceConfig

    fun asPaparazziDeviceConfig(): DeviceConfig =
        DeviceConfig(
            screenHeight = config.screenHeight,
            screenWidth = config.screenWidth,
            xdpi = config.xdpi,
            ydpi = config.ydpi,
            density = asPaparazziDensity(),
            ratio = asPaparazziScreenRatio(),
            size = asPaparazziScreenSize(),
            navigation = asPaparazziNavigation(),
            released = config.released,
        )

    fun asPaparazziDensity(): Density =
        when (config.density) {
            PaparazziWrapperDensity.XXXHIGH -> Density.XXXHIGH
            PaparazziWrapperDensity.DPI_560 -> Density.DPI_560
            PaparazziWrapperDensity.XXHIGH -> Density.XXHIGH
            PaparazziWrapperDensity.DPI_440 -> Density.DPI_440
            PaparazziWrapperDensity.DPI_420 -> Density.DPI_420
            PaparazziWrapperDensity.DPI_400 -> Density.DPI_400
            PaparazziWrapperDensity.DPI_360 -> Density.DPI_360
            PaparazziWrapperDensity.XHIGH -> Density.XHIGH
            PaparazziWrapperDensity.DPI_260 -> Density.DPI_260
            PaparazziWrapperDensity.DPI_280 -> Density.DPI_280
            PaparazziWrapperDensity.DPI_300 -> Density.DPI_300
            PaparazziWrapperDensity.DPI_340 -> Density.DPI_340
            PaparazziWrapperDensity.HIGH -> Density.HIGH
            PaparazziWrapperDensity.DPI_220 -> Density.DPI_220
            PaparazziWrapperDensity.TV -> Density.TV
            PaparazziWrapperDensity.DPI_200 -> Density.DPI_200
            PaparazziWrapperDensity.DPI_180 -> Density.DPI_180
            PaparazziWrapperDensity.MEDIUM -> Density.MEDIUM
            PaparazziWrapperDensity.DPI_140 -> Density.DPI_140
            PaparazziWrapperDensity.LOW -> Density.LOW
            PaparazziWrapperDensity.ANYDPI -> Density.ANYDPI
            PaparazziWrapperDensity.NODPI -> Density.NODPI
        }

    fun asPaparazziScreenRatio(): ScreenRatio =
        when (config.ratio) {
            PaparazziWrapperScreenRatio.NOTLONG -> ScreenRatio.NOTLONG
            PaparazziWrapperScreenRatio.LONG -> ScreenRatio.LONG
        }

    fun asPaparazziScreenSize(): ScreenSize =
        when (config.size) {
            PaparazziWrapperScreenSize.SMALL -> ScreenSize.SMALL
            PaparazziWrapperScreenSize.NORMAL -> ScreenSize.NORMAL
            PaparazziWrapperScreenSize.LARGE -> ScreenSize.LARGE
            PaparazziWrapperScreenSize.XLARGE -> ScreenSize.XLARGE
        }

    fun asPaparazziNavigation(): Navigation =
        when (config.navigation) {
            PaparazziWrapperNavigation.NONAV -> Navigation.NONAV
            PaparazziWrapperNavigation.DPAD -> Navigation.DPAD
            PaparazziWrapperNavigation.TRACKBALL -> Navigation.TRACKBALL
            PaparazziWrapperNavigation.WHEEL -> Navigation.WHEEL
        }

    fun asRenderingMode(): SessionParams.RenderingMode =
        when (paparazziConfig.renderingMode) {
            RenderingMode.NORMAL -> SessionParams.RenderingMode.NORMAL
            RenderingMode.V_SCROLL -> SessionParams.RenderingMode.V_SCROLL
            RenderingMode.H_SCROLL -> SessionParams.RenderingMode.H_SCROLL
            RenderingMode.FULL_EXPAND -> SessionParams.RenderingMode.FULL_EXPAND
            RenderingMode.SHRINK -> SessionParams.RenderingMode.SHRINK
            null -> SessionParams.RenderingMode.V_SCROLL
        }

    fun asEnvironment(): Environment =
        if (paparazziConfig.environment == null) {
            detectEnvironment()
        } else {
            val environment = paparazziConfig.environment!!
            Environment(
                platformDir = environment.platformDir,
                appTestDir = environment.appTestDir,
                resDir = environment.resDir,
                assetsDir = environment.assetsDir,
                packageName = environment.packageName,
                compileSdkVersion = environment.compileSdkVersion,
                resourcePackageNames = environment.resourcePackageNames,
            )
        }
}