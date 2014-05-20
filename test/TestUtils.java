import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import play.db.jpa.JPA;
import wikilocation.WikiMediaApi;
import wikilocation.managers.LocationManager;
import wikilocation.models.BaseLocation;
import wikilocation.models.WikiArticle;
import wikilocation.pojos.Position;
import wikilocation.utils.HtmlParser;
import wikilocation.utils.PositionCalculator;

import java.util.List;

import static org.fest.assertions.Assertions.*;

/**
 * Created by swami on 23/04/14.
 */
public class TestUtils extends TestBase {

    @Test
    public void testParseHtml() throws Exception {
        String baseUri = "http://sv.wikipedia.org";

        String html = readFile("parseTest.html");
        String parsed =  HtmlParser.parseWikiHtml(html, baseUri);

        assertThat(html).isNotSameAs(parsed);
    }

}
