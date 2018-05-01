package ie.fastway.scansort.device.averydennison

import avd.api.core.IDevice
import avd.api.core.IListenerError
import ie.fastway.scansort.lifecycle.AppSessionProvider
import ie.fastway.scansort.logging.LogConfig
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Monitors the connection between the app and the Pathfinder and re-connects
 * with it if needed.
 */
class AvdConnectionMonitor(
        val pathfinder: PathfinderDevice
) : IListenerError {

    companion object {
        const val REFRESH_PERIOD_MS = 10 * 100L;
    }

    var scheduledMonitorTask: Disposable? = null

    public fun start() {

        pathfinder.getConnection()?.addListenerConnectionError(this)

         this.scheduledMonitorTask = AppSessionProvider.createScheduledTask(
                waitPeriodMs = REFRESH_PERIOD_MS,
                subscribeScheduler = Schedulers.io(),
                task = this::ensureDeviceIsReachable
        );
    }

    private fun ensureDeviceIsReachable() {

        if (LogConfig.AVD_CONFIG && LogConfig.TEMP) {
            Timber.d("Checking if Pathfinder is reachable...")
        }


    }

    override fun onErrorReceived(errorText: String?, errorCode: Int, device: IDevice?) {

    }


}