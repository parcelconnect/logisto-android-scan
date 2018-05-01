package ie.fastway.scansort.device.scanner

import avd.api.core.BarcodeType
import ie.fastway.scansort.device.ConnectableDevice
import ie.logistio.equinox.Equinox

/**
 * A Scanner that behaves as a standard Bluetooth HID (Human Input Device) keyboard.
 */
open class KeyboardScanner(connectableDevice: ConnectableDevice)
    : BaseScanningDevice(connectableDevice) {


    val barcodeScanCallback: BarcodeScanListener = this::onBarcodeScan

    fun onBarcodeScan(barcodeValue: String) {
        scanListener?.let {
            val scanEvent = ScanEvent(
                    scannedValue = barcodeValue,
                    barcodeType = BarcodeType.Unknown,
                    scanningDevice = this,
                    timestamp = Equinox.now()
            )
            it(scanEvent)
        }
    }
}