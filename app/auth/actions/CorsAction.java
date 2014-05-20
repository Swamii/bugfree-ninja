package auth.actions;

import play.Logger;
import play.libs.F;
import play.mvc.*;
import play.mvc.SimpleResult;

/**
 * Created by swami on 21/04/14.
 */
public class CorsAction extends Action.Simple {

    @Override
    public F.Promise<SimpleResult> call(Http.Context context) throws Throwable {
        Http.Response response = context.response();
        response.setHeader("Access-Control-Allow-Origin", "*");
        Logger.debug("Request received: {}, method: {}", context.request(), context.request().method());
        //Handle preflight requests
        if(context.request().method().equalsIgnoreCase("OPTIONS")) {
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Auth-Token");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            Logger.debug("Preflight options checked, returning OK");
            return F.Promise.pure((SimpleResult) ok());
        }

//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
//        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Auth-Token");
//        response.setHeader("Access-Control-Allow-Headers","X-Requested-With, Content-Type, X-Auth-Token");
        return delegate.call(context);
    }

}
