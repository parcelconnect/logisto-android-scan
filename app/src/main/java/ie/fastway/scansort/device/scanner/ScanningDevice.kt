package ie.fastway.scansort.device.scanner

import ie.fastway.scansort.device.ConnectableDevice

/**
 *
 */
interface ScanningDevice {

    fun setScanEventListener(scanEventListener: ScanEventListener)

    /**
     * Gets a unique ID that uniquely identifies this particular device.
     */
    fun getDeviceId(): String = getBluetoothDetails().getDeviceId()

    fun getBluetoothDetails(): ConnectableDevice

}