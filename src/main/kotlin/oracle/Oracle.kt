package oracle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import kotlin.time.measureTimedValue

class Oracle(
    private val simulator: Simulator,
    private val expectedOutput: String,
    private val timeout: Long
) {
    private val monkeyDoCommand = "/home/milosz/.Garmin/ConnectIQ/Sdks/connectiq-sdk-lin-6.3.1-2023-09-13-47b193194/bin/monkeydo"

    private val expectedErrors = listOf("Unable to parser the app's UUID from the PRG.")

    suspend fun check(prgBytes: ByteArray): Boolean {
        val timedValue = measureTimedValue {
            val appFile = prepareApp(prgBytes)
            println("----------")
            println("Testing app '$appFile'...")

            val result = testApp(appFile)
            if(result) {
                appFile.delete()
                println("Finished testing with success!")
            } else {
                println("[!] App Failed [!]")
            }
            result
        }
        println("Elapsed: ${timedValue.duration}")
        return timedValue.value
    }

    private suspend fun testApp(appFile: File): Boolean = withContext(Dispatchers.IO) {
        val processBuilder = ProcessBuilder()
        processBuilder.command(monkeyDoCommand, appFile.absolutePath, "vivoactive4")

        val process = processBuilder.start()
        val reader = process.inputReader()
        val errorReader = process.errorReader()
        val result: Boolean = withTimeoutOrNull(timeout) {
            var lastErrorLine = ""
            var foundExpectedOutputLine = false
            while(process.isAlive) {
                delay(100)
                while(simulator.ready()) {
                    val line = simulator.readLine()
                    println("  >SIM $line")
                    val expectedLine = "Error: 'Signature check failed on file: ${appFile.name.uppercase().dropLast(4)}'"
                    if(line.endsWith(expectedLine)) {
                        println("  Found expected simulator error line")
                        return@withTimeoutOrNull true
                    }
                }
                if (foundExpectedOutputLine) {
                    println("  Found expected output line")
                    return@withTimeoutOrNull true
                }
                while (reader.ready()) {
                    val line = reader.readLine()
                    println("  >I $line")
                    if (line == expectedOutput) {
                        foundExpectedOutputLine = true
                    }
                }
                while (errorReader.ready()) {
                    lastErrorLine = errorReader.readLine()
                    println("  >E $lastErrorLine")

                }
            }
            println("   EXIT CODE: ${process.exitValue()}")
            if (expectedErrors.contains(lastErrorLine)) {
                println("  Found expected error line")
                return@withTimeoutOrNull true
            }
            false
        } ?: false
        return@withContext result
    }

    private fun prepareApp(prgBytes: ByteArray): File {
        val file = File("assets/fuzzing/app_${System.currentTimeMillis()}.prg")
        file.writeBytes(prgBytes)
        return file
    }

}