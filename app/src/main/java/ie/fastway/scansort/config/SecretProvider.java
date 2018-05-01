package ie.fastway.scansort.config;

/**
 * Provides secrets and credentials used by the app.
 * <p>
 * There is an argument to be made for keeping these individually in the places where
 * they are used, but keeping them here makes them all in one class makes them easier
 * to swap out with different product flavours and app releases.
 */
public class SecretProvider {

    /**
     * Logentries API token.
     * ----
     * Token: "64883839-7ff2-4b89-a77b-eed6f0e72f07"
     * Created On: 2017-11-29
     * Account: fastway@logistio.ie
     * Log Name: "ScanSort-v0.4.0"
     */
    public static final String LOGENTRIES_ACCESS_TOKEN = "64883839-7ff2-4b89-a77b-eed6f0e72f07";


}
