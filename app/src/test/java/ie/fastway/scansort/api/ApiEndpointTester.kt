package ie.fastway.scansort.api

import com.google.gson.Gson
import ie.fastway.scansort.api.service.FastwayApiServiceBuilder
import ie.logistio.paloma.ApiService
import java.util.concurrent.CountDownLatch

/**
 *
 */
class ApiEndpointTester {

    private val apiServiceBuilder = FastwayApiServiceBuilder().getBasicApiServiceBuilder()

    val countdownLatch = CountDownLatch(1)

    val apiService: ApiService = apiServiceBuilder.buildApiService()
    val gson: Gson = apiServiceBuilder.buildDefaultGson()

}
