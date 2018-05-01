package ie.fastway.scansort.shipment.repo

import com.tubbert.powdroid.mocking.Fakist
import ie.fastway.scansort.lifecycle.AppSessionProvider
import ie.fastway.scansort.mocking.ShipmentMocker
import ie.fastway.scansort.shipment.Shipment
import io.reactivex.schedulers.Schedulers

/**
 *
 */
class MockShipmentRepo : ShipmentRepo {

    private val storedShipments = HashMap<String, Shipment>();
    private val fakist = Fakist()
    private var shipmentMocker = ShipmentMocker()
    private var progressWatcher: StorageProgressWatcher? = null

    override fun storeAll(shipmentsToStore: Collection<Shipment>) {
        storedShipments.putAll(shipmentsToStore.map { Pair(it.trackingNumber, it) })

        progressWatcher?.invoke(shipmentsToStore.size, shipmentsToStore.size)
    }

    //----------------------------------------------------------------------------------------------

    override fun findShipmentByBarcodeAsync(barcode: String, callback: (String, Shipment?) -> Unit) {
        AppSessionProvider.createSingleTaskScheduler(80L, Schedulers.io(), Schedulers.io())
                .subscribe { callback(barcode, createShipmentForTrackingNumber(barcode)) }
    }

    private fun createShipmentForTrackingNumber(trackingNumber: String): Shipment? {
        return shipmentMocker.createShipment()
                .copy(trackingNumber = trackingNumber)
    }

    //----------------------------------------------------------------------------------------------

    override fun findLatestShipment(callback: (Shipment?) -> Unit) {
        AppSessionProvider.createSingleTaskScheduler(80L, Schedulers.io(), Schedulers.io())
                .subscribe { callback(createLatestShipment()) }
    }

    private fun createLatestShipment(): Shipment {
        return shipmentMocker.createShipment()
    }

    //----------------------------------------------------------------------------------------------

    override fun setStorageProgressCallback(watcher: StorageProgressWatcher) {
        progressWatcher = watcher
    }
}