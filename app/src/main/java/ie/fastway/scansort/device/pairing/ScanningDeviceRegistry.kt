package ie.fastway.scansort.device.pairing

import android.support.annotation.UiThread
import com.tubbert.powdroid.events.Executor
import com.tubbert.powdroid.events.Publisher
import com.tubbert.powdroid.events.Subscribable
import ie.fastway.scansort.device.ConnectableDevice
import timber.log.Timber
import java.util.*

/**
 *
 */
class ScanningDeviceRegistry(
        private val discoveryPublisher: Publisher<DiscoveryListener> = Publisher()
) : Subscribable<DiscoveryListener> by discoveryPublisher {

    private val discoveredDevices = HashMap<String, ConnectableDevice>()

    /**
     * Ordered list used to retrieve devices by position
     */
    private val deviceList = ArrayList<ConnectableDevice>()

    val size: Int get() = deviceList.size

    init {
        discoveryPublisher.logger = Timber.asTree()
    }

    fun add(device: ConnectableDevice) {
        synchronized(discoveredDevices) {
            if (!contains(device)) {
                discoveredDevices.put(device.asKey(), device)
                deviceList.add(device)
                notifySubscribersOnDeviceFound(device)
            }
        }
    }

    fun addAll(devicesToAdd: Collection<ConnectableDevice>): LinkedList<ConnectableDevice> {
        val addedDevices = LinkedList<ConnectableDevice>()
        synchronized(discoveredDevices) {
            devicesToAdd.forEach {
                if (!contains(it)) {
                    discoveredDevices.put(it.asKey(), it)
                    deviceList.add(it)
                    addedDevices.add(it)
                    notifySubscribersOnDeviceFound(it)
                }
            }
        }

        return addedDevices
    }

    fun getAtPosition(position: Int): ConnectableDevice? {
        return deviceList[position]
    }

    /**
     * Finds the entry for the given device in this registry.
     */
    fun find(deviceToFind: ConnectableDevice): ConnectableDevice? {
        return discoveredDevices.get(deviceToFind.asKey())
    }

    @UiThread
    fun notifyConnectionStateChanged(
            changedDevice: ConnectableDevice, eventType: PairingEvent.Type) {
        val pairingEvent = PairingEvent(changedDevice, eventType)

        discoveryPublisher.executeOnListeners(Executor {
            it.onPairingEvent(pairingEvent)
        })
    }

    private fun ConnectableDevice.asKey(): String = getBluetoothAddress()

    private fun notifySubscribersOnDeviceFound(element: ConnectableDevice) {
        val pairingEvent = PairingEvent(element, PairingEvent.Type.DeviceDiscovered)

        discoveryPublisher.executeOnListeners(Executor {
            it.onPairingEvent(pairingEvent)
        })
    }

    fun contains(device: ConnectableDevice): Boolean
            = discoveredDevices.containsKey(device.asKey())

}

