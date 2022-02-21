/*
 * Copyright (c) 2022-2022,  Arumugam Jeganathan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.wbtwzd.logdog.command.DeviceListCmd
import com.wbtwzd.logdog.ui.App
import com.wbtwzd.logdog.util.Log
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val log = Log("LogDog")
    val appOptions = AppOptions()

    // Parse the command line parameters
    val argList = args.asList().toMutableList()
    while (argList.isNotEmpty()) {
        when (val param = argList.removeFirst()) {
            "--debug", "-d" -> Log.logLevel = "debug"
            "--info" -> Log.logLevel = "info"
            "--warn", "-w" -> Log.logLevel = "error"
            "--output" -> {
                val dir = argList.removeFirstOrNull()
                dir?.let {
                    val outputDir = File(it)
                    if (outputDir.isDirectory) {
                        appOptions.outputDir = outputDir
                    } else {
                        log.e("Output directory does not exist")
                        printUsage()
                    }
                } ?: run {
                    log.e("Missing output location")
                    printUsage()
                }
            }
            else -> {
                log.e("Unknown option $param")
                printUsage()
            }
        }
    }

    // Check for `adb` installation location.
    System.getenv("PATH")?.let { path ->
        val adbFile = path.split(File.pathSeparator)
            .flatMap { File(it).listFiles()?.toList() ?: emptyList<File>() }
            .firstOrNull { it.name == "adb" }
        adbFile?.let { appOptions.adb = it.absolutePath }
    }

    log.i(appOptions)

    runBlocking {
        val devices = appOptions.adb?.let { DeviceListCmd(it).execute() }
        log.i(devices ?: "No devices")
    }
//    startApp()
}

private fun printUsage() {
    println(
        """
        Options:
            --debug, -d     - Enable logging debug messages
            --warn, -w      - Log only warning messages
            --output        - Output directory to write the log output to
    """.trimIndent()
    )
    exitProcess(-1)
}

private fun startApp() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}


data class AppOptions(
    var outputDir: File? = null,
    var adb: String? = null
)