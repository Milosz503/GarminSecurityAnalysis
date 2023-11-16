import org.testng.annotations.Test
import utils.Utils
import kotlin.test.expect

internal class UtilsTest {

    @Test
    fun testFlipBit() {
        expect(0b00000001.toByte()) {
            Utils.flipBit(0b00000000.toByte(), 0)
        }

        expect(0b00100000.toByte()) {
            Utils.flipBit(0b00000000.toByte(), 5)
        }

        expect(0b00000000.toByte()) {
            Utils.flipBit(0b10000000.toByte(), 7)
        }

        expect(0b10111111.toByte()) {
            Utils.flipBit(0b11111111.toByte(), 6)
        }
    }
}