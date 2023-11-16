package utils

import kotlin.experimental.xor
import kotlin.random.Random

class Utils {

    companion object {
        fun mutateByte(random: Random, byte: Byte): Byte {
            val bit = random.nextInt(8)
            return flipBit(byte, bit)
        }

        fun flipBit(byte: Byte, bit: Int): Byte {
            return byte xor (1 shl bit).toByte()
        }
    }
}