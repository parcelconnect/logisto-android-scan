package ie.fastway.scansort.device.pairing

import ie.fastway.scansort.device.ConnectableDevice

/**
 *
 */
interface DevicePairingTask {

    fun pair(deviceToPair: ConnectableDevice)

}