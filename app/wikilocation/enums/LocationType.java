package wikilocation.enums;

/**
 * Created by swami on 09/04/14.
 */
public enum LocationType {
    glacier,
    edu,
    railwaystation,
    mountain,
    isle,
    satellite,
    adm2nd,
    waterbody,
    adm3rd,
    camera,
    state,
    airport,
    park,
    neighbourhood,
    river,
    pass,
    wreck,
    forest,
    adm1st,
    road,
    country,
    event,
    landmark,
    city,
    church,
    building,
    socken,;  // empty as well

    @Override
    public String toString() {
        String name = this.name();
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }


}
