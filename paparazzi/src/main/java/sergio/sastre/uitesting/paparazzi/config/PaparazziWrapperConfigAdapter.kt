package sergio.sastre.uitesting.paparazzi.config

import android.view.View
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Environment
import app.cash.paparazzi.RenderExtension
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.Density
import com.android.resources.Navigation
import com.android.resources.ScreenRatio
import com.android.resources.ScreenSize
import sergio.sastre.uitesting.mapper.paparazzi.PaparazziConfig
import sergio.sastre.uitesting.mapper.paparazzi.wrapper.RenderingMode as PaparazziWrapperRenderingMode
import sergio.sastre.uitesting.mapper.paparazzi.wrapper.Density as PaparazziWrapperDensity
import sergio.sastre.uitesting.mapper.paparazzi.wrapper.ScreenRatio as PaparazziWrapperScreenRatio
import sergio.sastre.uitesting.mapper.paparazzi.wrapper.ScreenSize as PaparazziWrapperScreenSize
import sergio.sastre.uitesting.mapper.paparazzi.wrapper.Navigation as PaparazziWrapperNavigation

internal class PaparazziWrapperConfigAdapter(
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
            PaparazziWrapperRenderingMode.NORMAL -> SessionParams.RenderingMode.NORMAL
            PaparazziWrapperRenderingMode.V_SCROLL -> SessionParams.RenderingMode.V_SCROLL
            PaparazziWrapperRenderingMode.H_SCROLL -> SessionParams.RenderingMode.H_SCROLL
            PaparazziWrapperRenderingMode.FULL_EXPAND -> SessionParams.RenderingMode.FULL_EXPAND
            PaparazziWrapperRenderingMode.SHRINK -> SessionParams.RenderingMode.SHRINK
            null -> SessionParams.RenderingMode.V_SCROLL
        }

    fun asEnvironment(): Environment {
        val environment = detectEnvironment()
        val configEnvironment = paparazziConfig.environment
        return environment.copy(
            platformDir = configEnvironment?.platformDir ?: environment.platformDir,
            compileSdkVersion = configEnvironment?.compileSdkVersion
                ?: environment.compileSdkVersion,
            appTestDir = configEnvironment?.appTestDir ?: environment.appTestDir,
            packageName = configEnvironment?.packageName ?: environment.packageName,
            resourcePackageNames = configEnvironment?.resourcePackageNames
                ?: environment.resourcePackageNames,
            localResourceDirs = configEnvironment?.localResourceDirs
                ?: environment.localResourceDirs,
            moduleResourceDirs = configEnvironment?.moduleResourceDirs
                ?: environment.moduleResourceDirs,
            libraryResourceDirs = configEnvironment?.libraryResourceDirs
                ?: environment.libraryResourceDirs,
            allModuleAssetDirs = configEnvironment?.allModuleAssetDirs
                ?: environment.allModuleAssetDirs,
            libraryAssetDirs = configEnvironment?.libraryAssetDirs
                ?: environment.libraryAssetDirs,
        )
    }

    fun asRenderExtensions(): Set<RenderExtension> =
        paparazziConfig.renderExtensions.map {
            PaparazziRenderExtension(it)
        }.toSet()

    private class PaparazziRenderExtension(
        private val wrapperRenderExtension: sergio.sastre.uitesting.mapper.paparazzi.wrapper.RenderExtension,
    ) : RenderExtension {
        override fun renderView(contentView: View): View =
            wrapperRenderExtension.renderView(contentView)
    }
}
