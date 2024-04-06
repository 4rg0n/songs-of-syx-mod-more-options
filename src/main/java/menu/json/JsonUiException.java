package menu.json;

public class JsonUiException extends RuntimeException {
    public JsonUiException() {
    }

    public JsonUiException(String message) {
        super(message);
    }

    public JsonUiException(String message, Throwable cause) {
        super(message, cause);
    }
}
