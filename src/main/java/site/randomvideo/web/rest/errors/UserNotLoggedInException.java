package site.randomvideo.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException() {
        super("User not logged in.");
    }
}
