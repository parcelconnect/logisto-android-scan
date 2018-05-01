package ie.fastway.scansort.api.endpoint.auth

import ie.fastway.scansort.api.FastwayApi
import ie.fastway.scansort.lifecycle.AppSessionEvent
import ie.fastway.scansort.lifecycle.SessionEventListener
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.scene.AppSessionContext
import ie.logistio.equinox.Equinox
import ie.logistio.paloma.json.AuthCredentials
import io.reactivex.disposables.Disposable
import org.threeten.bp.Instant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


/**
 * Sets up the auth access token which is required in order
 * to interact with the [ie.fastway.scansort.api.FastwayApi].
 *
 * Responsible for completing the Auth process so that there is a valid AuthCredentials
 * available in the app session.
 *
 */
class AuthSetupManager(
        private val authApi: AuthApi,
        private val appSessionContext: AppSessionContext
) {

    private var pendingRequest: Disposable? = null
    var sessionEventListener: SessionEventListener? = null

    /**
     * Gets an Auth access token via the [AuthApi]
     */
    fun ensureAccessTokenIsAvailable() {

        val requestJson = createAuthRequest()

        // Discard any outstanding observer if available.
        pendingRequest?.dispose()

        authApi.getAuthToken(requestJson)
                .enqueue(ResponseSubscriber())
    }

    private fun createAuthRequest(): AuthRequestJson {
        val requestJson = AuthRequestJson()

        val credentials = AuthRequestJson.ClientCredentialsJson()
        credentials.clientId = FastwayApi.getClientId()
        credentials.clientSecret = FastwayApi.getClientSecret()

        requestJson.clientCredentials = credentials

        with(appSessionContext.deviceInfoFinder) {
            requestJson.deviceInfo = DeviceInfoJson.create(getDeviceInfo())
            requestJson.deviceInfo.appVersionName = getAppVersionName()
            requestJson.deviceInfo.appVersionInt = getAppVersionInt().toString()
        }

        return requestJson
    }

    private fun onSuccessfulResponse(responseJson: AuthResponseJson) {
        if (LogConfig.AUTH) {
            Timber.d("onSuccessfulResponse:$responseJson")
        }

        val authJson = responseJson.data!!

        val accessTokenExpiresOn = Equinox.now().plusSeconds(authJson.accessTokenExpiresIn)

        val auth = AuthCredentials(
                tokenType = authJson.tokenType,
                accessToken = authJson.accessToken,
                accessTokenExpiresOn = accessTokenExpiresOn
        )

        appSessionContext.apiService.setupAuth(auth)

        val event = AppSessionEvent(AppSessionEvent.EventBundle
                .AuthTokenAvailable(auth), Instant.now())

        sessionEventListener?.onAppSessionEvent(event)

    }

    inner class ResponseSubscriber : Callback<AuthResponseJson> {


        override fun onResponse(call: Call<AuthResponseJson>, response: Response<AuthResponseJson>) {

            if (LogConfig.AUTH) {
                Timber.d("AuthResponse; onResponse: $response")
            }

            if (response.isSuccessful) {
                onSuccessfulResponse(response.body()!!)
            }
            else {
                // TODO: Handle unsuccessful response.
                // This error occurs when the server is down, or the auth credentials are invalid.
                if (LogConfig.AUTH) {
                    Timber.e("Auth ResponseSubscriber; Result is not error, but response is not successful")
                }
            }

        }


        override fun onFailure(call: Call<AuthResponseJson>, error: Throwable) {
            if (LogConfig.AUTH) {
                Timber.e(error, "AuthApi response error.");
            }

            // TODO: Handle API error. Retry the request if Network failure.
            // This error only occurs when the network fails (IOException)
            // or when there is a serialisation problem.
            // This does not indicate that the server responded with a non-success code.


        }

    }

}