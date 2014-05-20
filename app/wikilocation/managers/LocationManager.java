package wikilocation.managers;

import org.apache.http.client.ClientProtocolException;
import play.db.jpa.JPA;
import wikilocation.WikiMediaApi;
import wikilocation.models.BaseLocation;
import wikilocation.models.SvLocation;
import wikilocation.models.WikiArticle;
import wikilocation.utils.PositionCalculator;
import wikilocation.models.EnLocation;
import wikilocation.pojos.Position;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swami on 08/04/14.
 */
public class LocationManager {

    private final static Integer DEFAULT_OFFSET = 500;

    public static List<BaseLocation> filter(Position pos) {
        return filter(pos, 0, 0);
    }

    public static List<BaseLocation> filter(Position pos, Integer limit) {
        return filter(pos, 0, limit);
    }

    public static List<BaseLocation> filter(Position pos, Integer offset, Integer limit) {
        if (offset == null || offset <= 0) {
            offset = DEFAULT_OFFSET;
        }

        Position[] positions = PositionCalculator.getSquare(pos, offset);

        Position bigPos = positions[0];
        Position smallPos = positions[1];

        Map<String, Double> params = new HashMap<String, Double>();
        params.put("bLat", bigPos.getLat());
        params.put("bLng", bigPos.getLng());
        params.put("sLat", smallPos.getLat());
        params.put("sLng", smallPos.getLng());

        List<BaseLocation> locs = new ArrayList<BaseLocation>();
        locs.addAll(executeForClass(EnLocation.class, params, limit));
        locs.addAll(executeForClass(SvLocation.class, params, limit));

        return locs;
    }

    private static List<? extends BaseLocation> executeForClass(Class<? extends BaseLocation> loc, Map<String, Double> params, Integer limit) {
        String query = String.format("select loc from %s loc "
                + "where loc.lat < :bLat and loc.lng < :bLng "
                + "and loc.lat > :sLat and loc.lng > :sLng", loc.getName());
        TypedQuery<? extends BaseLocation> q =  JPA.em().createQuery(query, loc);

        for (Map.Entry<String, Double> param : params.entrySet()) {
            q.setParameter(param.getKey(), param.getValue());
        }

        if (limit != null && limit > 0) {
            q = q.setMaxResults(limit);
        }
        return q.getResultList();
    }

    public static WikiArticle find(Long articleId, String locale) {
        String query = "select wa from WikiArticle wa where articleId = :articleId and locale = :locale";

        try {
            return JPA.em().createQuery(query, WikiArticle.class)
                           .setParameter("articleId", articleId)
                           .setParameter("locale", locale)
                           .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public static WikiArticle get(Long articleId, String locale) {
        WikiArticle article = find(articleId, locale);

        if (article == null) {
            try {
                article = WikiMediaApi.query(articleId, locale);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            }
            JPA.em().persist(article);
        }
        return article;
    }

    public static List<? extends BaseLocation> getByTitles(List<String> titles, String locale) {
        Class<? extends BaseLocation> locClass = BaseLocation.getClassByLocale(locale);
        String query = String.format("select loc from %s loc where title in :titles", locClass.getName());
        List<? extends BaseLocation> locs = JPA.em().createQuery(query, locClass)
                .setParameter("titles", titles)
                .getResultList();
        return locs;
    }

}
