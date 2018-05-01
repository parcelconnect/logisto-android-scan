package ie.fastway.scansort.shipment.repo

import ie.fastway.scansort.api.endpoint.shipment.ShipmentCatalogApi
import ie.fastway.scansort.api.endpoint.shipment.ShipmentCatalogResponseJson
import ie.fastway.scansort.api.endpoint.shipment.ShipmentJsonConverter
import ie.fastway.scansort.lifecycle.AppSessionProvider
import ie.fastway.scansort.lifecycle.EventPublisher
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.shipment.Shipment
import ie.fastway.scansort.views.networkinfo.UserWaitingEventUpdate
import ie.logistio.equinox.Equinox
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * Imports data retrieved from the [ie.fastway.scansort.api.endpoint.shipment.ShipmentCatalogApi]
 * into the SQLite database on the device.
 *
 */
class ShipmentCatalogImporter(
        private val shipmentRepo: ShipmentRepo,
        private val eventPublisher: EventPublisher,
        private val shipmentApi: ShipmentCatalogApi
) {

    private val shipmentConverter = ShipmentJsonConverter()

    init {

    }

    fun startShipmentImportScheduler() {
        AppSessionProvider.runOnBackgroundThread(this::executeImportShipmentData)
    }

    private fun executeImportShipmentData() {
        postEvent("Downloading shipments…")

        shipmentRepo.findLatestShipment {
            val latestUpdatedAt = it?.updatedAt ?: Equinox.epoch()
            val updatedAtCursor = Equinox.convertInstantToTimestamp(latestUpdatedAt)

            if (LogConfig.SHIPMENT_API) {
                Timber.d("Getting Shipment catalog from API; cursor=$updatedAtCursor;")
            }

            shipmentApi.getShipmentCatalog(updatedAtCursor)
                    .enqueue(ShipmentApiCallback())
        }

    }

    private fun postEvent(message: String) {
        eventPublisher.postOnUiThread(UserWaitingEventUpdate(
                taskId = "ShipmentCatalogImporter",
                userMessage = message
        ))
    }

    /**
     * Run the process of converting the API response to JSON and importing into the SQLite DB.
     */
    private fun convertAndImport(responseData: ShipmentCatalogResponseJson.BodyData) {
        Schedulers.io().scheduleDirect { executeConvertAndImport(responseData) }
    }

    private fun executeConvertAndImport(responseData: ShipmentCatalogResponseJson.BodyData) {
        val convertedShipments = shipmentConverter.convertAll(responseData.shipments)

        if (LogConfig.SHIPMENTS) {
            Timber.d("convertAndImport; convertedShipments.size=${convertedShipments.size}")
        }

        import(convertedShipments)

        if (LogConfig.SHIPMENT_API) {
            Timber.d("Finished importing ${convertedShipments.size} shipments in ShipmentRepo")
        }

        scheduleImportDataTask(responseData.isPaginated)
    }

    /**
     * Schedules the next download check-in.
     */
    private fun scheduleImportDataTask(scheduleImmediately: Boolean = false) {
        if (LogConfig.SHIPMENT_API) {
            Timber.d("Scheduling importShipmentData for 60 seconds from now.")
        }

        val waitTimeMs = if (scheduleImmediately) 100L else 30 * 1000L

        AppSessionProvider.createSingleTaskScheduler(waitTimeMs)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { executeImportShipmentData() }
    }


    fun import(shipments: Collection<Shipment>) {
        shipmentRepo.storeAll(shipments)
    }

    inner class ShipmentApiCallback : Callback<ShipmentCatalogResponseJson> {

        override fun onResponse(
                call: Call<ShipmentCatalogResponseJson>,
                response: Response<ShipmentCatalogResponseJson>) {

            if (response.isSuccessful) {
                if (LogConfig.SHIPMENTS) {
                    Timber.d("onResponse isSuccessful from ShipmentCatalogApi")
                }

                postEvent("Processing downloaded shipments…")

                val responseData = response.body()?.data!!
                convertAndImport(responseData)
            }
            else {
                if (LogConfig.SHIPMENTS) {
                    Timber.e("onResponse IS NOT SUCCESSFUL from ShipmentCatalogApi")
                }

                postEvent("Downloading shipments: Error")
                scheduleImportDataTask()
            }

        }

        override fun onFailure(call: Call<ShipmentCatalogResponseJson>?, t: Throwable?) {
            if (LogConfig.SHIPMENTS) {
                Timber.e(t, "ShipmentCatalogApi; onFailure;")
            }

            // TODO we should understand the type of failure that occured and
            // react to it more specifically.
            scheduleImportDataTask()

            postEvent("Downloading shipments failed")
        }

    }
}