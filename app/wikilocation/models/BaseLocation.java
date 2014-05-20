package wikilocation.models;

import wikilocation.enums.LocationType;
import wikilocation.pojos.Position;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by swami on 10/04/14.
 */
@MappedSuperclass
public abstract class BaseLocation {

    @Id
    public Long id;

    // This is the ID that maps to a wikipedia-article
    @Column(name = "article_id")
    public Long articleId;

    public Double lat;

    public Double lng;

    @Enumerated(EnumType.STRING)
    public LocationType type;

    public String title;

    public static Map<String, Class<? extends BaseLocation>> getLocationMapper() {
        // TODO: this should be done automatically, but it's a bit too much work.
        Map<String, Class<? extends BaseLocation>> locationMapper = new HashMap<String, Class<? extends BaseLocation>>();
        locationMapper.put("sv", SvLocation.class);
        locationMapper.put("en", EnLocation.class);
        return locationMapper;
    }

    public static Class<? extends BaseLocation> getClassByLocale(String locale) {
        return getLocationMapper().get(locale);
    }

    public abstract String getLocale();

    public String getUrl() {
        return String.format("http://%s.wikipedia.org/wiki?curid=%d", getLocale(), articleId);
    }

}
