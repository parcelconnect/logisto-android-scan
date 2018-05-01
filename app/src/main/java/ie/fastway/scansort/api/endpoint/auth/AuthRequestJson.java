package ie.fastway.scansort.api.endpoint.auth;

import com.google.gson.annotations.SerializedName;

import ie.fastway.scansort.api.ApiJson;
import ie.fastway.scansort.api.FastwayApi;

/**
 * GSON class for Request to Auth endpoint.
 */
public class AuthRequestJson implements ApiJson {

    @SerializedName(FastwayApi.Const.CLIENT_CREDENTIALS)
    public ClientCredentialsJson clientCredentials;

    @SerializedName(FastwayApi.Const.ANDROID_DEVICE)
    public DeviceInfoJson deviceInfo = DeviceInfoJson.createForUnknown();

    public static class ClientCredentialsJson implements ApiJson {

        @SerializedName(FastwayApi.Const.CLIENT_ID)
        public String clientId;

        @SerializedName(FastwayApi.Const.CLIENT_SECRET)
        public String clientSecret;

    }

}
