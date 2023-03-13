package sergio.sastre.uitesting.sharedtest.paparazzi.wrapper

data class Environment(
    val platformDir: String,
    val appTestDir: String,
    val resDir: String,
    val assetsDir: String,
    val packageName: String,
    val compileSdkVersion: Int,
    val resourcePackageNames: List<String>
)
