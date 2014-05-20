import org.junit.Test;
import play.libs.F;
import play.db.jpa.JPA;
import wikilocation.WikiMediaApi;
import wikilocation.managers.LocationManager;
import wikilocation.models.BaseLocation;
import wikilocation.models.WikiArticle;
import wikilocation.pojos.Position;
import wikilocation.utils.PositionCalculator;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.*;

/**
 * Created by Swami on 24/03/14.
 */
public class LocationTests extends TestBase {

    Position posInData = new Position(51.123234, 0.11111);
    Position posNotInData = new Position(50.123234, -0.11111);

    @Test
    public void testGet() throws Exception {
        JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws Throwable {
                Integer offset = 1000;

                // Test with OK positions
                List<BaseLocation> locs = LocationManager.filter(posInData, offset, 10);
                assertThat(locs).isNotEmpty();

                locs = LocationManager.filter(posNotInData, offset, 10);
                assertThat(locs).isEmpty();

                Double distance = PositionCalculator.distFrom(posInData, posNotInData);
                assertThat(distance).isGreaterThan(offset);

            }
        });
    }

    @Test
    public void testWikiMedia() throws Exception {
        JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws Throwable {

                Long[] articleIds = new Long[] {2466732L, 6447660L};
                String mockResponse = readFile("mockWikiMediaResponse.json");

                List<WikiArticle> resp = WikiMediaApi.parseArticles(mockResponse, "en");
                assertThat(resp).isNotEmpty();

                for (WikiArticle a : resp) {
                    assertThat(articleIds).contains(a.articleId);
                    assertThat(a.timestamp).isNotNull();
                    assertThat(a.content).isNotNull();
                    assertThat(a.locale).isNotNull();
                }

            }
        });
    }

}
