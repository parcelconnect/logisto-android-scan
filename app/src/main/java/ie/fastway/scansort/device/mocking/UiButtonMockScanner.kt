package ie.fastway.scansort.device.mocking

import avd.api.core.BarcodeType
import ie.fastway.scansort.api.endpoint.shipment.ShipmentCatalogApiMocker
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.scanner.BaseScanningDevice
import ie.fastway.scansort.device.scanner.ScanEvent
import ie.logistio.equinox.Equinox
import java.util.*

/**
 *
 */
object UiButtonMockScanner
    : BaseScanningDevice(ConnectableDevice.Factory.createMockPathfinder()) {

    private val rand = Random()
    private var trackingNumberProvider: ShipmentCatalogApiMocker? = null

    fun triggerRandomScan() {
        scanListener?.let {
            val barcode = trackingNumberProvider?.getSeededTrackingNumber()
                    ?: generateRandomBarcode()

            val scanEvent = ScanEvent(
                    barcode,
                    BarcodeType.Code128,
                    this,
                    Equinox.now()
            )

            it(scanEvent)
        }
    }

    private fun generateRandomBarcode(): String {
        val sb = StringBuilder()
        for (i in 1..10) {
            sb.append(rand.nextInt(10))
        }
        return sb.toString()
    }

    public fun setTrackingNumberProvider(shipmentApi: ShipmentCatalogApiMocker) {
        trackingNumberProvider = shipmentApi
    }

}