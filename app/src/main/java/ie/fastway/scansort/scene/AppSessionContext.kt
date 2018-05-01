package ie.fastway.scansort.scene

import com.tubbert.powdroid.util.AndroidDevice
import ie.fastway.scansort.api.endpoint.auth.AuthApiMocker
import ie.fastway.scansort.api.endpoint.scanevent.ScanEventLogApiMocker
import ie.fastway.scansort.api.endpoint.shipment.ShipmentCatalogApiMocker
import ie.fastway.scansort.api.service.FastwayApiServiceBuilder
import ie.fastway.scansort.device.DeviceManager
import ie.fastway.scansort.device.mocking.UiButtonMockScanner
import ie.fastway.scansort.config.AppConfig
import ie.fastway.scansort.lifecycle.AppSessionProvider
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.shipment.ShipmentDataManager
import ie.logistio.paloma.ApiService
import ie.logistio.paloma.MockApiRegistry
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber


/**
 * Holds information about the user's session for the app.
 *
 * Essentially a bundle of data that defines the current state of the app.
 */
class AppSessionContext(val mainActivity: MainActivity) {

    val activityContext: MainActivity = mainActivity

    val eventBus = AppSessionProvider.eventPublisher

    val apiService: ApiService
    init {
        val apiServiceBuilder = FastwayApiServiceBuilder()
                .getBasicApiServiceBuilder()

        if (AppConfig.USE_MOCK_API) {
            val shipmentApiMocker = ShipmentCatalogApiMocker()
            UiButtonMockScanner.setTrackingNumberProvider(shipmentApiMocker)

            val mockerRegistry = MockApiRegistry.createWith(
                    AuthApiMocker(),
                    shipmentApiMocker,
                    ScanEventLogApiMocker()
            )
            apiServiceBuilder.setMockerRegistry(mockerRegistry)
        }

        if (LogConfig.RETROFIT) {

            apiServiceBuilder.setupVerboseLogger({ Timber.d(it) });

            // Add verbose logging to all requests:
            val loggingInterceptor = HttpLoggingInterceptor(
                    HttpLoggingInterceptor.Logger { message ->
                        Timber.d("OkHttp:" + message)
                    })

            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            apiServiceBuilder.setLogger(loggingInterceptor)
        }

        apiService = apiServiceBuilder.buildApiService()

    }

    val shipmentDataManager = ShipmentDataManager(this)

    val deviceManager = DeviceManager(activityContext, eventBus)
    init {
        eventBus.register(deviceManager)
    }

    val deviceInfoFinder = AndroidDevice.Investigator(mainActivity)

}