package sergio.sastre.uitesting.roborazzi.config

import com.dropbox.differ.ImageComparator
import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.AiAssertionOptions
import com.github.takahirom.roborazzi.Dump
import com.github.takahirom.roborazzi.Dump.Companion.AccessibilityExplanation
import com.github.takahirom.roborazzi.Dump.Companion.DefaultExplanation
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.JvmImageIoFormat
import com.github.takahirom.roborazzi.LosslessWebPImageIoFormat
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.robolectric.config.screen.RoundScreen
import sergio.sastre.uitesting.robolectric.config.screen.ScreenAspect
import sergio.sastre.uitesting.robolectric.config.screen.ScreenDensity
import sergio.sastre.uitesting.robolectric.config.screen.ScreenOrientation
import sergio.sastre.uitesting.robolectric.config.screen.ScreenSize
import sergio.sastre.uitesting.robolectric.config.screen.ScreenType
import sergio.sastre.uitesting.mapper.roborazzi.RoborazziConfig
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.AiAssertion
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.ComparisonStyle
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.DumpExplanation
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.ImageIoFormat.*
import sergio.sastre.uitesting.robolectric.config.screen.DpiDensity
import sergio.sastre.uitesting.robolectric.config.screen.DpiDensity.*
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.CaptureType as WrapperCaptureType
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.screen.RoundScreen as WrapperRoundScreen
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.screen.ScreenDensity as WrapperDensity
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.screen.ScreenSize as WrapperScreenSize
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.screen.ScreenType as WrapperScreenType
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.screen.ScreenAspect as WrapperScreenAspect
import sergio.sastre.uitesting.mapper.roborazzi.wrapper.screen.ScreenOrientation as WrapperScreenOrientation

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

    @ExperimentalRoborazziApi
    fun asRoborazziOptions(): RoborazziOptions {
        roborazziConfig.roborazziOptions.let {
            val adaptedCaptureType: RoborazziOptions.CaptureType =
                when (it.captureType) {
                    is WrapperCaptureType.Dump -> {
                        val dumpExplanation =
                            when ((it.captureType as WrapperCaptureType.Dump).explanation) {
                                DumpExplanation.AccessibilityExplanation -> AccessibilityExplanation
                                DumpExplanation.DefaultExplanation -> DefaultExplanation
                            }
                        RoborazziOptions.CaptureType.Dump(explanation = dumpExplanation)
                    }

                    is WrapperCaptureType.Screenshot -> RoborazziOptions.CaptureType.Screenshot()
                }

            val adaptedCompareOptions: RoborazziOptions.CompareOptions =
                RoborazziOptions.CompareOptions(
                    outputDirectoryPath = it.compareOptions.outputDirectoryPath,
                    resultValidator = ThresholdValidator(it.compareOptions.changeThreshold),
                    comparisonStyle = asComparisonStyle(),
                    imageComparator = asImageComparator(),
                )

            val adaptedRecordOptions: RoborazziOptions.RecordOptions =
                RoborazziOptions.RecordOptions(
                    resizeScale = it.recordOptions.resizeScale,
                    applyDeviceCrop = it.recordOptions.applyDeviceCrop,
                    imageIoFormat = when (it.recordOptions.imageIoFormat) {
                        ImageIoFormat -> JvmImageIoFormat()
                        LosslessWebPImageIoFormat -> LosslessWebPImageIoFormat()
                    }
                )

            return RoborazziOptions(
                captureType = adaptedCaptureType,
                compareOptions = adaptedCompareOptions,
                contextData = it.contextData,
                recordOptions = adaptedRecordOptions,
            ).addedAiAssertions(it.aiAssertions)
        }
    }

    @OptIn(ExperimentalRoborazziApi::class)
    private fun RoborazziOptions.addedAiAssertions(
        aiAssertions: List<AiAssertion>
    ) : RoborazziOptions =
        if (aiAssertions.isEmpty()) {
            this
        } else {
            val addedAiAssertions = aiAssertions.map { aiAssertion ->
                AiAssertionOptions.AiAssertion(
                    assertionPrompt = aiAssertion.assertionPrompt,
                    requiredFulfillmentPercent = aiAssertion.requiredFulfillmentPercent,
                    failIfNotFulfilled = true
                )
            }.toTypedArray()
            addedAiAssertions(*addedAiAssertions)
        }

    @ExperimentalRoborazziApi
    private fun asComparisonStyle(): RoborazziOptions.CompareOptions.ComparisonStyle =
        when (
            val comparisonStyle = roborazziConfig.roborazziOptions.compareOptions.comparisonStyle
        ) {
            is ComparisonStyle.Grid -> RoborazziOptions.CompareOptions.ComparisonStyle.Grid(
                bigLineSpaceDp = comparisonStyle.bigLineSpaceDp,
                smallLineSpaceDp = comparisonStyle.smallLineSpaceDp,
                hasLabel = comparisonStyle.hasLabel,
            )

            ComparisonStyle.Simple -> RoborazziOptions.CompareOptions.ComparisonStyle.Simple
        }

    private fun asImageComparator(): ImageComparator {
        val imageComparator = roborazziConfig.roborazziOptions.compareOptions.simpleImageComparator
        return SimpleImageComparator(
            maxDistance = imageComparator.maxDistance,
            hShift = imageComparator.hShift,
            vShift = imageComparator.vShift,
        )
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

    private fun asDensity(): DpiDensity =
        config?.let {
            when (it.density) {
                WrapperDensity.XXXHDPI -> ScreenDensity.XXXHDPI
                WrapperDensity.DPI_560 -> ScreenDensity.DPI_560
                WrapperDensity.XXHDPI -> ScreenDensity.XXHDPI
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
                else -> Value(it.density.dpi)
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
