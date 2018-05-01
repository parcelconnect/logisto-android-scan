package ie.fastway.scansort.api.service

import ie.fastway.scansort.api.FastwayApi
import ie.fastway.scansort.api.converter.ApiDateTimeConverter
import ie.logistio.paloma.ApiServiceBuilder

/**
 * Builds the basic [ApiServiceBuilder] for interacting with the [FastwayApi].
 *
 * This should be used in tests when an [ApiServiceBuilder] is required that
 * mimics the configuration used by the app, since this class provides the
 * ApiService that is used by the app session.
 */
class FastwayApiServiceBuilder {

    fun getBasicApiServiceBuilder() = ApiServiceBuilder()
            .setBaseUrl(FastwayApi.getBaseUrl())
            .setTimeConverter(ApiDateTimeConverter())


}