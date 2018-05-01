package ie.fastway.scansort.api.endpoint.auth;

import ie.fastway.scansort.api.FastwayApi;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit-2 API for getting an Auth token to use with the app.
 */
public interface AuthApi {

    @POST(FastwayApi.Const.ENDPOINT_AUTH)
    Call<AuthResponseJson> getAuthToken(@Body AuthRequestJson requestJson);

}
