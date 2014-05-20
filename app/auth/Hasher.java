package auth;

import auth.models.User;
import org.apache.commons.codec.binary.Hex;
import play.Play;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by swami on 21/04/14.
 */
public class Hasher {

    /**
     * Creates a secret from: email + secret key
     * @param email
     * @return
     */
    public static String createSecret(String email) {
        String appSecret = Play.application().configuration().getString("application.secret");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(appSecret.getBytes("UTF-8"));
            digest.update(email.getBytes("UTF-8"));
            return Hex.encodeHexString(digest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies a call to the api based on the query params
     * @param user
     * @param params
     * @param hash
     * @return
     */
    public static boolean verifyHash(User user, String params, String hash) {
        return hash.equals(signParams(user, params));
    }

    public static boolean verifyHash(User user, Map params, String hash) {
        return false;
    }

    public static String signParams(User user, String params) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(user.secret.getBytes("UTF-8"));
            digest.update(params.getBytes("UTF-8"));
            return Hex.encodeHexString(digest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a apiKey
     * @return
     */
    public static String generateApiKey() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
