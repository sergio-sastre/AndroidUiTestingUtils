package sergio.sastre.uitesting.mapper.roborazzi.wrapper

/**
 * WARNING:
 * @param aiAssertions requires you to add the corresponding Roborazzi module, namely one of these:
 * 1. roborazzi-ai-gemini -> testImplementation(io.github.takahirom.roborazzi:roborazzi-ai-gemini)
 * 2. roborazzi-ai-openai -> testImplementation(io.github.takahirom.roborazzi:roborazzi-ai-openai)
 */
data class RoborazziOptions(
    val captureType: CaptureType = CaptureType.Screenshot,
    val compareOptions: CompareOptions = CompareOptions(),
    val recordOptions: RecordOptions = RecordOptions(),
    val contextData: Map<String, Any> = emptyMap(),
    val aiAssertions: List<AiAssertion> = emptyList()
)
