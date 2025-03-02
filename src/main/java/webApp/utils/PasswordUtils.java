package webApp.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PasswordUtils {

    private static final String ALGORITHM = "AES";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATIONS = 65536;

    /**
     * ✅ Generates an AES key using PBKDF2 from a dynamic key source (e.g., email)
     */
    private static SecretKey generateKey(String keySource) throws Exception {
        byte[] salt = keySource.getBytes(StandardCharsets.UTF_8); // Use email or unique data as salt
        PBEKeySpec spec = new PBEKeySpec(keySource.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * ✅ Encrypts a text (e.g., password) using AES.
     */
    public static String encrypt(String data, String keySource) throws Exception {
        SecretKey key = generateKey(keySource);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * ✅ Decrypts an AES-encrypted text.
     */
    public static String decrypt(String encryptedData, String keySource) throws Exception {
        SecretKey key = generateKey(keySource);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        try {
            String password = "";
            String email = "";  // Use dynamic key source

            // ✅ Encrypt password
            String encryptedPassword = encrypt(password, email);
            System.out.println("🔐 Encrypted: " + encryptedPassword);

            // ✅ Decrypt password
            String decryptedPassword = decrypt(encryptedPassword, email);
            System.out.println("🔓 Decrypted: " + decryptedPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
