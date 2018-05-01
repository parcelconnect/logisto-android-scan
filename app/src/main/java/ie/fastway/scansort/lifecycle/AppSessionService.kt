package ie.fastway.scansort.lifecycle

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.squareup.otto.Subscribe
import ie.fastway.scansort.api.endpoint.auth.AuthApi
import ie.fastway.scansort.api.endpoint.auth.AuthSetupManager
import ie.fastway.scansort.api.endpoint.scanevent.ScanEventExporter
import ie.fastway.scansort.api.endpoint.scanevent.ScanEventLogApi
import ie.fastway.scansort.device.DeviceManager
import ie.fastway.scansort.device.pairing.NativeBluetoothConnector
import ie.fastway.scansort.lifecycle.AppSessionEvent.EventBundle.*
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.scene.AppSessionContext
import ie.fastway.scansort.scene.MainSceneCoordinator
import ie.fastway.scansort.shipment.ShipmentDataManager
import ie.fastway.scansort.views.networkinfo.NetworkingInfoBar
import ie.fastway.scansort.views.networkinfo.WaitingInfoPresenter
import timber.log.Timber


/**
 *
 * Controls the business logic of this App.
 * The MainActivity launches this service, which then takes over managing the
 * other main services used by the app.
 *
 * This service manages the flow for the following tasks:
 *   - Get an access token via [AuthSetupManager].
 *
 *   - Trigger [ShipmentDataManager] to begin downloading the latest Shipment data
 *     and retrieving information from the SQLite cache.
 *
 *   - Start the [DeviceManager] to allow pairing with Bluetooth.
 *
 */
class AppSessionService(
        private val appSessionContext: AppSessionContext,
        private val sceneCoordinator: MainSceneCoordinator
) : SessionEventListener {

    private val REQUEST_CODE_COARSE_LOCATION_PERMISSION = 7489

    private val eventBus = appSessionContext.eventBus

    private val apiService = appSessionContext.apiService

    private var networkInfoPresenter: WaitingInfoPresenter? = null

    private val authManager = AuthSetupManager(
            apiService.createApi(AuthApi::class.java),
            appSessionContext
    )

    private val deviceManager = appSessionContext.deviceManager

    private val shipmentDataManager = appSessionContext.shipmentDataManager

    private val scanLogsExporter: ScanEventExporter

    init {
        authManager.sessionEventListener = this
        deviceManager.sessionEventListener = this

        eventBus.register(shipmentDataManager)

        scanLogsExporter = ScanEventExporter(apiService.createApi(ScanEventLogApi::class.java))
        scanLogsExporter.startExportSchedule()
        eventBus.register(scanLogsExporter)

        eventBus.register(this)
    }

    //----------------------------------------------------------------------------------------------

    fun start() {
        authManager.ensureAccessTokenIsAvailable()

        ensureRequiredPermissionsAreAvailable()
    }

    @Subscribe
    @Suppress("UNCHECKED_CAST")
    override fun onAppSessionEvent(event: AppSessionEvent<*>) {
        if (LogConfig.APP_SESSION) {
            Timber.d("onAppSessionEvent; event=$event")
        }

        when (event.eventBundle) {
            is AuthTokenAvailable ->
                onAuthTokenAvailable(event as AppSessionEvent<AuthTokenAvailable>)

            is ScanningDeviceConnected ->
                onScannerDeviceConnected(event as AppSessionEvent<ScanningDeviceConnected>)

            is LabelPrinterConnected ->
                onLabelPrinterConnected(event as AppSessionEvent<LabelPrinterConnected>)

            is ScanningDeviceDiconnected ->
                    onScannerDisconnected(event as AppSessionEvent<ScanningDeviceDiconnected>)
        }

    }

    private fun onAuthTokenAvailable(event: AppSessionEvent<AuthTokenAvailable>) {
        sceneCoordinator.showDevicePairingScene()
        appSessionContext.shipmentDataManager.start()
    }

    private fun onScannerDeviceConnected(
            event: AppSessionEvent<ScanningDeviceConnected>) {

        deviceManager.connectedScanner = event.eventBundle.scanningDevice

        sceneCoordinator.showScanningScene()
    }

    private fun onScannerDisconnected(
            event: AppSessionEvent<ScanningDeviceDiconnected>) {
        sceneCoordinator.showDevicePairingScene()
    }

    private fun onLabelPrinterConnected(
            event: AppSessionEvent<LabelPrinterConnected>) {
        deviceManager.connectedPrinter = event.eventBundle.labelPrinter
    }

    fun setupInfoBar(networkingInfoBar: NetworkingInfoBar) {
        networkInfoPresenter = WaitingInfoPresenter(networkingInfoBar)
        eventBus.register(networkInfoPresenter!!)
    }

    //----------------------------------------------------------------------------------------------

    private fun ensureRequiredPermissionsAreAvailable() {
        val hasPermission = ActivityCompat.checkSelfPermission(
                appSessionContext.mainActivity, NativeBluetoothConnector.BLUETOOTH_RUNTIME_PERMISSION)

        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (LogConfig.BLUETOOTH) {
                Timber.i("Requesting Bluetooth permissions from the user...")
            }

            ActivityCompat.requestPermissions(
                    appSessionContext.mainActivity,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_CODE_COARSE_LOCATION_PERMISSION
            )
        }

        appSessionContext.deviceInfoFinder.ensurePermissionsAreAvailable()
    }

}