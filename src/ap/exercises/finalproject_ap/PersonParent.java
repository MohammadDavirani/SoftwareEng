package ap.exercises.finalproject_ap;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PersonParent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String userId;
    private String username;
    private String password;

    public PersonParent(String name, String userId, String username, String password) {
        this(name, userId, username, password, false);
    }

    // Restores a person saved in the database, whose password is already hashed
    // and therefore must not be hashed a second time.
    protected PersonParent(String name, String userId, String username, String password, boolean passwordIsHashed) {
        this.name = name;
        this.userId = userId;
        this.username = username;
        this.password = passwordIsHashed ? password : hashPassword(password);
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public boolean checkPassword(String rawPassword) {
        return this.password != null && this.password.equals(hashPassword(rawPassword));
    }

    // Database layer only: the stored (already hashed) password.
    String getPasswordHash() {
        return password;
    }

    private static String hashPassword(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            // SHA-256 is guaranteed to be available in every standard JVM.
            return rawPassword;
        }
    }
}
