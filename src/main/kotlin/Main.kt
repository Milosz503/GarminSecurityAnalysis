import oracle.Oracle
import oracle.Simulator
import poet.Mutator
import poet.Poet
import java.io.File
import java.security.Signature
import kotlin.random.Random

const val terminatorLength = 8
const val rsaSignatureLength = 512
const val fullSignatureLength = 1036

suspend fun main(args: Array<String>) {
    val simulator = Simulator()
    if(!simulator.start()) {
        println("Failed to start simulator")
        simulator.close()
        return
    }

    val prgFile = PrgFile.fromFile(File("assets/build2/BackgroundTimer.prg"))
    val random = Random(7)
    val poet = Poet(random)
    val mutator = Mutator(prgFile, poet, random, 0.01f)
    val oracle = Oracle(simulator, "Okay", 15000)

    var appPassed = true
    for (i in 1..50) {
        println("Iteration $i")
        val bytes = mutator.mutate(appPassed)
        val result = oracle.check(bytes)
        appPassed = result == Oracle.Result.FileValid

        if(!simulator.isAlive()) {
            println("SUCCESS - SIMULATOR DIED!")
            break
        }
    }

    simulator.close()

//    File("assets/BackgroundTimer2.prg").writeBytes(PrgFile.fromFile("assets/BackgroundTimer.prg").export().toByteArray())
//    println("Test \u0000 test")
//    removeStoreSignature("assets/challenges.prg")
//    verify("assets/BackgroundTimer.prg", File("assets/dev_key.pub"))
//    sign("assets/BackgroundTimer-edited.prg", File("assets/developer_key"))
//    verify("assets/output.prg", File("assets/dev_key.pub"))
}

fun removeStoreSignature(inputFileName: String) {
    val bytes = File(inputFileName).readBytes()
    File("$inputFileName.dev.prg").writeBytes((bytes.dropLast(520 + terminatorLength) + getTerminator(bytes)).toByteArray())
}
fun verify(inputFileName: String, publicKeyFile: File) {
    val bytes = File(inputFileName).readBytes()
    val inputBytes = getAppBytes(bytes)
    val fullSignature = getFullSignature(bytes)
    val signature = fullSignature.drop(8).take(rsaSignatureLength).toByteArray()
    val dsa = Signature.getInstance("SHA1withRSA")
    dsa.initVerify(KeyUtils.getPublicKey(publicKeyFile))
    dsa.update(inputBytes.toByteArray())
    val isValid = dsa.verify(signature)
    println("Is valid: $isValid")
}

fun sign(inputFileName: String, privateKeyFile: File) {
    val bytes = File(inputFileName).readBytes()
    val appBytes = getAppBytes(bytes)
    val dsa = Signature.getInstance("SHA1withRSA")
    dsa.initSign(KeyUtils.getPrivateKey(privateKeyFile))
    dsa.update(appBytes.toByteArray())
    val signature = dsa.sign().toList()
    val fullSignature = getFullSignature(bytes)
    val newSignature = fullSignature.take(8) + signature + fullSignature.drop(8 + rsaSignatureLength)
    File("assets/output.prg").writeBytes((appBytes + newSignature + getTerminator(bytes)).toByteArray())
    println("Signature length: ${signature.size}")
}

fun getAppBytes(bytes: ByteArray): List<Byte> {
    return bytes.dropLast(fullSignatureLength + terminatorLength)
}

fun getFullSignature(bytes: ByteArray): List<Byte> {
    return bytes.dropLast(terminatorLength).takeLast(fullSignatureLength)
}

fun getTerminator(bytes: ByteArray) = bytes.takeLast(terminatorLength)

