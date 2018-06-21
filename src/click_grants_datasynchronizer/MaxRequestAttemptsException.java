package click_grants_datasynchronizer;

/**
 *
 * @author Glenn
 */
public class MaxRequestAttemptsException extends Exception {

    public MaxRequestAttemptsException() {
        super("Max request attempts reached.");
    }
}