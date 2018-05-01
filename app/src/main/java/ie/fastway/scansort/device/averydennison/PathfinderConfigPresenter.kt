package ie.fastway.scansort.device.averydennison

import avd.api.core.exceptions.ApiConfigurationException
import avd.api.core.exceptions.ApiException
import avd.api.core.exceptions.ApiScannerException
import com.tubbert.powdroid.ui.OnClickReference
import ie.fastway.scansort.device.DeviceManager
import ie.fastway.scansort.scene.BaseScenePresenter
import ie.fastway.scansort.scene.MainSceneCoordinator
import ie.fastway.scansort.scene.SceneContainer
import ie.logistio.equinox.Equinox

/**
 *
 */
class PathfinderConfigPresenter(
        override val sceneView: PathfinderConfigView,
        private val deviceManager: DeviceManager,
        private val onExitListener: OnClickReference
) : BaseScenePresenter(sceneView) {

    override fun onEnterScene() {
        sceneView.onSubmitListener = this::onSubmitConfig
        sceneView.onConfigResetListener = this::onResetConfig
        sceneView.onExitClickListener = onExitListener
        sceneView.onDisconnectClickListener = deviceManager::disconnectFromAllDevices

        val scanner = getPathfinder()

        if (scanner is PathfinderScanner) {
            showPathfinder(scanner)
        }
        else {
            sceneView.clear()
        }
    }

    private fun showPathfinder(scanner: PathfinderScanner) {
        sceneView.showDevice(scanner.getBluetoothDetails())
        sceneView.setScanMode(scanner.getScanMode())
    }

    private fun onSubmitConfig() {
        try {
            getPathfinder()?.setScanMode(sceneView.getSelectedScanMode())

            showSuccessMessage("Scan mode changed.")
        }
        catch (e: ApiConfigurationException) {
            showDeviceError(e, "Unable to set up scan mode.")
        }
    }

    /**
     * Reset to the default scanner configurations
     */
    private fun onResetConfig() {
        try {
            getPathfinder()?.let {
                it.resetConfigurationToDefault()
                showPathfinder(it)
                showSuccessMessage("Configuration reset to default.")
            }

        }
        catch (e: ApiScannerException) {
            showDeviceError(e, "Unable to reset configuration.")
        }
    }

    //----------------------------------------------------------------------------------------------

    private fun showDeviceError(e: ApiException, contextMessage: String) {
        val message = StringBuilder(Equinox.nowAsUserTime() + "| UNKNOWN: $contextMessage ")
        message.append("ErrorCode=${e.errorCode}.")
        if (e.message != null) {
            message.append(" Message=${e.message}")
        }
        sceneView.showErrorMessage(message.toString())
    }

    private fun showSuccessMessage(message: String) {
        sceneView.showSuccessMessage(message)
    }

    private fun getPathfinder(): PathfinderScanner? {
        val scanner = deviceManager.connectedScanner
        return if (scanner != null && scanner is PathfinderScanner)
            scanner
        else
            null
    }

    override fun getName(): String = MainSceneCoordinator.SceneKey.PATHFINDER_CONFIG

    object Factory {

        fun create(
                container: SceneContainer,
                deviceManager: DeviceManager,
                onExitListener: OnClickReference
        ): PathfinderConfigPresenter {

            val scanningSceneView = PathfinderConfigView(container.getContainerView())

            val presenter = PathfinderConfigPresenter(
                    scanningSceneView, deviceManager, onExitListener)

            scanningSceneView.setPresenter(presenter)

            return presenter
        }
    }

}