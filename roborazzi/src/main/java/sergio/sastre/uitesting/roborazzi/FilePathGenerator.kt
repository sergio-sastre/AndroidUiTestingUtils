package sergio.sastre.uitesting.roborazzi

import com.github.takahirom.roborazzi.DefaultFileNameGenerator
import com.github.takahirom.roborazzi.InternalRoborazziApi
import java.io.File

internal class FilePathGenerator {

    operator fun invoke(parent: String, fileName: String?): String {
        val fileNameWithExtension =
            when (fileName != null) {
                true -> "$fileName.png"
                false -> generateFilePath()
            }

        return File(parent, fileNameWithExtension).path
    }

    @OptIn(InternalRoborazziApi::class)
    private fun generateFilePath(): String {
        val defaultFileName = DefaultFileNameGenerator.generateFilePath("png")
        return defaultFileName.split("/").lastOrNull() ?: defaultFileName
    }
}
