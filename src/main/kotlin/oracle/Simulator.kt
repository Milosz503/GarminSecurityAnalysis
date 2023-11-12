package oracle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.BufferedReader
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Simulator {
    private val simulatorCommand =
        "/home/milosz/.Garmin/ConnectIQ/Sdks/connectiq-sdk-lin-6.3.1-2023-09-13-47b193194/bin/simulator"
    private val expectedInitLine = "Debug: SetLayout"

    lateinit var process: Process
    lateinit var inputReader: BufferedReader

    suspend fun start(): Boolean = withContext(Dispatchers.IO) {
        val processBuilder = ProcessBuilder()
        processBuilder.command("stdbuf", "-oL", "-eL", simulatorCommand)
        processBuilder.redirectErrorStream(true)
        process = processBuilder.start()
        inputReader = process.inputReader()


        return@withContext withTimeoutOrNull(100000.seconds) {
            while (process.isAlive) {
                delay(100)
                while (inputReader.ready()) {
                    val line = inputReader.readLine()
                    println("  >SIM $line")
                    if (line.endsWith(expectedInitLine)) {
                        println(" DESCENDANTS: ${process.descendants()}")
                        return@withTimeoutOrNull true
                    }
                }
            }
            return@withTimeoutOrNull false
        } ?: false
    }

    fun isAlive() = process.isAlive

    fun ready() = inputReader.ready()

    suspend fun readLine(): String = withContext(Dispatchers.IO) {
        return@withContext inputReader.readLine()
    }

    suspend fun close() {
        process.destroy()
        withContext(Dispatchers.IO) {
            process.onExit().join()
        }
        print("Destroyed simulator, exit code: ${process.exitValue()}")
    }

}