package ie.fastway.scansort.device.averydennison

import avd.api.barcodes.onedimensional.Code128
import avd.api.core.*
import avd.api.core.exceptions.ApiException
import avd.api.core.imports.ButtonState
import avd.api.core.imports.ButtonType
import avd.api.scanners.ScannerSe4500
import avd.sdk.CompanionAPIErrors
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.scanner.BaseScanningDevice
import ie.fastway.scansort.device.scanner.ScanEvent
import ie.fastway.scansort.logging.LogConfig
import ie.logistio.equinox.Equinox
import timber.log.Timber

/**
 * Avery Dennison Pathfinder 6140 scanner.
 */
class PathfinderScanner(
        connectableDevice: ConnectableDevice,
        val avdDevice: IDevice
) : BaseScanningDevice(connectableDevice), IListenerTriggerPress, IListenerScan {


    // TODO: Register IListenerError to listen for device disconnect callbacks.

    internal val avdScanner: IScanner
        get() = avdDevice.scanner

    internal val scanner4500: ScannerSe4500
        get() = avdScanner as ScannerSe4500

    internal val configBarcode128: Code128
        get() = scanner4500.code128

    private var lastScanEvent: ScanEvent? = null

    init {
        avdScanner.removeListenerScan(this)  // Must ensure any previous lisener is removed before adding again.
        avdScanner.addListenerScan(this)
    }

    //----------------------------------------------------------------------------------------------
    override fun onScanReceived(
            scanData: String?, barcodeType: BarcodeType?, device: IDevice?) {

        if (LogConfig.AVD_SCANNER) {
            Timber.d("onScanReceived; scanData=$scanData, barcodeType=$barcodeType, device=$device")
        }

        if (barcodeType == null || barcodeType == BarcodeType.None) {
            // No barcode was read.
            return
        }
        else if (barcodeType != BarcodeType.Code128) {
            if (LogConfig.AVD_CONFIG) {
                Timber.w("Read a barcode type that should be filtered from Scanner config. BarcodeType=$barcodeType")
            }
            return
        }

        // The barcode should be published to the listeners as a scan event:
        scanListener?.let {
            // Ensure non-null values:
            val scanMessage = scanData ?: ""
            val barcode = barcodeType ?: BarcodeType.Unknown

            val scanEvent = ScanEvent(scanMessage, barcode, this, Equinox.now())

            if (!isRepeatScan(scanEvent)) {
                lastScanEvent = scanEvent
                it.invoke(scanEvent)
            }
            // Note: We don't set bounce scanEvents as lastScanEvent, so if the trigger bounces
            // for a very long time the de-bouncer will fail.
        }

    }

    /**
     * De-bounces scan events so that repeat events are not broadcast.
     */
    private fun isRepeatScan(scanEvent: ScanEvent): Boolean {
        with(lastScanEvent) {
            if (this == null) {
                return false
            }

            return ((scannedValue == scanEvent.scannedValue)
                    && (barcodeType == scanEvent.barcodeType)
                    && (scanningDevice == scanEvent.scanningDevice)
                    && (Equinox.isApproximatelyEqual(timestamp, scanEvent.timestamp, 500L)))
        }
    }

    override fun onTriggerPressReceived(
            buttonType: ButtonType?, buttonState: ButtonState?, iDevice: IDevice?) {

        if (LogConfig.AVD_SCANNER) {
            Timber.d("onTriggerPressReceived; buttonType=$buttonType, buttonState=$buttonState, iDevice=$iDevice")
        }

    }

    override fun getBluetoothDetails(): ConnectableDevice = connectableDevice

    fun setScanMode(scanMode: ScanMode) {

        // TODO: use the executeConfigUpdate() method here.

        writeLog("enableScan")
        scanner4500.enableScan(true)

        writeLog("loadBaseSettings")
        scanner4500.loadBaseSettings()

        writeLog("beginSetSession")
        scanner4500.beginSetSession()

        writeLog("Setting mode=$scanMode")
        scanner4500.mode = scanMode

        writeLog("endSetSession")
        scanner4500.endSetSession()

        writeLog("unloadSettings")
        scanner4500.unloadSettings()
    }

    fun getScanMode(): ScanMode {
        return scanner4500.mode
    }

    internal fun executeConfigUpdate(updateTask: () -> Unit) {

        // loadSettings must be called twice to guarantee that settings changes
        // actually take effect. Nobody knows why.

        scanner4500.loadSettings()
        scanner4500.beginSetSession()
        scanner4500.loadSettings()

        updateTask.invoke()

        scanner4500.endSetSession()

        scanner4500.unloadSettings()
    }

    /**
     * Attempts to send data to the Pathfinder over the Bluetooth socket.
     * If the socket is busy it will re-try a few times.
     */
    internal fun tryApiTask(taskName: String = "", task: () -> Unit) {

        val maxAttempts = 4
        var attemptCount = 0;
        var isSuccessful = false
        var mayRetry = false

        if (LogConfig.AVD_SCANNER) {
            Timber.v("setupScanningConfig; $taskName;")
        }

        val lock = Object()
        synchronized(lock) {
            do {
                mayRetry = false
                try {
                    attemptCount++
                    task()
                    isSuccessful = true
                }
                catch (e: ApiException) {
                    var message = "Pathfinder API Exception. attemptCount=$attemptCount"
                    if (!taskName.isBlank()) {
                        message = "$taskName; $message"
                    }
                    Timber.e(e, message)

                    // If the device is busy we can try to execute the request again after
                    // some time has passed.
                    if (e.errorCode == CompanionAPIErrors.CD_ERROR_BUSY) {
                        mayRetry = true
                        lock.wait(100)
                    }
                }
            } while ((!isSuccessful) && (mayRetry) && (attemptCount < maxAttempts))
        }
    }

    private inline fun writeLog(message: String) {
        if (LogConfig.SCANNER_CONFIG) {
            Timber.d(message)
        }
    }

    /**
     * @throws avd.api.core.exceptions.ApiScannerException on failure.
     */
    fun resetConfigurationToDefault() {
        scanner4500.resetConfiguration()
    }

    override fun toString(): String {
        return "PathfinderScanner:${avdDevice.modelName}"
    }

}