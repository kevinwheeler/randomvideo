package site.randomvideo.web.rest.errors;

public class XUserNotFoundException extends RuntimeException {
    public XUserNotFoundException(Long userId) {
        super("XUser not found for user id: " + userId);
    }
}
