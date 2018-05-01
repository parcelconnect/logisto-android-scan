package ie.fastway.scansort.device.view

import android.support.annotation.StringRes
import android.support.transition.TransitionManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.tubbert.powdroid.ui.OnClickReference
import com.tubbert.powdroid.ui.ResourceHelper.setBackgroundColorRes
import ie.fastway.scansort.R
import ie.fastway.scansort.device.pairing.BluetoothStatus
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.scene.BaseSceneView
import timber.log.Timber

/**
 *
 */
class DevicePairingSceneView(sceneRoot: ViewGroup) : BaseSceneView(sceneRoot) {

    var recyclerView: RecyclerView? = null

    private lateinit var deviceSearchButton: Button
    var deviceSearchClickListener: (() -> Unit)? = null

    lateinit private var discoverySpinner: ProgressBar

    private lateinit var discoveryModeContainer: ViewGroup

    private lateinit var bluetoothMonitorContainer: ViewGroup
    private lateinit var bluetoothMessage: TextView
    private lateinit var bluetoothSwitch: SwitchCompat

    /**
     * Controls whether or not we forward events to [onBluetoothStatusChangeListener].
     * Used to prevent programmatic changes from triggering the listener, as it should be
     * alerted only when a manual event occurs.
     */
    private var enableCheckChangeForwarding = true

    var onBluetoothStatusChangeListener: ((isTurnedOn: Boolean) -> Unit)? = null
    var launchPermissionRequestCallback: OnClickReference = null


    override fun bindViews() {
        with(getRootView()) {
            recyclerView = findViewById(R.id.devicePairing_recycler)
            deviceSearchButton = findViewById(R.id.devicePairing_searchForDevicesButton)

            discoverySpinner = findViewById(R.id.devicePairing_discoverySpinner)
            discoveryModeContainer = findViewById(R.id.devicePairing_discoveryActiveContainer)

            bluetoothMonitorContainer = findViewById(R.id.devicePairing_bluetoothMonitor_container)
            bluetoothMessage = findViewById(R.id.devicePairing_bluetoothMonitor_message)
            bluetoothSwitch = findViewById(R.id.devicePairing_bluetoothMonitor_bluetoothToggle)
        }

        deviceSearchButton.setOnClickListener { deviceSearchClickListener?.invoke() }

        bluetoothSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (LogConfig.BLUETOOTH && LogConfig.TEMP) {
                Timber.d("bluetoothSwitch.setOnCheckedChangeListener; isChecked=$isChecked;")
            }

            if (enableCheckChangeForwarding) {
                onBluetoothStatusChangeListener?.invoke(isChecked)
            }
        }

        showBluetoothStatus(BluetoothStatus(false, false));
    }

    fun showModeDiscoveryInProgress() {
        TransitionManager.beginDelayedTransition(sceneRoot)

        deviceSearchButton.visibility = View.GONE

        discoverySpinner.visibility = View.VISIBLE
        discoverySpinner.isIndeterminate = true
        discoveryModeContainer.visibility = View.VISIBLE
    }

    fun showModeReadyToDiscover() {
        TransitionManager.beginDelayedTransition(sceneRoot)

        deviceSearchButton.visibility = View.VISIBLE

        discoverySpinner.isIndeterminate = false
        discoverySpinner.visibility = View.GONE
        discoveryModeContainer.visibility = View.GONE
    }

    fun showBluetoothStatus(bluetoothStatus: BluetoothStatus) {

        val isStatusOk = bluetoothStatus.isStatusOk

        val bgColourRes = if (isStatusOk) {
            R.color.green_ok_tp
        }
        else {
            R.color.red_error_tp
        }

        bluetoothMonitorContainer.setBackgroundColorRes(bgColourRes)

        @StringRes var messageRes: Int
        var toggleVisibility: Int
        var permissionsButtonVisibility: Int
        val isToggleChecked = bluetoothStatus.isTurnedOn

        if (!bluetoothStatus.isTurnedOn) {
            messageRes = R.string.bluetooth_is_turned_off
            toggleVisibility = View.VISIBLE
            permissionsButtonVisibility = View.GONE
        }
        else if (false == bluetoothStatus.isPermissionsGranted) {
            messageRes = R.string.bluetooth_permissions_required
            toggleVisibility = View.GONE
            permissionsButtonVisibility = View.VISIBLE
        }
        else {
            messageRes = R.string.bluetooth_status_ok
            toggleVisibility = View.VISIBLE
            permissionsButtonVisibility = View.GONE
        }

        bluetoothMessage.setText(messageRes)
        bluetoothSwitch.visibility = toggleVisibility

        // We disable checkChange forwarding so that this change doesn't get published,
        // since it is not a change that is being triggered by the user pressing the button.
        enableCheckChangeForwarding = false
        bluetoothSwitch.isChecked = isToggleChecked
        enableCheckChangeForwarding = true

    }


    override fun getLayoutId() = R.layout.scene_device_pairing

}