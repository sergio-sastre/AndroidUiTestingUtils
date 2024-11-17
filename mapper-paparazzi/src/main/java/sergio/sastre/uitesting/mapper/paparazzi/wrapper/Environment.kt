package sergio.sastre.uitesting.mapper.paparazzi.wrapper

import java.util.*

data class Environment(
    val compileSdkVersion: Int? = null,
    val appTestDir: String? = null,
    val packageName: String? = null,
    val resourcePackageNames: List<String>? = null,
    val localResourceDirs: List<String>? = null,
    val moduleResourceDirs : List<String>? = null,
    val libraryResourceDirs : List<String>? = null,
    val allModuleAssetDirs : List<String>? = null,
    val libraryAssetDirs : List<String>? = null,
)

@Suppress("unused")
fun androidHome() = System.getenv("ANDROID_SDK_ROOT")
    ?: System.getenv("ANDROID_HOME")
    ?: androidSdkPath()

private fun androidSdkPath(): String {
    val osName = System.getProperty("os.name").lowercase(Locale.US)
    val sdkPathDir = if (osName.startsWith("windows")) {
        "\\AppData\\Local\\Android\\Sdk"
    } else if (osName.startsWith("mac")) {
        "/Library/Android/sdk"
    } else {
        "/Android/Sdk"
    }
    val homeDir = System.getProperty("user.home")
    return homeDir + sdkPathDir
}
