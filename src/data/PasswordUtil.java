package data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Turns a plain password into the SHA-256 hash stored in the users
 * table, so passwords are never kept as readable text in the database.
 */
public final class PasswordUtil {

    private PasswordUtil() {
        // static helpers only
    }

    /** Returns the lower-case hexadecimal SHA-256 hash of the input. */
    public static String hash(String plain) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(plain.getBytes("UTF-8"));

            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                String part = Integer.toHexString(0xFF & b);
                if (part.length() == 1) {
                    hex.append('0');
                }
                hex.append(part);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 is not available", e);
        }
    }

    /** Compares a typed password with the hash stored in the database. */
    public static boolean matches(String plain, String storedHash) {
        return storedHash != null && storedHash.equalsIgnoreCase(hash(plain));
    }
}
