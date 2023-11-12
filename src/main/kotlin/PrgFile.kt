import java.io.File
import java.security.Signature

class PrgFile(
    bytes: List<Byte>,
    val originalSignature: List<Byte>,
    val terminator: List<Byte> = List(terminatorLength) { 0 }
) {

    val appBytes: MutableList<Byte>

    companion object {

        const val terminatorLength = 8
        const val rsaSignatureLength = 512
        const val fullSignatureLength = 1036
        fun fromFile(file: File): PrgFile {
            val bytes = file.readBytes().toList()
            val appBytes = getAppBytes(bytes)
            val signature = getFullSignature(bytes)
            val terminator = getTerminator(bytes)
            return PrgFile(appBytes, signature, terminator)
        }

        private fun getAppBytes(bytes: List<Byte>): MutableList<Byte> {
            return bytes.dropLast(fullSignatureLength + terminatorLength).toMutableList()
        }

        private fun getFullSignature(bytes: List<Byte>): List<Byte> {
            return bytes.dropLast(terminatorLength).takeLast(fullSignatureLength)
        }

        private fun getTerminator(bytes: List<Byte>) = bytes.takeLast(terminatorLength)
    }

    init {
        appBytes = bytes.toMutableList()
    }

    fun export(): List<Byte> {
        val dsa = Signature.getInstance("SHA1withRSA")
        dsa.initSign(KeyUtils.getPrivateKey(File("assets/developer_key")))
        dsa.update(appBytes.toByteArray())
        val signature = dsa.sign().toList()
        val newSignature = originalSignature.take(8) + signature + originalSignature.drop(8 + rsaSignatureLength)
        return appBytes + newSignature + terminator
    }


}