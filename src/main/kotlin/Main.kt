import java.io.File
import java.nio.ByteBuffer
import java.security.Signature

const val terminatorLength = 8
const val rsaSignatureLength = 512
const val fullSignatureLength = 1036

fun main(args: Array<String>) {
//    verify("assets/BackgroundTimer.prg", File("assets/dev_key.pub"))
    sign("assets/BackgroundTimer-edited.prg", File("assets/developer_key"))
    verify("assets/output.prg", File("assets/dev_key.pub"))
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

