package controllers;

import auth.models.User;
import auth.UserManager;
import auth.actions.*;
import play.Logger;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

/**
 * Created by swami on 17/04/14.
 */
@Transactional
@With(CorsAction.class)
public class AuthController extends Controller {

    /**
     * First time register. Creates user and generates an apiKey.
     * @return
     */
    public static Result sayHi() {
        Logger.debug("Got {}-request: {}", request().method(), request());

        Form<User> userForm = Form.form(User.class).bindFromRequest();
        if (!userForm.hasErrors()) {
            User user = userForm.get();
            user = UserManager.create(user);
            return ok(Json.toJson(user));
        }
        return badRequest(userForm.errorsAsJson());
    }

    /**
     * Full register. Email required. Updates user and generates secret.
     * @return
     */
    public static Result register() {
        Form<User> userForm = Form.form(User.class).bindFromRequest();
        if (!userForm.hasErrors()) {
            User user = userForm.get();
            user = UserManager.register(user);
            return ok(Json.toJson(user));
        }
        return badRequest(userForm.errorsAsJson());
    }

}
