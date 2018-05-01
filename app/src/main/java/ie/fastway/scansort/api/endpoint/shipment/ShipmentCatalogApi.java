package ie.fastway.scansort.api.endpoint.shipment;

import android.support.annotation.Nullable;

import ie.fastway.scansort.api.FastwayApi;
import ie.logistio.paloma.ApiEndpoint;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * REST endpoint for getting the available Shipment data.
 */
@ApiEndpoint
public interface ShipmentCatalogApi {

    @GET(FastwayApi.Const.ENDPOINT_SHIPMENT_CATALOG)
    public Call<ShipmentCatalogResponseJson> getShipmentCatalog(
            @Query(FastwayApi.Const.LAST_UPDATED_AT) @Nullable String lastUpdatedAt
    );

}
