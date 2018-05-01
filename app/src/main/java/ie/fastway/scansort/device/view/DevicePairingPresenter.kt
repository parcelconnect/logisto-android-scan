package ie.fastway.scansort.device.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.squareup.otto.Subscribe
import ie.fastway.scansort.device.DeviceManager
import ie.fastway.scansort.device.pairing.BluetoothStatus
import ie.fastway.scansort.lifecycle.EventPublisher
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.scene.BaseScenePresenter
import ie.fastway.scansort.scene.MainSceneCoordinator
import timber.log.Timber

/**
 * Presents a scene for the user to select a Bluetooth device to pair with.
 */
class DevicePairingPresenter(
        override val sceneView: DevicePairingSceneView,
        private val deviceManager: DeviceManager,
        private val eventBus: EventPublisher
) : BaseScenePresenter(sceneView) {

    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var recyclerAdapter: ConnectableDeviceRecyclerAdapter

    override fun onEnterScene() {

        if (LogConfig.SCENES) {
            Timber.d("DevicePairingPresenter; onEnterScene")
        }

        setupRecyclerView()

        showModeReadyToDiscover()

        sceneView.deviceSearchClickListener = {
            eventBus.postOnUiThread(DeviceManager.DeviceDiscoveryEvent
                    .OnUserRequestBeginDiscovery)
        }

        sceneView.onBluetoothStatusChangeListener = { isTurnedOn ->
            if (LogConfig.BLUETOOTH) {
                Timber.d("onBluetoothStatusChangeListener; isTurnedOn=$isTurnedOn;")
            }
            eventBus.postOnUiThread(DeviceManager.DeviceDiscoveryEvent
                    .OnUserRequestSetBluetoothState(isTurnedOn))
        }

        recyclerAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        // The LayoutManager and the adapter must be re-created every time the
        // scene is shown, because the RecyclerView appears to be re-inflated
        // every time it is shown.

        layoutManager = LinearLayoutManager(sceneView.getRootView().context)
        sceneView.recyclerView?.layoutManager = layoutManager

        recyclerAdapter = ConnectableDeviceRecyclerAdapter(
                deviceManager.connectableDeviceRegistry,
                deviceManager.deviceConnector
        )
        sceneView.recyclerView?.adapter = recyclerAdapter
    }

    @Subscribe
    fun onDeviceDiscoveryEvent(discoveryEvent: DeviceManager.DeviceDiscoveryEvent) {
        /*
        | WARNING: This class posts `DeviceDiscoveryEvent.OnUserRequestBeginDiscovery`
        | when the discovery button is clicked, so be careful not to respond to it
        | twice here.
        */

        if (LogConfig.EVENT_BUS) {
            Timber.d("onDeviceDiscoveryEvent: $discoveryEvent")
        }

        if (sceneView.isSetUp()) {
            when (discoveryEvent) {
                is DeviceManager.DeviceDiscoveryEvent.OnDiscoveryStarted ->
                    showModeDiscoveryInProgress()

                is DeviceManager.DeviceDiscoveryEvent.OnDiscoveryFinished ->
                    showModeReadyToDiscover()
            }
        }

    }


    private fun showModeReadyToDiscover() {
        sceneView.showModeReadyToDiscover()
    }

    private fun showModeDiscoveryInProgress() {
        sceneView.showModeDiscoveryInProgress()
    }

    @Subscribe
    fun onBluetoothStatusEvent(bluetoothStatus: BluetoothStatus) {
        if (LogConfig.TEMP) {
            Timber.d("onBluetoothStatusEvent; bluetoothStatus=$bluetoothStatus;")
        }
        if (sceneView.isSetUp()) {
            sceneView.showBluetoothStatus(bluetoothStatus)
        }
    }

    override fun getName() = MainSceneCoordinator.SceneKey.DEVICE_PAIRING

}