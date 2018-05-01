package ie.fastway.scansort.device

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tubbert.powdroid.android.context.AssetProvider
import com.tubbert.powdroid.android.context.ContextAssetProvider
import com.tubbert.powdroid.android.context.MockAssetProvider
import ie.fastway.scansort.config.AppConfig
import ie.fastway.scansort.device.averydennison.PathfinderPairingOperator
import ie.fastway.scansort.device.mocking.UiButtonMockScanner
import ie.fastway.scansort.device.pairing.*
import ie.fastway.scansort.device.printer.LabelPrinter
import ie.fastway.scansort.device.scanner.ScanEvent
import ie.fastway.scansort.device.scanner.ScanningDevice
import ie.fastway.scansort.device.symbol.SymbolPairingOperator
import ie.fastway.scansort.lifecycle.AppSessionEvent
import ie.fastway.scansort.lifecycle.AppSessionProvider
import ie.fastway.scansort.lifecycle.EventPublisher
import ie.fastway.scansort.lifecycle.SessionEventListener
import ie.fastway.scansort.logging.LogConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Responsible for establishing new connections with Bluetooth scanning devices.
 */
class DeviceConnector(
        private val assetProvider: AssetProvider,
        val bluetoothConnector: BluetoothConnector,
        private val deviceRegistry: ScanningDeviceRegistry,
        private val eventPublisher: EventPublisher
) {

    companion object {
        const val BOND_STATE_UNKNOWN = -1;
        const val BLUETOOTH_STATE_UNKNOWN = -2;

        /**
         * The number of milliseconds to wait when connecting to a device to
         * still show it as "connecting".
         * When the user clicks "Connect" on a device, that device's [PairingOperator]
         * has [CONNECT_DEVICE_MAX_TIMEOUT_MS] millis to report the device as
         * successfully connected, during which time the device will be shown as
         * being in the "Connecting" state. After [CONNECT_DEVICE_MAX_TIMEOUT_MS] the
         * device will revert to the "Ready to Connect" state, if it has not
         * been successfully connected in this time.
         */
        const val CONNECT_DEVICE_MAX_TIMEOUT_MS = 10 * 1000L;
    }

    private var sessionEventListener: SessionEventListener? = null

    private val bluetoothWatcher: BluetoothAvilabilityWatcher =
            BluetoothAvilabilityWatcher(bluetoothConnector, eventPublisher)

    private val bluetoothBroadcastHandler = BluetoothIntentHandler()

    private val pathfinderOperator = PathfinderPairingOperator(assetProvider)
    private val symbolOperator = SymbolPairingOperator()

    private val registeredOperators = listOf(
            pathfinderOperator, symbolOperator)

    init {
        bluetoothConnector.getBondedDevices()
                .map { addBluetoothDeviceIfValid(it) }

        bluetoothWatcher.startWatching()
    }

    //----------------------------------------------------------------------------------------------
    // BLUETOOTH
    //----------------------------------------------------------------------------------------------

    /**
     * Begins the process of searching for Bluetooth devices to pair with.
     */
    fun searchForDevices() {

        if (bluetoothConnector.isDiscovering()) {
            if (LogConfig.BLUETOOTH) {
                Timber.d("Cancelling existing Bluetooth discovery...")
            }
            bluetoothConnector.cancelDiscovery()
        }

        if (LogConfig.BLUETOOTH) {
            Timber.d("Starting discovery on BluetoothConnector=$bluetoothConnector")
        }

        bluetoothConnector.startDiscovery()
    }

    private fun addBluetoothDeviceIfValid(bluetoothDevice: BluetoothDevice) {
        val connectableDevice = ConnectableDevice.Factory
                .createFromBluetoothDevice(bluetoothDevice)
        addBluetoothDeviceIfValid(connectableDevice)
    }

    private fun addBluetoothDeviceIfValid(connectableDevice: ConnectableDevice) {
        if (findCompatibleOperator(connectableDevice) != null) {
            deviceRegistry.add(connectableDevice)
        }
        else if (LogConfig.BLUETOOTH_ALL_DEVICES) {
            Timber.d("This device is not a Pathfinder and will not be listed for connection: $connectableDevice")
        }
    }

    //----------------------------------------------------------------------------------------------
    // ATTEMPT TO PAIR
    //----------------------------------------------------------------------------------------------

    fun connectToDevice(deviceToConnect: ConnectableDevice) {

        if (LogConfig.SCANNING_DEVICES) {
            Timber.d("connectToDevice: $deviceToConnect")
        }

        if (AppConfig.USE_MOCK_BLUETOOTH) {
            val mockScanningDevice = UiButtonMockScanner

            onScannerPaired(mockScanningDevice)

            return
        }

        val compatibleOperator = findCompatibleOperator(deviceToConnect)

        if (compatibleOperator != null) {

            if (!deviceToConnect.deviceState.isConnected) {
                deviceToConnect.deviceState = DeviceConnectionState.onAttemptingToPair()
            }

            Schedulers.io().scheduleDirect {
                compatibleOperator.pairWith(
                        deviceToConnect,
                        ScannerPairingListener { device -> onScannerPaired(device) },
                        this::onPrinterConnected
                )
            }

            AndroidSchedulers.mainThread().scheduleDirect {
                showDeviceIsConnecting(deviceToConnect)
            }

            AppSessionProvider.createSingleTaskScheduler(CONNECT_DEVICE_MAX_TIMEOUT_MS)
                    .subscribe { showStopTryingToConnect(deviceToConnect) }

        }
        else {
            // TODO: Hanlde attempt to pair with wrong device type.
            // This shouldn't happen, because devices which can't be paired
            // with are not added to the DeviceRegistry.

            if (LogConfig.BLUETOOTH) {
                Timber.e("The chosen deviceToConnect cannot be paired by the devicePairingOperator. deviceToConnect=$deviceToConnect")
            }
        }
    }

    private fun showDeviceIsConnecting(deviceToConnect: ConnectableDevice) {
        deviceRegistry.notifyConnectionStateChanged(
                deviceToConnect, PairingEvent.Type.AttemptingToPair)
    }

    private fun showStopTryingToConnect(deviceToConnect: ConnectableDevice) {
        if (LogConfig.BLUETOOTH) {
            Timber.d("showStopTryingToConnect(); deviceToConnect=$deviceToConnect")
        }

        if (deviceToConnect.getBondState().isConnecting) {
            // The device is still trying to connect, but the timeout has expired,
            // so indicate that pairing has failed.

            // NOTE: The pairing might not have actually failed, sometimes the Pathfinder scanner
            // just takes a very long time to configure.

            deviceRegistry.notifyConnectionStateChanged(
                    deviceToConnect, PairingEvent.Type.PairingFailed)
        }
    }

    private fun findCompatibleOperator(deviceToConnect: ConnectableDevice): PairingOperator? {
        var foundOperator: PairingOperator? = null
        registeredOperators.forEach { operator ->
            if (operator.canPairWith(deviceToConnect)) {
                foundOperator = operator

                // Operators are added in order of priority, so the first compatible
                // one we find is the one we will use.
                return@forEach
            }
        }
        return foundOperator
    }

    //----------------------------------------------------------------------------------------------
    // AFTER PAIRING
    //----------------------------------------------------------------------------------------------
    private fun onScannerPaired(scanningDevice: ScanningDevice) {
        if (LogConfig.BLUETOOTH) {
            Timber.d("onScannedPaired: $scanningDevice")
        }

        scanningDevice.setScanEventListener(this::onScanEvent)

        sessionEventListener?.onAppSessionEvent(
                AppSessionEvent.Factory.onScannerConnected(scanningDevice))
    }

    private fun onPrinterConnected(connectedPrinter: LabelPrinter) {
        if (LogConfig.BLUETOOTH) {
            Timber.d("onPrinterConnected(); connectedPrinter=$connectedPrinter;")
        }

        sessionEventListener?.onAppSessionEvent(
                AppSessionEvent.Factory.onPrinterConnected(connectedPrinter)
        )
    }

    private fun onScanEvent(scanEvent: ScanEvent) {
        if (LogConfig.SCAN_EVENT) {
            Timber.v("Broadcasting ScanEvent=$scanEvent")
        }

        AppSessionProvider.eventPublisher.postOnUiThread(scanEvent)
    }

    fun setAppEventListener(listener: SessionEventListener) {
        this.sessionEventListener = listener
    }

    //----------------------------------------------------------------------------------------------
    // DISCONNECTING FROM DEVICES
    //----------------------------------------------------------------------------------------------

    fun disconnectFromScanner(scanningDevice: ScanningDevice) {

        for (operator in registeredOperators) {
            if (operator.canPairWith(scanningDevice.getBluetoothDetails())) {
                if (LogConfig.SCANNING_DEVICES) {
                    Timber.d("Found Operator for disconnecting from Scanner. operator=$operator;")
                }

                operator.disconnectFrom(scanningDevice)
                break
            }
        }

        /*
        registeredOperators.firstOrNull {
            it.canPairWith(scanningDevice.getBluetoothDetails())
        }?.disconnectFrom(scanningDevice)
        */
    }

    //----------------------------------------------------------------------------------------------

    inner class BluetoothIntentHandler {

        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (LogConfig.BLUETOOTH) {
                    Timber.d("BroadcastReceiver::onReceive; intent=$intent")
                }

                onReceivedBroadcast(intent ?: Intent())
            }
        }

        fun onReceivedBroadcast(intent: Intent) {

            if (LogConfig.BLUETOOTH) {
                Timber.d("onReceivedBroadcast: ${intent.action}")
            }

            when (intent.action) {
                BluetoothDevice.ACTION_FOUND ->
                    onBluetoothDeviceDiscovered(intent)

                BluetoothDevice.ACTION_BOND_STATE_CHANGED ->
                    onBluetoothBondStateChanged(intent)

                BluetoothDevice.ACTION_ACL_CONNECTED ->
                    onBluetoothDeviceConnected(intent)

                BluetoothDevice.ACTION_ACL_DISCONNECTED ->
                    onBluetoothDeviceDisconnected(intent)

                BluetoothAdapter.ACTION_STATE_CHANGED ->
                    onBluetoothAvailabilityStateChanged(intent)

                BluetoothAdapter.ACTION_DISCOVERY_STARTED ->
                    onBluetoothDiscoveryStart()

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->
                    onBluetoothDiscoveryFinish()
            }
        }


        private fun onBluetoothDeviceDiscovered(intent: Intent) {
            val device = extractDevice(intent)
            if (LogConfig.BLUETOOTH_ALL_DEVICES) {
                val connectableDevice = ConnectableDevice.Factory.createFromBluetoothDevice(device)

                Timber.d("onBluetoothDeviceDiscovered; bluetoothDevice=$device; connectableDevice=$connectableDevice;")
            }

            addBluetoothDeviceIfValid(device)
        }
    }

    private fun onBluetoothBondStateChanged(intent: Intent) {
        val device = extractDevice(intent)
        val currentState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BOND_STATE_UNKNOWN)
        val previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BOND_STATE_UNKNOWN)

        if (LogConfig.BLUETOOTH) {
            Timber.d("onBluetoothBondStateChanged: $device; currentState=$currentState; previousState=$previousState")
        }

        symbolOperator.onBondStateChanged(device, currentState, previousState)
        // keyboardScannerOperator.onBondStateChanged(device, currentState, previousState)
    }

    private fun onBluetoothDeviceConnected(intent: Intent) {
        val device = extractDevice(intent)

        if (LogConfig.BLUETOOTH) {
            Timber.d("Connected to Bluetooth device:  $device")
        }

    }

    private fun onBluetoothDeviceDisconnected(intent: Intent) {
        val device = extractDevice(intent)

        if (LogConfig.BLUETOOTH) {
            Timber.d("onBluetoothDeviceDisconnected:  $device")
        }
    }

    private fun extractDevice(intent: Intent): BluetoothDevice =
            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

    private fun onBluetoothAvailabilityStateChanged(intent: Intent) {
        val currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BLUETOOTH_STATE_UNKNOWN)
        val previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BLUETOOTH_STATE_UNKNOWN)

        if (LogConfig.BLUETOOTH) {

            Timber.d("Bluetooth availability state changed from $currentState to $previousState")
        }

        // Post the BluetoothStatus to listeners:
        eventPublisher.postOnUiThread(BluetoothStatus(
                isTurnedOn = (currentState == BluetoothAdapter.STATE_ON)))
    }

    /**
     * Called when the [BluetoothAdapter] begins searching for Bluetooth devices.
     */
    private fun onBluetoothDiscoveryStart() {
        if (LogConfig.BLUETOOTH) {
            Timber.d("onBluetoothDiscoveryStart")
        }
        eventPublisher.postOnUiThread(DeviceManager.DeviceDiscoveryEvent.OnDiscoveryStarted)
    }

    /**
     * Called when the [BluetoothAdapter] has finished searching for Bluetooth devices.
     */
    private fun onBluetoothDiscoveryFinish() {
        if (LogConfig.BLUETOOTH) {
            Timber.d("onBluetoothDiscoveryFinish")
        }
        eventPublisher.postOnUiThread(DeviceManager.DeviceDiscoveryEvent.OnDiscoveryFinished)
    }


    //----------------------------------------------------------------------------------------------

    object Factory {

        private var isMockModeActive = false

        private val REQUIRED_INTENTS = arrayOf(
                BluetoothDevice.ACTION_FOUND,
                BluetoothDevice.ACTION_BOND_STATE_CHANGED,
                BluetoothDevice.ACTION_ACL_CONNECTED,
                BluetoothDevice.ACTION_ACL_DISCONNECTED,
                BluetoothAdapter.ACTION_STATE_CHANGED,
                BluetoothAdapter.ACTION_DISCOVERY_STARTED,
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED
        )

        fun initiateMockBluetoothMode() {
            isMockModeActive = true
        }

        /**
         * Creates a [DeviceConnector] and registers the native Andorid [BluetoothAdapter] as
         * the [BluetoothConnector], provided mock Bluetooth mode is not active.
         *
         * If mock Bluetooth mode is active, then [MockBluetoothConnector] is used instead.
         *
         * Automatically adds the currently bonded devices to the [deviceRegistry].
         *
         */
        fun createAndSetupBluetoothAdapter(
                context: Activity,
                deviceRegistry: ScanningDeviceRegistry,
                eventPublisher: EventPublisher = AppSessionProvider.eventPublisher
        ): DeviceConnector {

            //TODO: Execute asynchronously, check if Bluetooth permissions must be requested from the user.

            val connector = if (isMockModeActive) {
                // Create a mock Bluetooth connector to work with.
                val mockConnector = MockBluetoothConnector()
                mockConnector.connectableDeviceRegistry = deviceRegistry
                DeviceConnector(MockAssetProvider(), mockConnector, deviceRegistry, eventPublisher)
            }
            else {
                // Connect to the native Android BluetoothAdapter

                // TODO: Check if user has granted Bluetooth permissions on Android 6+.

                val bluetoothAdapter = NativeBluetoothConnector(context)

                val asserManager = ContextAssetProvider(context)
                val deviceConnector = DeviceConnector(asserManager, bluetoothAdapter, deviceRegistry, eventPublisher)

                val intentReceiver = deviceConnector.bluetoothBroadcastHandler.broadcastReceiver
                val intentFilter = IntentFilter()
                REQUIRED_INTENTS.forEach {
                    intentFilter.addAction(it)
                }
                context.registerReceiver(intentReceiver, intentFilter)

                deviceConnector
            }

            return connector
        }
    }

}