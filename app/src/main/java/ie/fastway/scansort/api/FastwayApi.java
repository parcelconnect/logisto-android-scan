package ie.fastway.scansort.api;

/**
 *
 */
public class FastwayApi {

    private static final ApiConfig API_CONFIG = ConfigFactory.STAGING;

    public static String getBaseUrl() {
        return API_CONFIG.getDomainUrl() + "/" + Const.API_V1;
    }

    public static String getClientId() {
        return API_CONFIG.getClientId();
    }

    public static String getClientSecret() {
        return API_CONFIG.getClientSecret();
    }

    //----------------------------------------------------------------------------------------------

    private static class ConfigFactory {
        private static final ApiConfig DEVELOPMENT = new ApiConfig(
                "http://192.168.0.175:8085",
                "1",
                "FTgcjp9MYpqqQklDjXCeZ8yjf8caXo6nGpY6WR7R"
        );

        private static final ApiConfig PRODUCTION = new ApiConfig(
                "https://fastway-platform.logistio.ie",
                "1",
                "VIl7EtdKeG6ccZEztEK01b3973rswSUVdkmvD3mv"
        );

        private static final ApiConfig STAGING = new ApiConfig(
                "https://fastway-platform-staging.logistio.ie",
                "1",
                "2LRF1CBuX05qiVJf4VRSnIhgR9eYdBAzt3p8vjXY"
        );
    }

    /**
     * Holds constants used by {@link FastwayApi}.
     */
    public static class Const {

        public static final String API_V1 = "api/scan-sort-client/v1/";

        public static final String ENDPOINT_AUTH = "auth/token";
        public static final String ENDPOINT_SHIPMENT_CATALOG = "shipment/catalog";
        public static final String ENDPOINT_SCAN_EVENT_LOG = "scan-sort-log";
        public static final String ENDPOINT_DEVICE_SCANNER = "device-scanner";

        // AUTH ----
        public static final String DATA = "data";
        public static final String AUTH = "auth";
        public static final String TOKEN_TYPE = "token_type";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String ACCESS_TOKEN_EXPIRES_IN = "expires_in";

        public static final String CLIENT_ID = "client_id";
        public static final String CLIENT_SECRET = "client_secret";

        public static final String CLIENT_CREDENTIALS = "client_credentials";

        // DEVICE DATA ----
        public static final String ANDROID_DEVICE = "android_device";
        public static final String APP_VERSION_INT = "app_version";
        public static final String APP_VERSION_NAME = "app_version_name";
        public static final String IMEI = "imei";
        public static final String SIM_SERIAL = "sim_serial";
        public static final String ANDROID_SDK = "android_sdk";

        // SHIPMENTS ----
        public static final String ID = "id";
        public static final String TRACKING_NUMBER = "tracking_number";
        public static final String BARCODE = "barcode";
        public static final String COURIER_ID = "courier_id";
        public static final String PLACE_ID = "place_id";
        public static final String ADDRESS = "address";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String COURIER_FRANCHISEE = "courier_franchisee";
        public static final String REGIONAL_FRANCHISEE = "regional_franchisee";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";

        // SHIPMENT CATALOG ----
        public static final String LAST_UPDATED_AT = "last_updated_at";
        public static final String CURSOR_DATETIME = "cursor_datetime";
        public static final String TOTAL_RETRIEVABLE = "total_retrievable";
        public static final String TOTAL_RETRIEVED = "total_retrieved";
        public static final String IS_PAGINATED = "is_paginated";
        public static final String SHIPMENT_CATALOG_JSON = "shipments";

        // SCAN EVENT LOG ----
        public static final String SCAN_EVENTS = "scan_events";
        public static final String EVENT_AT = "event_at";
        public static final String DEVICE_SCANNER_SERIAL_NUMBER = "device_scanner_serial_number";
        public static final String RESULT = "result";
        public static final String SHIPMENT_ID = "shipment_id";
        public static final String BARCODE_VALUE = "barcode_value";
        public static final String BARCODE_TYPE = "barcode_type";



    }
}
