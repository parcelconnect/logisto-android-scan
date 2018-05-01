package ie.fastway.scansort.scanning.view

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import ie.fastway.scansort.R
import ie.fastway.scansort.scene.BaseSceneView
import ie.fastway.scansort.shipment.Shipment

/**
 * ViewHolder for showing the scannign scene.
 */
class ScanningSceneView(sceneRoot: ViewGroup) : BaseSceneView(sceneRoot) {

    lateinit var deviceNameTx: TextView
        private set

    lateinit var deviceConfigButton: Button
        private set

    lateinit var lastScanTx: TextView
        private set

    lateinit var errorMessageTx: TextView
        private set

    lateinit var cf: TextView
        private set

    lateinit var rf: TextView
        private set

    private lateinit var deliveryAddress: TextView
        private set

    lateinit var mockScanButton: Button
        private set

    override fun bindViews() {

        with(sceneRoot) {
            deviceNameTx = findViewById(R.id.scanningScene_connectedDevice_deviceId)
            deviceConfigButton = findViewById(R.id.scanningScene_connectedDevice_configureButton)
            lastScanTx = findViewById(R.id.scanningScene_latestScanEventTx)
            cf = findViewById(R.id.scanningScene_cf)
            rf = findViewById(R.id.scanningScene_rf)
            deliveryAddress = findViewById(R.id.scanningScene_address)
            errorMessageTx = findViewById(R.id.scanningScene_errorMessage)
            mockScanButton = findViewById(R.id.scanningScene_manualScanButton)
        }

        showError(null)
    }

    fun showBarcode(barcodeValue: String?) {
        lastScanTx.text = barcodeValue
        lastScanTx.visibility = View.VISIBLE
    }

    fun showMatchedShipment(matchedShipment: Shipment) {
        beginDelayedTransition()

        with(matchedShipment) {
            cf.text = courierFranchisee
            rf.text = regionalFranchisee
            deliveryAddress.text = address
        }
        setShipmentDetailsVisibility(View.VISIBLE)
        executeShowError(null)
    }

    private fun setShipmentDetailsVisibility(visibilityInt: Int) {
        cf.visibility = visibilityInt
        rf.visibility = visibilityInt
        deliveryAddress.visibility = visibilityInt
    }

    fun showError(errorContent: String?) {
        beginDelayedTransition()
        executeShowError(errorContent)
        setShipmentDetailsVisibility(View.GONE)
    }

    private fun executeShowError(errorContent: String?) {
        if (errorContent == null) {
            errorMessageTx.visibility = View.GONE
        }
        else {
            errorMessageTx.text = errorContent
            errorMessageTx.visibility = View.VISIBLE
        }
    }

    fun clearLastScan() {
        deviceNameTx.text = ""
        lastScanTx.text = ""
        errorMessageTx.text = ""
        deliveryAddress.text = ""
        cf.text = ""
        rf.text = ""

    }

    override fun getLayoutId(): Int = R.layout.scene_scanning

}