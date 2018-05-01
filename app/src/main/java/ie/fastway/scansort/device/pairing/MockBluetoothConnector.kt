package ie.fastway.scansort.device.pairing

import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.DeviceManager
import ie.fastway.scansort.lifecycle.AppSessionProvider
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber

/**
 *
 */
class MockBluetoothConnector : BluetoothConnector {

    var connectableDeviceRegistry: ScanningDeviceRegistry? = null

    private var isDicoveryActive = false
    private var isBluetoothActive = false

    override fun getBondedDevices(): Collection<ConnectableDevice> {
        return ArrayList()
    }

    override fun isDiscovering(): Boolean {
        return isDicoveryActive
    }

    override fun startDiscovery(): Boolean {
        if (!isBluetoothActive) {
            if (LogConfig.BLUETOOTH) {
                Timber.d("Bluetooth is not active, so discovery will not be started.")
            }
            return false
        }

        isDicoveryActive = true

        AppSessionProvider.eventPublisher.postOnUiThread(DeviceManager.DeviceDiscoveryEvent.OnDiscoveryStarted)

        AppSessionProvider.createSingleTaskScheduler(5000).subscribe {
            for (i in 1..4) {
                val device = ConnectableDevice.Factory.createMockPathfinder()
                connectableDeviceRegistry?.add(device)
            }
            AppSessionProvider.eventPublisher.postOnUiThread(DeviceManager.DeviceDiscoveryEvent.OnDiscoveryFinished)
        }

        return isDicoveryActive
    }

    override fun activate() {
        isBluetoothActive = true
    }

    override fun deactivate() {
        isBluetoothActive = false
    }

    override fun cancelDiscovery(): Boolean {
        isDicoveryActive = false
        return isDicoveryActive
    }

    override fun getBluetoothStatus(): BluetoothStatus {
        return BluetoothStatus(isTurnedOn = isBluetoothActive, isPermissionsGranted = true)
    }

    override fun requestBluetoothPermissions() {

    }
}