import java.io.File
import java.security.Signature

class PrgFile(
    bytes: List<Byte>
) {
    val appBytes: MutableList<Byte>

    private val originalSignature: List<Byte>
    private val terminator: List<Byte>

    companion object {

        const val terminatorLength = 8
        const val rsaSignatureLength = 512
        const val fullSignatureLength = 1036
        fun fromFile(fileName: String): PrgFile {
            return PrgFile(File(fileName).readBytes().toList())
        }
    }

    init {
        appBytes = getAppBytes(bytes)
        originalSignature = getFullSignature(bytes)
        terminator = getTerminator(bytes)
    }

    fun export(): List<Byte> {
        val dsa = Signature.getInstance("SHA1withRSA")
        dsa.initSign(KeyUtils.getPrivateKey(File("assets/developer_key")))
        dsa.update(appBytes.toByteArray())
        val signature = dsa.sign().toList()
        val newSignature = originalSignature.take(8) + signature + originalSignature.drop(8 + rsaSignatureLength)
        return appBytes + newSignature + terminator
    }

    private fun getAppBytes(bytes: List<Byte>): MutableList<Byte> {
        return bytes.dropLast(fullSignatureLength + terminatorLength).toMutableList()
    }

    private fun getFullSignature(bytes: List<Byte>): List<Byte> {
        return bytes.dropLast(terminatorLength).takeLast(fullSignatureLength)
    }

    private fun getTerminator(bytes: List<Byte>) = bytes.takeLast(terminatorLength)
}