package ie.fastway.scansort.api.endpoint.shipment;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.Instant;

import ie.fastway.scansort.api.ApiJson;

import static ie.fastway.scansort.api.FastwayApi.Const.ADDRESS;
import static ie.fastway.scansort.api.FastwayApi.Const.BARCODE;
import static ie.fastway.scansort.api.FastwayApi.Const.COURIER_FRANCHISEE;
import static ie.fastway.scansort.api.FastwayApi.Const.ID;
import static ie.fastway.scansort.api.FastwayApi.Const.REGIONAL_FRANCHISEE;
import static ie.fastway.scansort.api.FastwayApi.Const.TRACKING_NUMBER;
import static ie.fastway.scansort.api.FastwayApi.Const.UPDATED_AT;

/**
 *
 */
public class ShipmentJson implements ApiJson {

    @SerializedName(ID)
    public Long id;

    @SerializedName(TRACKING_NUMBER)
    public String trackingNumber;

    @SerializedName(BARCODE)
    public String barcode;

    @Nullable
    @SerializedName(ADDRESS)
    public String address;

    @Nullable
    @SerializedName(COURIER_FRANCHISEE)
    public String courierFranchisee;

    @Nullable
    @SerializedName(REGIONAL_FRANCHISEE)
    public String regionalFranchisee;

    @SerializedName(UPDATED_AT)
    public Instant updatedAt;

    @Override
    public String toString() {
        return "ShipmentJson{" +
                "id=" + id +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", address='" + address + '\'' +
                ", courierFranchisee='" + courierFranchisee + '\'' +
                ", regionalFranchisee='" + regionalFranchisee + '\'' +
                ", address='" + address + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
