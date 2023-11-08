package poet

import PrgFile
import kotlin.random.Random

class Poet(seed: Int) {

    private val random: Random
    init {
        random = Random(seed)
    }

    fun generate(prg: PrgFile) = sequence {
        yieldAll(randomByteZero(prg).take(100))
    }
    private fun randomByteZero(prg: PrgFile) = sequence {
        while(true) {
            val selectedByte = random.nextInt(prg.appBytes.size)
            prg.appBytes[selectedByte] = 0
            yield(prg.export().toByteArray())
            prg.appBytes[selectedByte] = 1
        }
    }



}