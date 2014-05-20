import auth.UserManager;
import auth.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.routes;
import org.junit.*;

import play.api.Play;
import play.db.jpa.JPA;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static play.test.Helpers.contentAsString;


/**
 * Created by swami on 22/04/14.
 */
public class FunctionalTest extends TestBase {

    @Test
    public void testSayHi() {
        JPA.withTransaction(new play.libs.F.Callback0() {
            @Override
            public void invoke() throws Throwable {

                String deviceUUID = "ace32425aff23122314aec";

                Result result = callAction(controllers.routes.ref.AuthController.sayHi());

                assertThat(status(result)).isEqualTo(BAD_REQUEST);

                Map<String, String> data = new HashMap<String, String>();
                data.put("deviceUUID", deviceUUID);
                JsonNode jsonData = Json.toJson(data);

                result = callAction(
                        controllers.routes.ref.AuthController.sayHi(),
                        fakeRequest().withJsonBody(jsonData)
                );

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentAsString(result)).contains(deviceUUID);

                result = callAction(
                        controllers.routes.ref.AuthController.sayHi(),
                        fakeRequest().withJsonBody(jsonData)
                );

                assertThat(status(result)).isEqualTo(BAD_REQUEST);
                assertThat(contentAsString(result)).contains("The device already has a user");

            }
        });
    }

    @Test
    public void testRegister() {
        JPA.withTransaction(new play.libs.F.Callback0() {
            @Override
            public void invoke() throws Throwable {

                String takenEmail = "lala@test.ok";
                String notTakenEmail = "wala@test.ok";
                String testDevice = "34rwfdsfsdfadfs";
                User userWithoutSecret = UserManager.byDevice(testDevice);  // added in initial-data.yml

                Result result = callAction(controllers.routes.ref.AuthController.register());
                assertThat(status(result)).isEqualTo(BAD_REQUEST);

                Map<String, String> data = new HashMap<String, String>();
                data.put("deviceUUID", userWithoutSecret.deviceUUID);
                JsonNode jsonData = Json.toJson(data);

                result = callAction(
                        controllers.routes.ref.AuthController.register(),
                        fakeRequest().withJsonBody(jsonData));
                assertThat(status(result)).isEqualTo(BAD_REQUEST);

                userWithoutSecret.email = takenEmail;  // taken!

                result = callAction(
                        controllers.routes.ref.AuthController.register(),
                        fakeRequest().withJsonBody(Json.toJson(userWithoutSecret)));
                assertThat(status(result)).isEqualTo(BAD_REQUEST);
                assertThat(contentAsString(result)).contains("That email is already taken");

                userWithoutSecret.email = notTakenEmail;
                userWithoutSecret.firstName = "Wala";
                result = callAction(
                        controllers.routes.ref.AuthController.register(),
                        fakeRequest().withJsonBody(Json.toJson(userWithoutSecret)));
                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentAsString(result)).contains(notTakenEmail);

                User updatedUser = Json.fromJson(Json.parse(contentAsString(result)), User.class);
                assertThat(updatedUser).isNotNull();
                assertThat(updatedUser.secret).matches("[a-f0-9]{64}");

            }
        });
    }



    // Cant mock a file being uploaded with Java using play's built-in stuff.
//    @Test
//    public void testUpload() {
//        JPA.withTransaction(new play.libs.F.Callback0() {
//            @Override
//            public void invoke() throws Throwable {
//
//                String testDevice = "sdasqf4sdsdsdf";
//                User userWithSecret = UserManager.byDevice(testDevice);
//                File projectRoot = play.Play.application().path();
//                System.out.println(projectRoot.getAbsolutePath());
//                File testFile = new File(projectRoot, "");
//
//                // This needs to be done with javascript
//                final Http.MultipartFormData.FilePart filePart = new Http.MultipartFormData.FilePart("recording", "test-recording.amr", "audio/AMR", testFile);
//                Http.MultipartFormData formData = new Http.MultipartFormData() {
//                    @Override
//                    public Map<String, String[]> asFormUrlEncoded() {
//                        return null;
//                    }
//
//                    @Override
//                    public List<FilePart> getFiles() {
//                        List<FilePart> fileParts = new ArrayList<FilePart>();
//                        fileParts.add(filePart);
//                        return fileParts;
//                    }
//                };
////                val formData = MultipartFormData(dataParts = Map(), files = Seq(part), badParts = Seq(), missingFileParts = Seq())
////                val result = routeAndCall(FakeRequest(POST, "/path/to/test", FakeHeaders(), formData))
//                Result result = callAction(
//                        controllers.routes.ref.MainController.upload(),
//                        fakeRequest().);
//                assertThat(status(result)).isEqualTo(BAD_REQUEST);
//                assertThat(contentAsString(result)).contains("That email is already taken");
//                System.out.println(contentAsString(result));
//
//            }
//        });
//    }

}
