package auth.actions;

import play.mvc.Security;
import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Not implemented. TODO
 */

@Security.Authenticated(Authenticator.class)
@With(ValidateSignedRequest.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SignedRequestRequired {}
