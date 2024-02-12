package ro.bogdan_mierloiu.authserver.config;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CustomEncoder{
    private final SecretKey secretKey;

    public CustomEncoder(String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(), 0, secretKey.getBytes().length, "AES");
    }

    public String encrypt(String token) {
        try {
            byte[] utf8 = token.getBytes(StandardCharsets.UTF_8);

            Cipher encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encrypted = encryptCipher.doFinal(utf8);

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encryptedToken) {
        try {

            byte[] encrypted = Base64.getDecoder().decode(encryptedToken);

            Cipher decryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");


            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new String(decryptCipher.doFinal(encrypted));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}



