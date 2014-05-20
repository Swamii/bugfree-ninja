package auth;

import auth.models.User;
import play.db.jpa.JPA;

import javax.persistence.NoResultException;

/**
 * Created by swami on 17/04/14.
 */
public class UserManager {


    public static User byEmail(String email) {
        String query = "select u from User u where u.email = :email";

        try {
            return JPA.em().createQuery(query, User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public static User byDevice(String deviceUUID) {
        String query = "select u from User u where u.deviceUUID = :deviceUUID";

        try {
            return JPA.em().createQuery(query, User.class)
                    .setParameter("deviceUUID", deviceUUID)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public static User create(User user) {
        // Generate apiKey
        assert (user.deviceUUID != null);  // should be set on form
        user.apiKey = Hasher.generateApiKey();
        JPA.em().persist(user);

        return user;
    }

    public static User register(User updatedUser) {
        // Generate secret and update user with email.
        assert (updatedUser.email != null);

        User user = JPA.em().find(User.class, updatedUser.id);

        user.email = updatedUser.email;
        user.firstName = updatedUser.firstName;
        user.lastName = updatedUser.lastName;
        user.secret = Hasher.createSecret(user.email);
        JPA.em().persist(user);

        return user;
    }

    /**
     * Secret Data for a not-logged-in user. apiKey is required to make GET requests to the backend.
     * It does not allow POST nor PUT. Except for register-actions of course.
     */
    public static class SecretDataMini {
        public String apiKey;

        public SecretDataMini() {}
        public SecretDataMini(User user) {
            apiKey = user.apiKey;
        }
    }

    /**
     * Full secret data for a logged-in user. The secret is required to make POST requests to the backend.
     */
    public static class SecretDataFull extends SecretDataMini {
        public String secret;
        public String email;

        public SecretDataFull(User user) {
            super(user);
            secret = user.secret;
            email = user.email;
        }
    }

}
