
import org.testng.annotations.Test
import java.io.File
import kotlin.test.assertEquals

internal class PrgFileTest {

    @Test
    fun testSigning() {
        val originalBytes = File("assets/BackgroundTimer.prg").readBytes().toList()
        val prgFile = PrgFile(originalBytes)
        val newBytes = prgFile.export()
        assertEquals(originalBytes.size, newBytes.size)
        assertEquals(originalBytes, newBytes)
    }
}