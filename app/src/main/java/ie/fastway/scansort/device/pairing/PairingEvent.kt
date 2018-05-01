package ie.fastway.scansort.device.pairing

import ie.fastway.scansort.device.ConnectableDevice

/**
 * An event that occurs when trying to pair with a Bluetooth device.
 */
data class PairingEvent(
        val device: ConnectableDevice,
        val eventType: Type
) {

    /**
     * The type of event that has occurred.
     */
    sealed class Type {
        object DeviceDiscovered : Type()
        object AttemptingToPair : Type()
        object PairingFailed : Type()
        object DiscoveryFinished : Type()
    }

}