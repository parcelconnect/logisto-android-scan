package ie.fastway.scansort.device.averydennison

import avd.api.core.ConnectionType
import avd.api.core.IDevice
import avd.api.core.exceptions.ApiConfigurationException
import avd.api.core.exceptions.ApiException
import avd.api.core.exceptions.ApiPrinterException
import avd.api.core.exceptions.ApiScannerException
import avd.api.core.imports.TriggerMode
import avd.api.devices.management.DeviceConnectionInfo
import avd.api.devices.management.DeviceManager
import avd.sdk.CompanionAPIErrors
import com.tubbert.powdroid.android.context.AssetProvider
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.pairing.DeviceConnectionState
import ie.fastway.scansort.device.pairing.DevicePairingTask
import ie.fastway.scansort.device.pairing.ScannerPairingListener
import ie.fastway.scansort.device.printer.PrinterPairingListener
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber
import java.lang.IllegalArgumentException


/**
 *
 */
internal class PathfinderPairingTask(
        private val assetProvider: AssetProvider,
        private val avdDeviceManager: DeviceManager)
    : DevicePairingTask {

    var scannerPairingListener: ScannerPairingListener? = null
    var printerPairingListener: PrinterPairingListener? = null

    private var isPairingStarted = false
    private var isCancelled = false
    private var isConnectionComplete = false
    private lateinit var deviceToPair: ConnectableDevice

    override fun pair(deviceToPair: ConnectableDevice) {
        if (isPairingStarted) {
            throw IllegalStateException("PathfinderPairingTask can only be used to pair once.")
        }
        isPairingStarted = true

        this.deviceToPair = deviceToPair

        val connectionInfo = DeviceConnectionInfo(
                null, deviceToPair.getBluetoothAddress(), ConnectionType.Bluetooth)

        try {
            attemptConnectToDevice(connectionInfo)
        }
        catch (e: ApiException) {
            if (CompanionAPIErrors.CD_ERROR_TIMEOUT == e.errorCode) {
                // TODO: This timeouts occur when the user needs to click the connect button on the scanner.
                if (LogConfig.BLUETOOTH) {
                    Timber.w(e, "PathfinderPairingTask; timeout error from AVD_SCANNER API")
                }
            }
        }
        catch (e: IllegalArgumentException) {
            if (LogConfig.BLUETOOTH) {
                Timber.e(e, "The device cannot be paired with. deviceName=${deviceToPair.getBluetoothName()}")
            }
        }
    }

    private fun attemptConnectToDevice(connectionInfo: DeviceConnectionInfo) {
        val avdDevice = avdDeviceManager.createDevice(connectionInfo)

        try {
            // Get rid of old pending print jobs from the device:
            avdDevice.printer.clearError()
            avdDevice.printer.abortAllJobs()
        }
        catch (e: ApiPrinterException) {
            if (LogConfig.BLUETOOTH) {
                Timber.w(e, "PathfinderPairingTask; ApiPrinterException thrown when clearing printer.")
            }
        }

        // Setup the device for scanning mode:
        avdDevice.scanner.enableScan(true)

        verifyDeviceConnection(avdDevice)

        if (!isCancelled) {
            synchronized(this, {
                // We double-lock check for cancellations:
                if (isCancelled) {
                    return
                }

                deviceToPair.deviceState = DeviceConnectionState.onDeviceConnected()

                val pathfinderScanner = PathfinderScanner(deviceToPair, avdDevice)
                setupScanningConfig(pathfinderScanner)

                val pathfinderPrinter = PathfinderPrinter(assetProvider, deviceToPair, avdDevice)

                scannerPairingListener?.onScannerPaired(pathfinderScanner)

                printerPairingListener?.invoke(pathfinderPrinter)

                isConnectionComplete = true
            })
        }
    }

    private fun setupScanningConfig(pathfinderScanner: PathfinderScanner) {
        try {
            with(pathfinderScanner) {

                /*
                | NOTE: executing every API call individually here using "executeConfigUpdate"
                | is quite slow, but through much testing it has been found to be by far the most
                | reliable way ensure that the desired configuration is actually set on the device.
                 */

                tryApiTask("resetConfiguration [10]") {
                    avdDevice.resetConfiguration()
                }

                executeConfigUpdate {
                    tryApiTask("disableAllBarcodes [20]") { scanner4500.disableAllBarcodes() }
                }

                executeConfigUpdate {
                    tryApiTask("enableScan [30]") { scanner4500.enableScan(true) }
                }

                executeConfigUpdate {
                    tryApiTask("soundVolume=5 [30]") { avdDevice.soundVolume = 5 }
                }

                executeConfigUpdate {
                    tryApiTask("Code128.setEnabled(true) [40]") { configBarcode128.isEnabled = true }
                }

                executeConfigUpdate {
                    tryApiTask("QRCode.setEnabled(false) [41]") { scanner4500.qrCode.isEnabled = false }
                }

                executeConfigUpdate {
                    tryApiTask("avdDevice.setTriggerMode(Scan)) [50]") { avdDevice.triggerMode = TriggerMode.Scan }
                }

                /*
                tryApiTask("avdDevice.addListenerTriggerPress") {
                    avdDevice.addListenerTriggerPress(pathfinderScanner)
                }
                */

                tryApiTask("loadSettings [60]") { avdDevice.loadSettings() }

                if (LogConfig.AVD_SCANNER) {
                    Timber.v("setupScanningConfig; Applying endSetSession [100]")
                }
                avdDevice.endSetSession()
            }
        }
        catch (e: ApiException) {
            if (LogConfig.AVD_SCANNER) {
                val failureReason = when (e) {
                    is ApiConfigurationException ->
                        "setting a new device Session"
                    is ApiScannerException ->
                        "disabling all barcode types"
                    else ->
                        "configuring continuous-scan mode"
                }

                Timber.d("setupScanningConfig failed when $failureReason. ErrorCode=${e.errorCode}")
            }
        }
    }

    /**
     * @throws avd.api.core.exceptions.ApiDeviceException
     *      If the device is not corrected correctly.
     */
    private fun verifyDeviceConnection(avdDevice: IDevice) {
        avdDevice.modelName
    }
}
