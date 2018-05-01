package ie.fastway.scansort.scanning.view

import android.view.View
import com.squareup.otto.Subscribe
import ie.fastway.scansort.device.DeviceManager
import ie.fastway.scansort.device.mocking.UiButtonMockScanner
import ie.fastway.scansort.device.scanner.ScanEvent
import ie.fastway.scansort.device.scanner.ScanResult
import ie.fastway.scansort.config.AppConfig
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.scene.BaseScenePresenter
import ie.fastway.scansort.scene.MainSceneCoordinator
import timber.log.Timber

/**
 *
 */
class ScanningScenePresenter(
        override val sceneView: ScanningSceneView,
        private val deviceManager: DeviceManager
) : BaseScenePresenter(sceneView) {

    var onLaunchDeviceConfigListener: (() -> Unit)? = null

    override fun onEnterScene() {
        if (LogConfig.SCENES) {
            Timber.d("ScanningScenePresenter; onEnterScene")
        }

        sceneView.clearLastScan()

        sceneView.deviceConfigButton.setOnClickListener {
            onLaunchDeviceConfigListener?.invoke()
        }

        if (AppConfig.USE_MOCK_BLUETOOTH) {
            sceneView.mockScanButton.setOnClickListener { UiButtonMockScanner.triggerRandomScan() }
            sceneView.mockScanButton.visibility = View.VISIBLE
        }
        else {
            sceneView.mockScanButton.visibility = View.GONE
        }

        sceneView.deviceNameTx.text = deviceManager.getConnectedScannerName()
    }

    @Subscribe
    fun onScanEvent(scanEvent: ScanEvent) {

        if (LogConfig.SCAN_EVENT) {
            Timber.d("onScanEvent:$scanEvent")
        }

        if (!sceneView.isSetUp()) {
            return
        }

        sceneView.lastScanTx.text = scanEvent.scannedValue
        sceneView.cf.text = ""
        sceneView.rf.text = ""
        sceneView.errorMessageTx.text = ""

    }

    @Subscribe
    fun onScanResult(scanResult: ScanResult) {
        if(!sceneView.isSetUp()) {
            return
        }

        sceneView.clearLastScan()

        sceneView.showBarcode(scanResult.scanEvent.scannedValue)

        with(scanResult.matchedShipment) {
            if (this != null) {
                sceneView.showMatchedShipment(this)
            }
            else {
                sceneView.showError(scanResult.errorMessage)
            }
        }

    }

    override fun getName() = MainSceneCoordinator.SceneKey.SCANNING

}