package ie.fastway.scansort.device.scanner

import ie.fastway.scansort.device.ConnectableDevice

/**
 *
 */
abstract class BaseScanningDevice(
        protected val connectableDevice: ConnectableDevice)
    : ScanningDevice {

    protected var scanListener: ScanEventListener? = null

    override fun setScanEventListener(scanEventListener: ScanEventListener) {
        this.scanListener = scanEventListener
    }

    override fun getBluetoothDetails(): ConnectableDevice {
        return connectableDevice
    }
}