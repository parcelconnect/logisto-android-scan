package ie.fastway.scansort.logging;

/**
 * This class allows us to easily remove logger calls from production builds using ProGuard.
 * <p>
 * Log code that is places inside any final static boolean condition which evaluates to FALSE
 * will be left out by the compiler when building with ProGuard, so we can easily strip
 * logger messages from the app selectively by changing the hardcoded values here.
 */
public class LogConfig {

    public static final boolean ERROR = true;

    public static final boolean DEBUG = true && ERROR;

    public static final boolean LOGENTRIES = true;

    /**
     * A marker for log messages that are particularly verbose, and are
     * only used when developing a particular feature.
     * These logs should always be deleted once the feature they relate to is
     * production-ready.
     */
    public static final boolean TEMP = true && DEBUG;

    public static final boolean AUTH = false && DEBUG;

    public static final boolean APP_SESSION = true && DEBUG;

    public static final boolean SCENES = true && DEBUG;

    public static final boolean API = true && DEBUG;

    public static final boolean RETROFIT = true && API;

    public static final boolean RX = false && DEBUG;

    public static final boolean THREADS = false && APP_SESSION;

    public static final boolean BLUETOOTH = true && DEBUG;

    public static final boolean BLUETOOTH_ALL_DEVICES = true && BLUETOOTH;

    public static final boolean SCANNING_DEVICES = true && DEBUG;

    public static final boolean SCANNER_CONFIG = true && SCANNING_DEVICES;

    public static final boolean NETWORKING_EVENT = false && DEBUG;

    /**
     * AveryDennison scanner logs.
     */
    public static final boolean AVD_SCANNER = true && SCANNING_DEVICES;

    public static final boolean AVD_PRINTER = AVD_SCANNER;

    /**
     * Logs errors in the Pathfinder configuration.
     */
    public static final boolean AVD_CONFIG = true && ERROR;

    public static final boolean KEYBOARD_SCANNER = true && DEBUG;

    /**
     * Symbol scanner logs.
     */
    public static final boolean SYMBOL = true && SCANNING_DEVICES;

    public static final boolean SCAN_EVENT = false && SCANNING_DEVICES;

    public static final boolean SCAN_EVENT_API = SCAN_EVENT || API;

    public static final boolean SHIPMENTS = true && DEBUG;

    public static final boolean SHIPMENT_DB = true && SHIPMENTS;

    public static final boolean SHIPMENT_API = false && SHIPMENTS && RETROFIT;

    public static final boolean SHIPMENT_MOCKER = false && DEBUG;

    public static final boolean EVENT_BUS = false && DEBUG;

}
