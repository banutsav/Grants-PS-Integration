package click_grants_datasynchronizer;

/**
 *
 * @author Glenn
 */
public class ResponseCodeException extends Exception {

    public ResponseCodeException(int responseCode) {
        super("Got response code: " + responseCode);
    }
}