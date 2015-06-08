package nl.gridshore.dwes.index;

/**
 * Exception thrown when the index creator was mis configured.
 */
public class IndexCreatorConfigException extends RuntimeException {
    public IndexCreatorConfigException(String message) {
        super(message);
    }
}
