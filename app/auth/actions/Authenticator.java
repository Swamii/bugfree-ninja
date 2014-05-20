package auth.actions;

import play.mvc.Http;
import play.mvc.Security;

/**
 * Created by swami on 21/04/14.
 */
public class Authenticator extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.request().getQueryString("apiKey");
    }
}
