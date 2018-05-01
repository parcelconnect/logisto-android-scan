package ie.fastway.scansort.config;

/**
 * Constant values that act as switches for turning on developer tools.
 * <p>
 * Since the values here are boolean constants the compiler will strip
 * out `if` conditions involving them that evalute to false.
 */
public class AppConfig {


    private static final boolean ALLOW_MOCK_MODE = false;

    public static final boolean USE_MOCK_API = true && ALLOW_MOCK_MODE;

    public static final boolean USE_MOCK_BLUETOOTH = false && ALLOW_MOCK_MODE;

    public static final boolean USE_MOCK_SHIPMENT_REPO = false && ALLOW_MOCK_MODE;

}
