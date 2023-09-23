import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.*
import java.security.interfaces.RSAPrivateCrtKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec

object KeyUtils {
    const val CIQ_KEY_ALGORITHM = "RSA"
    const val CIQ_KEY_SIZE = 4096
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class, IOException::class)
    fun getPrivateKey(filename: String): RSAPrivateCrtKey {
        val privateKey = getPrivateKey(filename, CIQ_KEY_ALGORITHM)
        if (privateKey is RSAPrivateCrtKey) {
            return privateKey
        }
        throw InvalidKeySpecException("Expecting an RSA key.")
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class, IOException::class)
    fun getPrivateKey(file: File): RSAPrivateCrtKey {
        val privateKey = getPrivateKey(file, CIQ_KEY_ALGORITHM)
        if (privateKey is RSAPrivateCrtKey) {
            return privateKey
        }
        throw InvalidKeySpecException("Expecting an RSA key.")
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPrivateKey(filename: String, algorithm: String): PrivateKey {
        val file = File(filename)
        return getPrivateKey(file, algorithm)
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPrivateKey(file: File, algorithm: String): PrivateKey {
        val fis = FileInputStream(file)
        val key = getPrivateKey(fis, algorithm)
        fis.close()
        return key
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPrivateKey(inputStream: InputStream, algorithm: String): PrivateKey {
        val keyBytes: ByteArray = inputStream.readAllBytes()
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(algorithm)
        return keyFactory.generatePrivate(spec)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class, IOException::class)
    fun getPublicKey(filename: String): PublicKey {
        return getPublicKey(filename, CIQ_KEY_ALGORITHM)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class, IOException::class)
    fun getPublicKey(file: File): PublicKey {
        return getPublicKey(file, CIQ_KEY_ALGORITHM)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class, IOException::class)
    fun getPublicKey(fileBytes: ByteArray): PublicKey {
        return getPublicKey(fileBytes, CIQ_KEY_ALGORITHM)
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(filename: String, algorithm: String): PublicKey {
        val file = File(filename)
        return getPublicKey(file, algorithm)
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(file: File, algorithm: String): PublicKey {
        val keyBytes: ByteArray = file.readBytes()
        return getPublicKey(keyBytes, algorithm)
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(fileBytes: ByteArray, algorithm: String): PublicKey {
        val spec = X509EncodedKeySpec(fileBytes)
        val keyFactory = KeyFactory.getInstance(algorithm)
        return keyFactory.generatePublic(spec)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(privateKey: PrivateKey): PublicKey {
        if (privateKey !is RSAPrivateCrtKey) {
            throw InvalidKeySpecException()
        }
        val prvKey = privateKey
        return generatePublicKey(prvKey.modulus, prvKey.publicExponent)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generatePublicKey(modulus: BigInteger, exponent: BigInteger): PublicKey {
        val spec = RSAPublicKeySpec(modulus, exponent)
        val factory = KeyFactory.getInstance(CIQ_KEY_ALGORITHM)
        return factory.generatePublic(spec)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generatePublicKey(modulus: ByteArray, exponent: Int): PublicKey {
        val modifiedModulusBytes: ByteArray
        if (modulus.size == 512) {
            modifiedModulusBytes = ByteArray(modulus.size + 1)
            System.arraycopy(modulus, 0, modifiedModulusBytes, 1, modulus.size)
        } else {
            modifiedModulusBytes = modulus
        }
        val mod = BigInteger(modifiedModulusBytes)
        val exp = BigInteger(ByteBuffer.allocate(4).putInt(exponent).array())
        return generatePublicKey(mod, exp)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance(CIQ_KEY_ALGORITHM)
        keyGen.initialize(4096)
        return keyGen.generateKeyPair()
    }
}