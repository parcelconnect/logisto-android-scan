package ie.fastway.scansort.api.endpoint.scanevent

import ie.logistio.paloma.mock.ApiServiceMocker
import retrofit2.Call

/**
 *
 */
class ScanEventLogApiMocker : ApiServiceMocker<ScanEventLogApi>() {

    var lastRequest: ScanEventRequestJson? = null

    override fun createMockApi(): ScanEventLogApi {

        return object : ScanEventLogApi {
            override fun postScanEvents(body: ScanEventRequestJson): Call<ScanEventResponseJson> {

                lastRequest = body

                val responseJson = ScanEventResponseJson()
                return createApiCall(responseJson)
            }
        }
    }

    override fun clearRequestHistory() {
        super.clearRequestHistory()
        lastRequest = null
    }

    override fun getType(): Class<ScanEventLogApi> = ScanEventLogApi::class.java
}