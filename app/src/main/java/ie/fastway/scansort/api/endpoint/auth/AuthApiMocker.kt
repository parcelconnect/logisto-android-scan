package ie.fastway.scansort.api.endpoint.auth

import ie.fastway.scansort.logging.LogConfig
import ie.logistio.equinox.Equinox
import ie.logistio.paloma.mock.ApiServiceMocker
import retrofit2.Call
import timber.log.Timber

/**
 *
 */
class AuthApiMocker : ApiServiceMocker<AuthApi>() {

    init {
        if (LogConfig.AUTH) {
            Timber.d("Creating new AuthApiMocker")
        }

        // networkEmulator.networkDelayMillis = 200L
    }

    override fun createMockApi(): AuthApi {

        return object : AuthApi {

            override fun getAuthToken(requestJson: AuthRequestJson?): Call<AuthResponseJson> {

                val responseJson = AuthResponseJson()
                responseJson.data = AuthTokenJson()
                with(responseJson.data) {
                    tokenType = "Bearer"
                    accessToken = "ACCESS_TOKEN"
                    accessTokenExpiresIn = Equinox.UnitConverter.daysToSeconds(60)
                }

                if(LogConfig.AUTH) {
                    Timber.d("Created mock AuthResponseJson:$requestJson")
                }

                return createApiCall(responseJson)
            }
        }

    }

    override fun getType(): Class<AuthApi> {
        return AuthApi::class.java
    }
}