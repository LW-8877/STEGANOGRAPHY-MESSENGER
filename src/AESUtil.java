import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * AES utility with simple password-based key derivation.
 * NOTE: Educational only. We derive a 128-bit key by hashing the password
 * with SHA-256 and taking the first 16 bytes. Good enough for student demo.
 */
public class AESUtil {

    private static SecretKey deriveKey(String password) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(password.getBytes("UTF-8"));
        byte[] keyBytes = new byte[16]; // 128-bit AES
        System.arraycopy(hash, 0, keyBytes, 0, 16);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String message, String password) throws Exception {
        SecretKey key = deriveKey(password);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // simple mode for demo
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(message.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedB64, String password) throws Exception {
        SecretKey key = deriveKey(password);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encryptedB64);
        byte[] plain = cipher.doFinal(decoded);
        return new String(plain, "UTF-8");
    }
}
