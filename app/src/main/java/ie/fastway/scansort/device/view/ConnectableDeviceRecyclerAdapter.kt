package ie.fastway.scansort.device.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.DeviceConnector
import ie.fastway.scansort.device.pairing.DiscoveryListener
import ie.fastway.scansort.device.pairing.PairingEvent
import ie.fastway.scansort.device.pairing.ScanningDeviceRegistry
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber


/**
 * Adapter for showing a RecyclerView list of devices that can be connected to.
 *
 * Shows [ConnectableDevice]s as CardViews, using the [ConnectableDeviceCard] ViewHolder.
 *
 */
class ConnectableDeviceRecyclerAdapter(
        private val deviceRegistry: ScanningDeviceRegistry,
        private val deviceConnector: DeviceConnector
) : RecyclerView.Adapter<ConnectableDeviceCard>(), DiscoveryListener {

    init {
        deviceRegistry.subscribe(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectableDeviceCard {
        // Create a new view:
        val rowView = LayoutInflater.from(parent.context)
                .inflate(ConnectableDeviceCard.LAYOUT_ID, parent, false)

        return ConnectableDeviceCard(rowView, this::onClickConnectDevice)
    }

    private fun onClickConnectDevice(clickedDevice: ConnectableDevice) {
        if (LogConfig.SCANNING_DEVICES) {
            Timber.i("onDeviceConnectClick: ${clickedDevice}")
        }

        deviceConnector.connectToDevice(clickedDevice)
    }

    override fun onPairingEvent(event: PairingEvent) {
        if(LogConfig.SCANNING_DEVICES) {
            Timber.d("onPairingEvent: $event")
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: ConnectableDeviceCard, position: Int) {
        viewHolder.bindDevice(deviceRegistry.getAtPosition(position)!!)
    }

    override fun getItemCount(): Int = deviceRegistry.size

}