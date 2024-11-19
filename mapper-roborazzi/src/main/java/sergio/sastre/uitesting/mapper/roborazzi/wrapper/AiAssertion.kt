package sergio.sastre.uitesting.mapper.roborazzi.wrapper

data class AiAssertion(
    val assertionPrompt: String,
    val requiredFulfillmentPercent: Int,
    val failIfNotFulfilled: Boolean = true
)

