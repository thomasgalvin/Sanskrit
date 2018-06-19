package sanskrit.ui

import java.awt.Desktop
import java.awt.event.ActionEvent
import java.io.File
import java.io.IOException
import java.util.Enumeration
import java.util.Properties

object SystemUtils {
    val IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac")
    val IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows")
    val PREFERED_MODIFIER_KEY = if (IS_MAC) ActionEvent.META_MASK else ActionEvent.CTRL_MASK
    val SECONDARY_MODIFIER_KEY = if (IS_MAC) ActionEvent.CTRL_MASK else ActionEvent.ALT_MASK
    val QUIT_STRING = if (IS_MAC) "Quit" else "Exit"

    val userHome: File
        get() {
            val result = System.getProperty("user.home")
            return File(result)
        }

    val randomTempDir: File
        get() {
            val tmpFile = File(System.getProperty("java.io.tmpdir"), java.util.UUID.randomUUID().toString())
            tmpFile.mkdirs()
            return tmpFile
        }

    val tempFile: File
        get() = getTempFile(java.util.UUID.randomUUID().toString())

    fun getPreferencesDirectory(programName: String,
                                version: String): File {
        val preferencesDirectory = getPreferencesDirectory(programName)
        val result = File(preferencesDirectory, version)
        result.mkdirs()
        return result
    }

    fun getPreferencesDirectory(programName: String): File {
        var result = System.getProperty("user.home")
        result += File.separatorChar

        if (IS_MAC) {
            result += "Library" + File.separatorChar + programName + File.separatorChar
        } else if (IS_WINDOWS) {
            result += "Application Data" + File.separatorChar + programName + File.separatorChar
        } else {
            //assuming a unix variant
            result += "." + programName + File.separatorChar
        }

        val resultFile = File(result)
        resultFile.mkdirs()
        return resultFile
    }

    fun getSystemPreferencesDirectory(programName: String,
                                      version: String): File {
        val preferencesDirectory = getSystemPreferencesDirectory(programName)
        return File(preferencesDirectory, version)
    }

    fun getSystemPreferencesDirectory(programName: String): File {
        var result = ""

        if (IS_MAC) {
            result += "/Library/$programName"
        } else if (IS_WINDOWS) {
            result = "C:\\Program Files\\$programName"
        } else {
            result = "/etc/$programName"
        }

        result += File.separatorChar

        return File(result)
    }

    fun getTempDir(name: String): File {
        var result = System.getProperty("java.io.tmpdir")
        result += File.separatorChar
        result += name
        result += File.separatorChar
        return File(result)
    }

    fun getTempDir(programName: String, programVersion: String): File {
        var result = getTempDir(programName)
        result = File(result, programVersion)
        return result
    }

    fun getRandomTempDir(dirName: String): File {
        var tmpFile = File(System.getProperty("java.io.tmpdir"), java.util.UUID.randomUUID().toString())
        tmpFile = File(tmpFile, dirName)
        tmpFile.mkdirs()
        return tmpFile
    }

    fun getTempFileWithExtension(extension: String): File {
        return getTempFile(java.util.UUID.randomUUID().toString() + extension)
    }

    fun getTempFile(fileName: String): File {
        var tmpFile = File(System.getProperty("java.io.tmpdir"), java.util.UUID.randomUUID().toString())
        tmpFile.mkdirs()
        tmpFile = File(tmpFile, java.util.UUID.randomUUID().toString() + fileName)
        return tmpFile
    }

    fun displaySystemProperties() {
        val properties = System.getProperties()
        val names = properties.propertyNames()
        while (names.hasMoreElements()) {
            val name = names.nextElement().toString()
            val value = properties.getProperty(name)

            println("$name: $value")
        }

    }

    fun printMemory() {
        println("Available processors (cores): " + Runtime.getRuntime().availableProcessors())

        val free = Runtime.getRuntime().freeMemory() / 1048576
        println("Free memory (megabytes): $free")

        val maxMemory = Runtime.getRuntime().maxMemory() / 1048576
        /* Maximum amount of memory the JVM will attempt to use */
        println("Maximum memory (megabytes): " + if (maxMemory == java.lang.Long.MAX_VALUE) "no limit" else maxMemory)

        val total = Runtime.getRuntime().totalMemory() / 1048576
        println("Total memory available to JVM (megabytes): $total")
    }

    @Throws(IOException::class)
    fun selectFileInBrowser(file: File) {
        if (SystemUtils.IS_WINDOWS) {
            val command = "explorer.exe /select,\"" + file.absolutePath + "\""
            Runtime.getRuntime().exec(command)
        } else if (SystemUtils.IS_MAC) {
            val args = arrayOf("open", "-R", file.absolutePath)

            val p = Runtime.getRuntime().exec(args)

            try {
                p.waitFor()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        } else {
            val parent = file.parentFile
            if (parent != null) {
                Desktop.getDesktop().open(file)
            }
        }
    }

}
