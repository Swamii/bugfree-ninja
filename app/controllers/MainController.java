package controllers;

import ad.AdManager;
import ad.dto.AdSubmission;
import ad.models.Ad;
import auth.actions.*;
import org.apache.http.client.ClientProtocolException;
import play.Logger;
import play.Play;
import play.Routes;
import play.api.mvc.AnyContent;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData.FilePart;
import wikilocation.WikiMediaApi;
import wikilocation.managers.LocationManager;
import wikilocation.models.BaseLocation;
import wikilocation.models.WikiArticle;
import wikilocation.pojos.Position;

import java.io.File;
import java.util.List;

@Transactional
@With(CorsAction.class)
public class MainController extends Controller {

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result wiki(Double lat, Double lng, Integer limit) {
        if (lat == null || lng == null) {
            return badRequest("Latitude and longitude is required!");
        }

        Position pos = new Position(lat, lng);
        List<BaseLocation> locations = LocationManager.filter(pos, limit);
        return ok(Json.toJson(locations));
    }

    @SignedRequestRequired
    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        List<FilePart> files =  body.getFiles();
        if (files.size() > 0) {  // there should only be one file
            FilePart filePart = files.get(0);
            String fileName = filePart.getFilename();
            File file = filePart.getFile();
            String dest = Play.application().configuration().getString("upload.path");
            file.renameTo(new File(dest + fileName));
            return ok("File Saved to Master Server");
        }
        return badRequest("No files...");
    }

    @SignedRequestRequired
    public static Result submitAdForm() {
        Form<AdSubmission> subForm = Form.form(AdSubmission.class).bindFromRequest();
        if (!subForm.hasErrors()) {
            AdSubmission sub = subForm.get();
            Ad ad = AdManager.create(sub);

            return ok(Json.toJson(ad));
        }

        return badRequest(subForm.errorsAsJson());
    }

    public static Result wikiInfo(Long articleId, String locale) {
        Logger.debug("Hello WikiInfo id: {}, locale: {}", articleId, locale);
        WikiArticle article = LocationManager.get(articleId, locale);
        return ok(Json.toJson(article));
    }

    public static Result checkPreFlight(String path) {
        Logger.debug("checking options...");
        return ok();
    }

    private static AssetsBuilder delegate = new AssetsBuilder();

    public static play.api.mvc.Action<AnyContent> asset(String file) {
        String path = "uploads/";
        return delegate.at(path, file);
    }

}
