package auth.actions;

import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

import java.util.Map;

/**
 * TODO: Implement.
 */
public class ValidateSignedRequest extends Action<SimpleResult> {

    @Override
    public F.Promise<SimpleResult> call(Http.Context ctx) throws Throwable {

        JsonNode query = ctx.request().body().asJson();
        Logger.debug("Received 'signedrequest': {}", query);

        String s = "Lala";
        if (s.equals("wola")) {
            return F.Promise.pure((SimpleResult) forbidden());
        }
        return delegate.call(ctx);
    }

}
