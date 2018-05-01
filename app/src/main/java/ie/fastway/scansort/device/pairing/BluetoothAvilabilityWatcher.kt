package ie.fastway.scansort.device.pairing

import ie.fastway.scansort.lifecycle.AppSessionProvider
import ie.fastway.scansort.lifecycle.EventPublisher
import ie.fastway.scansort.logging.LogConfig
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Watches the native BluetothAdapter to make sure that Bluetooth remains switched on
 * and that all runtime permissions are granted.
 */
class BluetoothAvilabilityWatcher(
        val bluetoothConnector: BluetoothConnector,
        val eventBus: EventPublisher) {

    companion object {
        const val SCHEDULE_PERIOD_MS = 10 * 1000L
    }

    private var scheduledTask: Disposable? = null
    val bluetoothStatusListener: ((BluetoothStatus) -> Unit)? = null

    public fun startWatching() {
        scheduledTask = AppSessionProvider.createScheduledTask(
                waitPeriodMs = SCHEDULE_PERIOD_MS,
                subscribeScheduler = Schedulers.io(),
                task = this::checkBluetoothAvailability)
    }

    private fun checkBluetoothAvailability() {
        if (LogConfig.TEMP) {
            Timber.d("Executing checkBluetoothAvailability...")
        }
        val status = bluetoothConnector.getBluetoothStatus()

        eventBus.postOnUiThread(status)

        if (!status.isTurnedOn) {
            if (LogConfig.BLUETOOTH) {
                Timber.d("checkBluetoothAvailability; Bluetooth is not enabled.")
            }

            bluetoothConnector.activate()
        }

        if (status.isPermissionsGranted == false) {
            bluetoothConnector.requestBluetoothPermissions()
        }


    }


}