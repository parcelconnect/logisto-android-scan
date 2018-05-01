package ie.fastway.scansort.shipment.repo

import ie.fastway.scansort.shipment.Shipment
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Instant

/**
 *
 */
class InMemoryShipmentRepo : ShipmentRepo {

    private val shipmentMap: MutableMap<String, Shipment> = HashMap()
    private var latestShipment: Shipment? = null

    override fun storeAll(shipmentsToStore: Collection<Shipment>) {

        // We add the elements to a smaller HashMap so that the big HashMap only has to
        // resize itself once. The performance enhancement of this is untested.

        val trackingNumberMap = HashMap<String, Shipment>()
        shipmentsToStore.forEach {
            trackingNumberMap.put(it.trackingNumber, it)
            if (it.updatedAt > (latestShipment?.updatedAt ?: Instant.EPOCH)) {
                latestShipment = it
            }
        }

        shipmentMap.putAll(trackingNumberMap)
    }

    override fun findShipmentByBarcodeAsync(
            barcode: String, callback: (String, Shipment?) -> Unit) {
        Schedulers.io().scheduleDirect {
            callback(barcode, shipmentMap[barcode])
        }
    }

    override fun findLatestShipment(callback: (Shipment?) -> Unit) {
        Schedulers.io().scheduleDirect {
            callback(latestShipment)
        }
    }

    override fun setStorageProgressCallback(watcher: StorageProgressWatcher) {

    }
}