package ie.fastway.scansort.shipment

import com.squareup.otto.Subscribe
import ie.fastway.scansort.api.endpoint.shipment.ShipmentCatalogApi
import ie.fastway.scansort.config.AppConfig
import ie.fastway.scansort.device.scanner.ScanEvent
import ie.fastway.scansort.device.scanner.ScanResult
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.scene.AppSessionContext
import ie.fastway.scansort.shipment.repo.MockShipmentRepo
import ie.fastway.scansort.shipment.repo.ShipmentCatalogImporter
import ie.fastway.scansort.shipment.repo.ShipmentRepo
import ie.fastway.scansort.shipment.repo.SqliteShipmentRepo
import ie.fastway.scansort.views.networkinfo.UserWaitingEventUpdate
import timber.log.Timber

/**
 *
 */
class ShipmentDataManager(
        private val appSessionContext: AppSessionContext
) {

    private val sqliteTaskId = "ShipmentDataManager:SqliteImport"
    private val eventBus = appSessionContext.eventBus
    private val shipmentRepo: ShipmentRepo
    private val shipmentImporter: ShipmentCatalogImporter

    init {
        shipmentRepo = if (AppConfig.USE_MOCK_SHIPMENT_REPO) {
            MockShipmentRepo()
        }
        else {
            val sqliteRepo = SqliteShipmentRepo(appSessionContext.activityContext)
            sqliteRepo.purgeExpiredData()
            sqliteRepo
        }
        shipmentRepo.setStorageProgressCallback(this::onImportProgress)

        val shipmentApi = appSessionContext.apiService.createApi(ShipmentCatalogApi::class.java)
        shipmentImporter = ShipmentCatalogImporter(shipmentRepo, eventBus, shipmentApi)

    }

    fun start() {
        shipmentImporter.startShipmentImportScheduler()
    }

    @Subscribe
    fun onScanEvent(scanEvent: ScanEvent) {
        if (LogConfig.SHIPMENTS) {
            Timber.d("Received ScanEvent:$scanEvent")
        }

        shipmentRepo.findShipmentByBarcodeAsync(
                scanEvent.scannedValue, { _, shipment ->
            onTrackingNumberResponse(scanEvent, shipment)
        })
    }

    private fun onTrackingNumberResponse(
            scanEvent: ScanEvent, foundShipment: Shipment?) {

        if (LogConfig.SHIPMENTS) {
            Timber.d("onTrackingNumberResponse; foundShipment=$foundShipment")
        }

        val errorMessage = if (foundShipment == null) "UNKNOWN" else null

        val scanResult = ScanResult(scanEvent, foundShipment, errorMessage)

        eventBus.postOnUiThread(scanResult)
    }

    private fun onImportProgress(progress: Int, total: Int) {
        if (LogConfig.SHIPMENT_API) {
            Timber.v("onImportProgress; progress=$progress, total=$total")
        }

        if (progress >= total) {
            val event = UserWaitingEventUpdate.Factory.onEventFinished(sqliteTaskId)
            eventBus.postOnUiThread(event)
        }
        else if (progress < 5 || progress % 200 == 0) {
            val event = UserWaitingEventUpdate(
                    taskId = sqliteTaskId,
                    userMessage = "Importing shipment data",
                    progressValue = progress,
                    progressMax = total
            )
            eventBus.postOnUiThread(event)
        }
    }

}