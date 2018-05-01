package ie.fastway.scansort.api.endpoint.scanevent;

import ie.fastway.scansort.api.FastwayApi;
import ie.logistio.paloma.ApiEndpoint;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * REST endpoint for posting the history of {@link ie.fastway.scansort.device.scanner.ScanEvent}s.
 */
@ApiEndpoint
public interface ScanEventLogApi {

    @POST(FastwayApi.Const.ENDPOINT_SCAN_EVENT_LOG)
    public Call<ScanEventResponseJson> postScanEvents(@Body ScanEventRequestJson body);

}
