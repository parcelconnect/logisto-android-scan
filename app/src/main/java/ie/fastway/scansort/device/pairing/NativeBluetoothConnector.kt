package ie.fastway.scansort.device.pairing

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber
import java.util.*


/**
 * A [BluetoothConnector] that uses the native Android [BluetoothManager]
 */
class NativeBluetoothConnector(
        private val context: Activity,
        private val realAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
) : BluetoothConnector {

    companion object {

        const val INTENT_BLUETOOTH_PERMISSION_REQUEST = 5973

        const val INTENT_ACTIVATE_BLUETOOTH = 19253

        /**
         * The ACCESS_COARSE_LOCATION permission is required to use Bluetooth on Android 6+.
         * This a runtime permission that the user must grant explicitly.
         */
        const val BLUETOOTH_RUNTIME_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    }

    override fun getBondedDevices(): Collection<ConnectableDevice> {
        val scanningDevices = LinkedList<ConnectableDevice>()
        realAdapter.bondedDevices.forEach {
            if (LogConfig.BLUETOOTH) {
                Timber.d("Found bonded device: ${deviceToString(it)}")
            }

            val device = convertDevice(it)
            scanningDevices.add(device)

        }
        return scanningDevices
    }

    private fun convertDevice(bluetoothDevice: BluetoothDevice)
            = ConnectableDevice.Factory.createFromBluetoothDevice(bluetoothDevice)

    override fun isDiscovering(): Boolean = realAdapter.isDiscovering

    override fun startDiscovery(): Boolean {
        if (isBluetoothEnabled()) {
            return realAdapter.startDiscovery()
        }
        else {
            ensureBluetoothIsAvailable()
            // TODO: Schedule a task to startDiscovery when bluetooth is activated.
            return false
        }
    }

    override fun cancelDiscovery() = realAdapter.cancelDiscovery()

    //----------------------------------------------------------------------------------------------
    // BLUETOOTH SETTINGS
    //----------------------------------------------------------------------------------------------

    /**
     * Checks to see if the device's Bluetooth is turned on.
     */
    fun isBluetoothEnabled(): Boolean = realAdapter.isEnabled

    fun ensureBluetoothIsAvailable() {
        activate()
    }

    override fun activate() {
        if (LogConfig.TEMP) {
            Timber.d("Activating Bluetooth...")
        }

        if (!realAdapter.isEnabled) {
            showChangeBluetoothStateActivity()
        }
    }

    override fun deactivate() {
        if (LogConfig.TEMP) {
            Timber.d("Deactivating Bluetooth...")
        }

        if (realAdapter.isEnabled) {
            try {
                realAdapter.disable()
            }
            catch (e: Exception) {
                Timber.e(e, "Could not disabled bluetooth.")
            }
        }
    }

    /**
     * Shows an activity that allows the user to turn Bluetooth on or off.
     *
     * Results will be posted back to the main activity via an ActivityResult,
     * but they will also be posted by default to any BoradcastReceiver registered
     * for the [BluetoothAdapter.ACTION_STATE_CHANGED] intent filter.
     */
    private fun showChangeBluetoothStateActivity() {
        val bluetoothStateIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

        // Start an Activity to turn on Bluetooth:
        if (context is Activity) {
            context.startActivityForResult(bluetoothStateIntent, INTENT_ACTIVATE_BLUETOOTH)
        }
        else {
            context.startActivity(bluetoothStateIntent)
        }
    }

    //----------------------------------------------------------------------------------------------
    // PERMISSIONS
    //----------------------------------------------------------------------------------------------

    override fun requestBluetoothPermissions() {

        if (areBluetoothPermissionsGranted()) {
            if (LogConfig.BLUETOOTH) {
                Timber.i("Requesting Bluetooth permissions from the user...")
            }

            ActivityCompat.requestPermissions(
                    context,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    INTENT_BLUETOOTH_PERMISSION_REQUEST
            )
        }

    }

    internal fun areBluetoothPermissionsGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(context, BLUETOOTH_RUNTIME_PERMISSION)
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun getBluetoothStatus(): BluetoothStatus {
        return BluetoothStatus(
                isTurnedOn = isBluetoothEnabled(),
                isPermissionsGranted = areBluetoothPermissionsGranted())
    }

    //----------------------------------------------------------------------------------------------

    private fun deviceToString(bluetoothDevice: BluetoothDevice?): String {
        return if (bluetoothDevice == null) {
            "null"
        }
        else {
            "name=${bluetoothDevice.name}; " +
                    "address=${bluetoothDevice.address}; " +
                    "bluetoothClass=${bluetoothDevice.bluetoothClass}; " +
                    "bondState=${bluetoothDevice.bondState}; " +
                    "type=${bluetoothDevice.type}; "
        }

    }


}