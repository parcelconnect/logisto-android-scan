package ie.fastway.scansort.api.endpoint.shipment;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ie.fastway.scansort.api.FastwayApi;
import ie.logistio.paloma.json.DataJson;

/**
 *
 */
public class ShipmentCatalogResponseJson
        extends DataJson<ShipmentCatalogResponseJson.BodyData> {

    public static ShipmentCatalogResponseJson createEmpty() {
        BodyData data = new BodyData();
        ShipmentCatalogResponseJson json = new ShipmentCatalogResponseJson();
        json.data = data;
        return json;
    }

    public static class BodyData {

        @SerializedName(FastwayApi.Const.CURSOR_DATETIME)
        public String cursorDatetime;

        @SerializedName(FastwayApi.Const.TOTAL_RETRIEVABLE)
        public int totalRetrievable;

        @SerializedName(FastwayApi.Const.TOTAL_RETRIEVED)
        public int totalRetrieved;

        @SerializedName(FastwayApi.Const.IS_PAGINATED)
        public boolean isPaginated;

        @SerializedName(FastwayApi.Const.SHIPMENT_CATALOG_JSON)
        public List<ShipmentJson> shipments = new ArrayList<>();
    }

}
