
import org.testng.annotations.Test
import java.io.File
import kotlin.test.assertEquals

internal class PrgFileTest {

    @Test
    fun testSigning() {
        val file = File("assets/BackgroundTimer.prg")
        val originalBytes = file.readBytes().toList()
        val prgFile = PrgFile.fromFile(file)
        val newBytes = prgFile.export()
        assertEquals(originalBytes.size, newBytes.size)
        assertEquals(originalBytes, newBytes)
    }
}