package auth.models;

import auth.UserManager;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swami on 17/04/14.
 */

@Entity
@Table(name="`user`")  // Postgres already has a user table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    public Long id;

    public String email;

    @Constraints.Required
    @Column(unique = true, name = "device_uuid")
    public String deviceUUID;

    @Column(name = "api_key")
    public String apiKey;

    public String secret;

    @Column(name = "first_name")
    public String firstName;

    @Column(name = "last_name")
    public String lastName;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        if (id == null) {
            // First time user
            if (null != UserManager.byDevice(deviceUUID)) {
                errors.add(new ValidationError("device_unique", "The device already has a user"));
            }

        } else {
            // This is the real register-action.
            if (null != UserManager.byEmail(email)) {
                errors.add(new ValidationError("email_unique", "That email is already taken"));
            }
        }

        return errors.isEmpty() ? null : errors;
    }

}
