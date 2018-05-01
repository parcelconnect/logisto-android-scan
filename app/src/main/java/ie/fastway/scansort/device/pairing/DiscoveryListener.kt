package ie.fastway.scansort.device.pairing

/**
 * Listens for events that occur while pairing with a BluetoothDevice.
 */
interface DiscoveryListener {

    fun onPairingEvent(event: PairingEvent)

}