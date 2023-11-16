package poet

import PrgFile
import utils.Utils
import kotlin.math.ceil
import kotlin.random.Random

class Mutator(
    private val originalPrg: PrgFile,
    poet: Poet,
    private val random: Random,
    private val mutationRate: Float,
) {
    private val sequence: Sequence<ByteArray> = poet.generate(originalPrg)
    private val currentPrg: PrgFile = PrgFile.fromBytes(originalPrg.export().toByteArray())

    private val mutatedBytes = mutableSetOf<Int>()
    private var lastMutation = emptyList<Int>()
    private var currentMutationRate = mutationRate
    private var lastResult = true

    fun mutate(appPassed: Boolean): ByteArray {
        lastResult = appPassed

        if(appPassed) {
            println(">> Mutating with noise")
            mutateCurrentApp()
        } else {
            println(">> Reverting some mutations")
            revertSomeMutations()
        }
        println(">> Current mutation: ${mutatedBytes.size / currentPrg.appBytes.size.toFloat()}")
        return currentPrg.export().toByteArray()
    }

    private fun mutateCurrentApp() {
        val mutation = generateMutation()
        mutatedBytes.addAll(mutation)
        lastMutation = mutation

        for (i in mutation) {
            currentPrg.appBytes[i] = Utils.mutateByte(random, currentPrg.appBytes[i])
        }
    }

    private fun revertSomeMutations() {
        val bytesToRevert = if(lastMutation.isNotEmpty()) {
            val divider = ceil(lastMutation.size / 2.0).toInt()
            lastMutation.subList(0, divider).also {
                lastMutation = lastMutation.subList(divider, lastMutation.size)
            }
        } else {
            mutatedBytes.randomSubset(ceil(mutatedBytes.size / 2.0).toInt())
        }
        for (i in bytesToRevert) {
            currentPrg.appBytes[i] = originalPrg.appBytes[i]
        }
        mutatedBytes.removeAll(bytesToRevert)
    }

    fun <T> Set<T>.randomSubset(size: Int): List<T> {
        if (size >= this.size) {
            return this.toList()
        }

        val shuffledList = this.toList().shuffled()
        return shuffledList.subList(0, size)
    }
    private fun generateMutation(): List<Int> {
        val appLength = currentPrg.appBytes.size
        val mutationSize = (currentPrg.appBytes.size * currentMutationRate).toInt()
        return List(mutationSize) {
            random.nextInt(appLength)
        }
    }

}