package ie.fastway.scansort.device.averydennison

import avd.api.core.IListenerScan
import avd.api.devices.management.DeviceManager
import com.tubbert.powdroid.android.context.AssetProvider
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.pairing.DiscoveryListener
import ie.fastway.scansort.device.pairing.PairingOperator
import ie.fastway.scansort.device.pairing.ScannerPairingListener
import ie.fastway.scansort.device.printer.PrinterPairingListener
import ie.fastway.scansort.device.scanner.ScanningDevice
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber


/**
 * Manages the process of pairing with the AveryDennison Monarch Pathfinder 6140
 * scanner-printer device.
 *
 */
class PathfinderPairingOperator(val assetProvider: AssetProvider) : PairingOperator {

    companion object {
        /**
         * The string the AveryDennison Monarch Pathfinder 6140 printer-scanner
         * Bluetooth device name starts with.
         */
        const val PREFIX_PATHFINDER_6140 = "6140"
    }

    private val avdDeviceManager = DeviceManager()

    private val scanListener: IListenerScan? = null

    private val discoveryListener: DiscoveryListener? = null

    private val deviceStateListener: DeviceConnectionStateListener? = null

    override fun canPairWith(bluetoothDevice: ConnectableDevice) = isMonarch6140(bluetoothDevice)

    /**
     * Checks if the Bluetooth device is an AVD Monarch Pathfinder 6140.
     */
    private fun isMonarch6140(device: ConnectableDevice?): Boolean {
        return device?.getBluetoothName() != null &&
                device.getBluetoothName().startsWith(PREFIX_PATHFINDER_6140)
    }

    override fun pairWith(
            bluetoothDevice: ConnectableDevice,
            scannerPairingListener: ScannerPairingListener,
            printerPairingListener: PrinterPairingListener?) {

        if (LogConfig.BLUETOOTH) {
            Timber.d("Pairing with pathfinder: $bluetoothDevice")
        }

        val task = PathfinderPairingTask(assetProvider, avdDeviceManager)
        task.scannerPairingListener = scannerPairingListener
        task.printerPairingListener = printerPairingListener
        task.pair(bluetoothDevice)
    }

    override fun disconnectFrom(scanningDevice: ScanningDevice) {
        if(scanningDevice is PathfinderDevice) {
            disconnectFrom(scanningDevice.pathfinderScanner)
        }
        else if (scanningDevice is PathfinderScanner) {
            if (LogConfig.AVD_SCANNER) {
                Timber.d("disconnectFromScanner; scanningDevice=$scanningDevice;")
            }

            scanningDevice.avdDevice.connection.disconnect()
        }
    }
}
