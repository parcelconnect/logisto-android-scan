package ie.fastway.scansort.shipment.repo

import ie.fastway.scansort.shipment.Shipment

/**
 *
 */
interface ShipmentRepo {

    fun storeAll(shipmentsToStore: Collection<Shipment>)

    fun findShipmentByBarcodeAsync(
            barcode: String,
            callback: (String, Shipment?) -> Unit
    )

    /**
     * Gets the Shipment with the latest "updatedAt" timestamp.
     */
    fun findLatestShipment(callback: (Shipment?) -> Unit)

    /**
     * Sets a callback function that will be informed about the progress of storing jobs
     * in the repo.
     */
    fun setStorageProgressCallback(watcher: StorageProgressWatcher);

}

typealias StorageProgressWatcher = ((progress: Int, total: Int) -> Unit);
