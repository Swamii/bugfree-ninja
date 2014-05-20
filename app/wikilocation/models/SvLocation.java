package wikilocation.models;

import wikilocation.enums.LocationType;
import wikilocation.pojos.Position;

import javax.persistence.*;

/**
 * Created by swami on 09/04/14.
 */

@Entity
@Table(name = "live_sv")
public class SvLocation extends BaseLocation {

    @Override
    public String getLocale() {
        return "sv";
    }

}
