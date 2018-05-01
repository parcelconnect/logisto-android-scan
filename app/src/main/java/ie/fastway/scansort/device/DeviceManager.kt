package ie.fastway.scansort.device

import com.squareup.otto.Subscribe
import com.tubbert.powdroid.activity.BaseMajorActivity
import ie.fastway.scansort.device.averydennison.LabelType
import ie.fastway.scansort.device.pairing.DeviceConnectionState
import ie.fastway.scansort.device.pairing.ScanningDeviceRegistry
import ie.fastway.scansort.device.printer.LabelMessage
import ie.fastway.scansort.device.printer.LabelPrinter
import ie.fastway.scansort.device.scanner.KeyboardKeyEventToBarcodeConverter
import ie.fastway.scansort.device.scanner.ScanResult
import ie.fastway.scansort.device.scanner.ScanningDevice
import ie.fastway.scansort.device.symbol.SymbolScanner
import ie.fastway.scansort.lifecycle.AppSessionEvent
import ie.fastway.scansort.lifecycle.EventPublisher
import ie.fastway.scansort.lifecycle.SessionEventListener
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber

/**
 * Manages the workflow for connecting to Bluetooth scanner hardware.
 */
class DeviceManager(
        context: BaseMajorActivity,
        private val eventPublisher: EventPublisher) {

    val connectableDeviceRegistry = ScanningDeviceRegistry()

    private val keyboardScanConverter = KeyboardKeyEventToBarcodeConverter()

    var connectedScanner: ScanningDevice? = null
        set(value) {
            field = value
            onPostScannerSet()
        }

    var connectedPrinter: LabelPrinter? = null

    val deviceConnector: DeviceConnector

    var sessionEventListener: SessionEventListener? = null

    init {
        context.registerKeyEventCallback(keyboardScanConverter)

        deviceConnector = DeviceConnector.Factory
                .createAndSetupBluetoothAdapter(context, connectableDeviceRegistry)

        deviceConnector.setAppEventListener(SessionEventListener {
            sessionEventListener?.onAppSessionEvent(it)
        })

        deviceConnector.searchForDevices()
    }

    @Subscribe
    fun onDeviceDiscoveryEvent(event: DeviceManager.DeviceDiscoveryEvent) {
        when (event) {

            is DeviceManager.DeviceDiscoveryEvent.OnUserRequestBeginDiscovery ->
                beginDeviceDiscovery()

            is DeviceManager.DeviceDiscoveryEvent.OnUserRequestSetBluetoothState ->
                setBluetoothActiveState(event.isTurnedOn)

        }
    }

    /**
     * Turns Bluetooth on or off.
     */
    private fun setBluetoothActiveState(isTurnedOn: Boolean) {
        if (LogConfig.BLUETOOTH) {
            Timber.d("setBluetoothActiveState; isTurnedOn=$isTurnedOn;")
        }

        if (isTurnedOn) {
            deviceConnector.bluetoothConnector.activate()
        }
        else {
            deviceConnector.bluetoothConnector.deactivate()
        }
    }

    @Subscribe
    fun onScanResult(scanResult: ScanResult) {
        connectedPrinter?.print(
                convertToLabelMessage(scanResult))
    }

    private fun convertToLabelMessage(scanResult: ScanResult): LabelMessage {
        with(scanResult) {
            return if (matchedShipment != null) {
                LabelMessage(
                        timestamp = scanEvent.timestamp,
                        primaryText = matchedShipment.regionalFranchisee,
                        secondaryText = matchedShipment.courierFranchisee,
                        labelType = LabelType.SHIPMENT
                )
            }
            else {
                LabelMessage(
                        timestamp = scanEvent.timestamp,
                        primaryText = scanResult.errorMessage,
                        labelType = LabelType.UNKNOWN
                )
            }
        }
    }

    private fun onPostScannerSet() {
        // Ensure the callback is null in case we don't register a new one.
        keyboardScanConverter.onBarcodeReadyCallback = null

        connectedScanner?.let {
            if (it is SymbolScanner) {
                keyboardScanConverter.onBarcodeReadyCallback = it.barcodeScanCallback
            }
        }
    }

    private fun beginDeviceDiscovery() {
        deviceConnector.searchForDevices()
    }

    fun disconnectFromAllDevices() {
        if (LogConfig.SCANNING_DEVICES) {
            Timber.d("Disconnecting from all devices...")
        }

        disconnectFromCurrentScanner()
        disconnectFromCurrentPrinter()
    }

    fun disconnectFromCurrentScanner() {
        connectedScanner?.let {
            if (LogConfig.SCANNING_DEVICES) {
                Timber.d("disconnectFromCurrentScanner; connectedScanner=$it;")
            }
            deviceConnector.disconnectFromScanner(it)

            // Change the state of the device in the registry to "Not Connected".
            connectableDeviceRegistry.find(it.getBluetoothDetails())
                    ?.deviceState = DeviceConnectionState.onDeviceNotConnected()

            // Post the event to registered listeners:
            eventPublisher.postOnBackgroundThread(
                    AppSessionEvent.Factory.onScannerDisconnected(it))
        }
        connectedScanner = null
    }

    fun disconnectFromCurrentPrinter() {
        connectedPrinter?.let {
            eventPublisher.postOnBackgroundThread(
                    AppSessionEvent.Factory.onPrinterDisconnected(it)
            )
        }
        connectedPrinter = null
    }

    /**
     * Gets the display name used to show the particular device that is attached
     * to this [DeviceManager]. This does not return the product name, but instead returns
     * something that can be used to uniquely identify the exact device that is
     * connected.
     *
     * If no device is connected, then this returns NULL.
     */
    fun getConnectedScannerName(): String? =
            connectedScanner?.getBluetoothDetails()?.getBluetoothName()

    /**
     * Simple event to use with [com.squareup.otto.Bus].
     */
    sealed class DeviceDiscoveryEvent {

        object OnDiscoveryStarted : DeviceDiscoveryEvent()
        /**
         * The event to trigger when the user requests that discovery be started.
         */
        object OnUserRequestBeginDiscovery : DeviceDiscoveryEvent()

        data class OnUserRequestSetBluetoothState(val isTurnedOn: Boolean) : DeviceDiscoveryEvent()

        object OnDiscoveryFinished : DeviceDiscoveryEvent()
    }


}