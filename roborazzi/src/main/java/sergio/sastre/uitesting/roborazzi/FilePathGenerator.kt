package sergio.sastre.uitesting.roborazzi

import java.io.File

class FilePathGenerator{
    operator fun invoke(parent: String, fileName: String?): String {
        val childFileName = fileName ?: randomName()
        val file = File(parent, "$childFileName.png")
        return file.path
    }

    private fun randomName(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return List(8) { allowedChars.random() }.joinToString("")
    }
}
