package wikilocation.models;

import wikilocation.enums.LocationType;
import wikilocation.pojos.Position;

import javax.persistence.*;

/**
 * Created by swami on 09/04/14.
 */

@Entity
@Table(name = "live_en")
public class EnLocation extends BaseLocation {

    @Override
    public String getLocale() {
        return "en";
    }
}
