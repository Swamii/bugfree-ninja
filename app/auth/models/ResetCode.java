package auth.models;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;

/**
 * Created by swami on 18/04/14.
 */
//@Entity
//@Table(name = "reset_code")
public class ResetCode {

    @Id
    @GeneratedValue
    public Long id;

    @Column(nullable = false)
    public Boolean used;

    @OneToMany
    @Column(nullable = false)
    public User user;

    @Column(nullable = false)
    public DateTime created;

    @PrePersist
    public void prePersist() {
        // set some default values

        if (used == null) {
            used = false;
        }
        if (created == null) {
            created = DateTime.now(DateTimeZone.UTC);
        }
    }

    public static Boolean use() {
        return null;
    }

}
