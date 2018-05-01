package ie.fastway.scansort.device.mocking

import avd.api.core.BarcodeType
import com.tubbert.powdroid.mocking.Fakist
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.scanner.ScanEvent
import ie.fastway.scansort.device.scanner.ScanResult
import ie.fastway.scansort.mocking.ShipmentMocker
import ie.logistio.equinox.Equinox
import org.threeten.bp.Instant


/**
 * Creates mock [ScanResult]s.
 */
class ScanMocker {

    private val fakist = Fakist()
    private var shipmentMocker = ShipmentMocker()

    var eventAtOverride: Instant? = null
    private val eventAt get() = eventAtOverride ?: Equinox.now()

    fun createMockScanResult() = ScanResult(
            scanEvent = createMockScanEvent(),
            matchedShipment = shipmentMocker.createShipment(),
            errorMessage = null
    )

    fun createMockScanEvent(): ScanEvent {

        val bluetoothDevice = ConnectableDevice.Factory.createMock()
        val scanningDevice = MockScanner(bluetoothDevice)

        return ScanEvent(
                scannedValue = fakist.makeFakeString(10),
                barcodeType = BarcodeType.Code128,
                scanningDevice = scanningDevice,
                timestamp = eventAt
        )
    }


}