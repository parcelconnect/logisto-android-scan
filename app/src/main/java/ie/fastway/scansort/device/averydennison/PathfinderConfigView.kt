package ie.fastway.scansort.device.averydennison

import android.support.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import avd.api.core.ScanMode
import com.tubbert.powdroid.ui.OnClickReference
import ie.fastway.scansort.R
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.scene.BaseSceneView

/**
 *
 */
class PathfinderConfigView(sceneRoot: ViewGroup) : BaseSceneView(sceneRoot) {

    lateinit var deviceIdTx: TextView
        private set
    lateinit var bluetoothAddressTx: TextView
        private set
    lateinit var scanModeRadioGroup: RadioGroup
        private set
    lateinit var scanModeTrigger: RadioButton
        private set
    lateinit var scanModeContinuous: RadioButton
        private set
    lateinit var scanModeCompatible: RadioButton
        private set

    private lateinit var submitButton: Button
    var onSubmitListener: OnClickReference = null

    private lateinit var resetConfigButton: Button
    var onConfigResetListener: OnClickReference = null

    private lateinit var disconnectButton: Button
    var onDisconnectClickListener: OnClickReference = null

    private lateinit var exitButton: Button
    var onExitClickListener: OnClickReference = null

    private lateinit var errorMessageTx: TextView

    private lateinit var successMessageTx: TextView

    override fun bindViews() {
        with(sceneRoot) {
            deviceIdTx = findViewById(R.id.deviceConfig_deviceId)
            bluetoothAddressTx = findViewById(R.id.deviceConfig_bluetoothAddress)

            scanModeRadioGroup = findViewById(R.id.deviceConfig_scanMode_radioGroup)
            scanModeTrigger = findViewById(R.id.deviceConfig_scanMode_trigger)
            scanModeContinuous = findViewById(R.id.deviceConfig_scanMode_continuous)
            scanModeCompatible = findViewById(R.id.deviceConfig_scanMode_compatible)

            submitButton = findViewById(R.id.deviceConfig_submit)
            resetConfigButton = findViewById(R.id.deviceConfig_resetToDefault)

            errorMessageTx = findViewById(R.id.deviceConfig_errorMessage)
            successMessageTx = findViewById(R.id.deviceConfig_successMessage)
            exitButton = findViewById(R.id.deviceConfig_exitButton)
            disconnectButton = findViewById(R.id.deviceConfig_disconnectButton)
        }

        showErrorMessage("")
        showSuccessMessage("")

        submitButton.setOnClickListener { onSubmitListener?.invoke() }
        resetConfigButton.setOnClickListener { onConfigResetListener?.invoke() }
        exitButton.setOnClickListener { onExitClickListener?.invoke() }
        disconnectButton.setOnClickListener { onDisconnectClickListener?.invoke() }

    }

    fun clear() {
        if (isSetUp()) {
            deviceIdTx.text = ""
            bluetoothAddressTx.text = ""

            scanModeRadioGroup.clearCheck()

            errorMessageTx.visibility = View.GONE
            successMessageTx.visibility = View.GONE
        }
    }

    fun showDevice(connectableDevice: ConnectableDevice) {
        if (isSetUp()) {
            deviceIdTx.text = connectableDevice.getBluetoothName()
            bluetoothAddressTx.text = connectableDevice.getBluetoothAddress()
        }
    }

    fun showErrorMessage(error: String) {
        TransitionManager.beginDelayedTransition(sceneRoot)

        successMessageTx.visibility = View.GONE

        if (error.isBlank()) {
            errorMessageTx.visibility = View.GONE
        }
        else {
            errorMessageTx.text = error
            errorMessageTx.visibility = View.VISIBLE
        }
    }

    fun showSuccessMessage(message: String) {
        TransitionManager.beginDelayedTransition(sceneRoot)

        errorMessageTx.visibility = View.GONE

        if (message.isBlank()) {
            successMessageTx.visibility = View.GONE
        }
        else {
            successMessageTx.text = message
            successMessageTx.visibility = View.VISIBLE
        }

    }


    fun setScanMode(scanMode: ScanMode) {
        val idToCheck = when (scanMode) {
            ScanMode.Momentary -> scanModeTrigger.id
            ScanMode.Continuous -> scanModeContinuous.id;
            ScanMode.Compatible -> scanModeCompatible.id;
        }

        if (idToCheck != null) {
            scanModeRadioGroup.check(idToCheck)
        }
    }

    fun getSelectedScanMode(): ScanMode {
        return when (scanModeRadioGroup.checkedRadioButtonId) {
            scanModeTrigger.id -> ScanMode.Momentary;
            scanModeContinuous.id -> ScanMode.Continuous;
            scanModeCompatible.id -> ScanMode.Compatible;
            else -> ScanMode.Momentary
        }
    }

    override fun getLayoutId(): Int = R.layout.scene_pathfinder_config
}