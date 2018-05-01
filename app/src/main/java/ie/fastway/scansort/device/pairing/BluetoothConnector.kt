package ie.fastway.scansort.device.pairing

import ie.fastway.scansort.device.ConnectableDevice

/**
 * Wrapper for the native Android BluetoothAdapter.
 */
interface BluetoothConnector {

    fun getBondedDevices(): Collection<ConnectableDevice>

    fun isDiscovering(): Boolean

    fun startDiscovery(): Boolean

    fun cancelDiscovery(): Boolean

    fun activate()

    fun deactivate()

    fun getBluetoothStatus(): BluetoothStatus

    fun requestBluetoothPermissions()

}