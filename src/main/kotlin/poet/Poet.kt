package poet

import PrgFile
import kotlin.random.Random

class Poet(seed: Int) {

    private val random: Random

    init {
        random = Random(seed)
    }

    fun generate(prg: PrgFile) = sequence<ByteArray> {
//        yield(prg.export().toByteArray())
//        yield((prg.appBytes + prg.terminator).toByteArray())
//        val newPrg = PrgFile(prg.appBytes.dropLast(PrgFile.fullSignatureLength), prg.appBytes.takeLast(PrgFile.fullSignatureLength))
//        yield(newPrg.export().toByteArray())
        yieldAll(completelyRandomWithHeader(prg))
    }

    private fun randomByteZero(prg: PrgFile) = sequence {
        while (true) {
            val selectedByte = random.nextInt(prg.appBytes.size)
            prg.appBytes[selectedByte] = 0
            yield(prg.export().toByteArray())
            prg.appBytes[selectedByte] = 1
        }
    }

    private fun completelyRandom(prg: PrgFile) = sequence {
        while (true) {
            val bytes = random.nextBytes(1000).toList()
            val newPrg = PrgFile(bytes, prg.originalSignature)
            yield(newPrg.export().toByteArray())
        }
    }

    private fun completelyRandomWithHeader(prg: PrgFile) = sequence {
        while (true) {
            val bytes = random.nextBytes(1000).toList()
            val newPrg = PrgFile(prg.appBytes.take(1000) + bytes, prg.originalSignature)
            yield(newPrg.export().toByteArray())
        }
    }


}