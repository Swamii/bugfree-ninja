import ad.models.Ad;
import auth.models.User;
import org.junit.After;
import org.junit.Before;
import play.db.jpa.JPA;
import play.libs.Yaml;
import play.test.WithApplication;
import wikilocation.models.EnLocation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeGlobal;
import static play.test.Helpers.start;

/**
 * Created by swami on 10/04/14.
 */
public class TestBase extends WithApplication {

    public File testDataDir;

    public String readFile(String path) throws IOException {
        File file = new File(testDataDir, path);
        return readFile(file.getAbsolutePath(), Charset.forName("UTF-8"));
    }

    public String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @Before
    public void setUp() throws Exception {
        final HashMap<String,String> postgres = new HashMap<String, String>();
        postgres.put("jpa.default", "testPersistenceUnit");
        postgres.put("db.default.driver", "org.h2.Driver");
        postgres.put("db.default.url", "jdbc:h2:mem:play");
        postgres.put("db.default.jndiName", "TestDS");
        postgres.put("evolutionplugin", "disabled");
        start(fakeApplication(postgres));

        testDataDir = new File(play.Play.application().path(), "conf/test-data/");

        JPA.withTransaction(new play.libs.F.Callback0() {
            public void invoke() {
                Map items = (Map) Yaml.load("test-data/initial-data.yml");

                List locations = (List) items.get("enLocations");
                for (Object location : locations) {
                    if (location instanceof EnLocation) {
                        EnLocation loc = (EnLocation) location;
                        JPA.em().persist(loc);
                    }
                }

                List users = (List) items.get("users");
                for (Object user : users) {
                    if (user instanceof User) {
                        User usr = (User) user;
                        JPA.em().persist(usr);
                    }
                }

                List ads = (List) items.get("ads");
                for (Object add : ads) {
                    if (add instanceof Ad) {
                        Ad ad = (Ad) add;
                        JPA.em().persist(ad);
                    }
                }

            }
        });
    }

}
