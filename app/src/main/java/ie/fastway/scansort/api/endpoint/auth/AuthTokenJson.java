package ie.fastway.scansort.api.endpoint.auth;

import com.google.gson.annotations.SerializedName;

import ie.fastway.scansort.api.ApiJson;

import static ie.fastway.scansort.api.FastwayApi.Const.*;

/**
 *
 */
public class AuthTokenJson implements ApiJson {

    @SerializedName(TOKEN_TYPE)
    public String tokenType;

    @SerializedName(ACCESS_TOKEN)
    public String accessToken;

    @SerializedName(ACCESS_TOKEN_EXPIRES_IN)
    public long accessTokenExpiresIn;

    @Override
    public String toString() {
        return "AuthTokenJson{" +
                "tokenType='" + tokenType + '\'' +
                "accessToken='" + accessToken + '\'' +
                ", accessTokenExpiresIn=" + accessTokenExpiresIn +
                '}';
    }
}
