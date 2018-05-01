package ie.fastway.scansort.device.view

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.MaterialIcons
import ie.fastway.scansort.R
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.pairing.DeviceConnectionState

/**
 * ViewHolder for showing devices that can be connected to via Bluetooth.
 *
 */
class ConnectableDeviceCard(
        itemView: View,
        private val onConnectClickCallback: (ConnectableDevice) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private var boundDevice: ConnectableDevice? = null

    private val commonNameContainer: ViewGroup
    private val commonName: TextView
    private val bluetoothAddress: TextView
    private val serialNumber: TextView
    private val connectButton: Button
    private val bluetoothIcon: ImageView

    init {
        commonNameContainer = itemView.findViewById(R.id.connectableDevice_deviceInfo_commonName_container)
        commonName = itemView.findViewById(R.id.connectableDevice_deviceInfo_commonName)
        bluetoothAddress = itemView.findViewById(R.id.connectableDevice_deviceInfo_bluetoothAddress)
        serialNumber = itemView.findViewById(R.id.connectableDevice_deviceInfo_serialNumber)
        connectButton = itemView.findViewById(R.id.connectableDevice_bluetoothPairing_connectButton)
        // connectButton.setOnClickListener { boundDevice?.let(onConnectClickCallback) }

        bluetoothIcon = itemView.findViewById(R.id.connectableDevice_bluetoothPairing_stateIcon)
        showBluetoothStateIcon(DeviceConnectionState.noState())
    }

    fun bindDevice(device: ConnectableDevice) {

        boundDevice = device

        val deviceName = device.productName
        if (deviceName != null) {
            commonName.text = deviceName
            commonNameContainer.visibility = View.VISIBLE
        }
        else {
            commonNameContainer.visibility = View.GONE
        }

        bluetoothAddress.text = device.getBluetoothAddress()

        serialNumber.text = device.getBluetoothName()

        setupConnectButton()

        showBluetoothStateIcon(device.getBondState())

    }

    private fun setupConnectButton() {
        val currentDevice = boundDevice

        if (currentDevice == null) {
            connectButton.visibility = View.GONE
        }
        else if (currentDevice.getBondState().isConnecting) {
            connectButton.setText(R.string.bluetooth_connecting)
            connectButton.setOnClickListener { /* Do nothing */ }
            connectButton.visibility = View.VISIBLE
        }
        else {
            connectButton.setText(R.string.bluetooth_connect)
            connectButton.setOnClickListener { onConnectClickCallback(currentDevice) }
            connectButton.visibility = View.VISIBLE
        }
    }

    private fun showBluetoothStateIcon(connectionState: DeviceConnectionState) {
        val iconImage = when (connectionState.connectionStatus) {
            DeviceConnectionState.STATE_CONNECTED ->
                MaterialIcons.md_bluetooth_connected

            DeviceConnectionState.STATE_IS_CONNECTING ->
                MaterialIcons.md_bluetooth_searching

            DeviceConnectionState.STATE_NOT_CONNECTED ->
                MaterialIcons.md_bluetooth_disabled

            else -> null
        }

        if (iconImage != null) {
            val iconDrawable = IconDrawable(getContext(), iconImage)
                    .colorRes(R.color.colorPrimary)
                    .sizeRes(R.dimen.bluetoothConnectionState_iconSize)

            bluetoothIcon.setImageDrawable(iconDrawable)
            bluetoothIcon.visibility = View.VISIBLE
        }
        else {
            bluetoothIcon.visibility = View.GONE
        }

    }

    private fun getContext() = itemView.context


    companion object {
        @LayoutRes
        val LAYOUT_ID = R.layout.card_connectable_device;
    }


}