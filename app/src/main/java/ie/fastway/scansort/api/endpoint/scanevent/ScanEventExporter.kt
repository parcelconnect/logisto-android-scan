package ie.fastway.scansort.api.endpoint.scanevent

import com.squareup.otto.Subscribe
import com.tubbert.powdroid.util.Syncer
import ie.fastway.scansort.device.scanner.ScanResult
import ie.fastway.scansort.logging.LogConfig
import ie.logistio.paloma.RetrofitHelper
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Exports [ScanEventRequestJson]s via the [ScanEventLogApi].
 */
class ScanEventExporter(private val scanEventLogApi: ScanEventLogApi) {

    companion object {
        const val INITIAL_DELAY_MS = 37 * 1000L
        const val SCHEDULE_PERIOD = 33 * 1000L
    }

    private val pendingScanEvents = ArrayList<ScanResult>()
    private val scanResultConverter = ScanResultConverter()
    private var scheduler: Disposable? = null

    fun startExportSchedule(
            initialDelayMs: Long = INITIAL_DELAY_MS,
            schedulePeriodMs: Long = SCHEDULE_PERIOD
    ) {
        stopExportSchedule()

        if (LogConfig.SCAN_EVENT_API) {
            Timber.v("Setting up export scheduler.")
        }

        scheduler = Schedulers.io().createWorker()
                .schedulePeriodically(
                        this::executeExportPendingEvents,
                        initialDelayMs, schedulePeriodMs, TimeUnit.MILLISECONDS)
    }

    fun stopExportSchedule() {
        scheduler?.dispose()
    }

    @Subscribe
    fun onScanResult(scanResult: ScanResult) {
        if (LogConfig.SCAN_EVENT_API) {
            Timber.v("Received ScanResult=$scanResult")
        }

        pendingScanEvents.add(scanResult)
    }

    private fun executeExportPendingEvents() {
        if (LogConfig.SCAN_EVENT_API) {
            Timber.v("executeExportPendingEvents")
        }

        Syncer.doubleCheckedLock(pendingScanEvents, pendingScanEvents::isNotEmpty) {
            if (LogConfig.SCAN_EVENT_API) {
                Timber.v("Triggering EventExportTask.")
            }
            val exportTask = EventExportTask()
            pendingScanEvents.clear()
            exportTask.execute()
        }

    }

    inner class EventExportTask : Callback<ScanEventResponseJson> {

        private val eventsToExport: List<ScanResult>

        init {
            // We make an immutable copy of the current pending events.
            eventsToExport = ArrayList(pendingScanEvents)
        }

        fun execute() {
            val requestJson = ScanEventRequestJson()
            requestJson.scanEvents = scanResultConverter.convertAll(eventsToExport);
            if (LogConfig.SCAN_EVENT_API) {
                Timber.v("Enqueuing export request. requestSize=${eventsToExport.size}")
            }

            scanEventLogApi.postScanEvents(requestJson)
                    .enqueue(this)

        }

        override fun onResponse(
                call: Call<ScanEventResponseJson>, response: Response<ScanEventResponseJson>) {
            if (response.isSuccessful) {
                if (LogConfig.SCAN_EVENT_API) {
                    Timber.v("Successfully exported ${eventsToExport.size} events.");
                }
            }
            else {
                if (LogConfig.SCAN_EVENT_API) {
                    Timber.e("Unsuccessful ScanEventLog API response: ${response.code()}; " +
                            "RequestSize = ${eventsToExport.size}")
                }
            }
        }

        override fun onFailure(call: Call<ScanEventResponseJson>, t: Throwable) {
            if (LogConfig.SCAN_EVENT_API) {
                Timber.e(t, "ScanEventLogs API error.")
            }

            if (RetrofitHelper.isNetworkError(t)) {
                // Add the events we tried to export back to the pending queue.
                pendingScanEvents.addAll(eventsToExport)
            }
        }

    }

}