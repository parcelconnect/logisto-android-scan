package ie.fastway.scansort.api.endpoint.scanevent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;

import ie.fastway.scansort.api.ApiJson;

import static ie.fastway.scansort.api.FastwayApi.Const.BARCODE_TYPE;
import static ie.fastway.scansort.api.FastwayApi.Const.BARCODE_VALUE;
import static ie.fastway.scansort.api.FastwayApi.Const.DEVICE_SCANNER_SERIAL_NUMBER;
import static ie.fastway.scansort.api.FastwayApi.Const.EVENT_AT;
import static ie.fastway.scansort.api.FastwayApi.Const.RESULT;
import static ie.fastway.scansort.api.FastwayApi.Const.SCAN_EVENTS;
import static ie.fastway.scansort.api.FastwayApi.Const.SHIPMENT_ID;

/**
 *
 */
public class ScanEventRequestJson implements ApiJson {

    @NonNull
    @SerializedName(SCAN_EVENTS)
    public List<ScanEventLogJson> scanEvents = new ArrayList<ScanEventLogJson>();

    static class ScanEventLogJson {

        @SerializedName(EVENT_AT)
        public String eventAt;

        @SerializedName(RESULT)
        public ResultJson result;

        @SerializedName(DEVICE_SCANNER_SERIAL_NUMBER)
        public String deviceScannerSerialNumber;

        @Nullable
        @SerializedName(SHIPMENT_ID)
        public Long shipmentId;

        @SerializedName(BARCODE_VALUE)
        public String barcodeValue;

        @SerializedName(BARCODE_TYPE)
        public String barcodeType;

    }

    /**
     * The treats the "result" field as JSON if it is present, but it
     * supports any JSON schema, so we can add more data here if we choose to.
     */
    static final class ResultJson {
        @Nullable
        @SerializedName("error_message")
        public String errorMessage;
    }

}
