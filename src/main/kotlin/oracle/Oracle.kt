package oracle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class Oracle(
    private val expectedOutput: String,
    private val timeout: Long
) {
    private val monkeyDoCommand = "/home/milosz/.Garmin/ConnectIQ/Sdks/connectiq-sdk-lin-6.3.1-2023-09-13-47b193194/bin/monkeydo"
    suspend fun check(prgBytes: ByteArray): Boolean {
        val appFile = prepareApp(prgBytes)
        println("----------")
        println("Testing app '$appFile'...")

        val result = testApp(appFile)
        if(result) {
            appFile.delete()
        }
        return result
    }

    private suspend fun testApp(appFile: File): Boolean = withContext(Dispatchers.IO) {
        val processBuilder = ProcessBuilder()
        processBuilder.command(monkeyDoCommand, appFile.absolutePath, "vivoactive4")
        val startTime = System.currentTimeMillis()
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        while(System.currentTimeMillis() - startTime < timeout) {
            if(reader.ready()) {
                val line = reader.readLine()
                println("  > $line")
                if(line == expectedOutput) {
                    println("Finished testing with success!")
                    return@withContext true
                }
            }
            delay(100)
        }
        println("[!] App Failed [!]")
        return@withContext false
    }

    private fun prepareApp(prgBytes: ByteArray): File {
        val file = File("assets/fuzzing/app_${System.currentTimeMillis()}.prg")
        file.writeBytes(prgBytes)
        return file
    }

}