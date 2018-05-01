package ie.fastway.scansort.device.scanner

import android.os.SystemClock
import android.view.KeyEvent
import ie.fastway.scansort.logging.LogConfig
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Converts [KeyEvent]s issued via the [KeyEvent.Callback] to a barcode [ScanEvent]
 * to be consumed by listeners.
 *
 * Bluetooth scanners that do not have their own SDKs typically behave as HID keyboard scanners,
 * and when the user scans a barcode the data from the barcode is converted to a series of
 * [KeyEvent]s in the main Activity. The Activity is notified of these [KeyEvent]s in the order
 * in which they occur in the barcode, and they occur together within a very short space of time.
 *
 * Rather than have every keyboard (HID) type scanner implement the [KeyEvent.Callback]
 * interface, and then have that registered with the main Activity, this
 * [KeyboardKeyEventToBarcodeConverter] listens for [KeyEvent]s and debounces the incoming
 * content into a single barcode scan.
 *
 * NOTE: This is NOT case-sensitive; All alphabetical characters will be reported as upper-case.
 */
class KeyboardKeyEventToBarcodeConverter : KeyEvent.Callback {

    companion object {
        /** Max time between events that are in the same sequence */
        internal const val MAX_TIME_BETWEEN_EVENTS_MS = 80L

        /** Time between tasks to check if the sequence is finished */
        internal const val SCHEDULE_SEQUENCE_CHECK_MS = MAX_TIME_BETWEEN_EVENTS_MS + 40L
    }

    private var currentScanSeries: BarcodeScanSeries? = null
    var onBarcodeReadyCallback: BarcodeScanListener? = null

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        handleKeyEvent(keyCode, event)

        // We only observe the event, we don't consume it, so allow it to be
        // propagated down the chain.
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        handleKeyEvent(keyCode, event)

        return false
    }

    override fun onKeyMultiple(keyCode: Int, count: Int, event: KeyEvent?): Boolean {
        // Not implemented
        return false
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        // Not implemented.
        return false
    }

    private fun handleKeyEvent(keyCode: Int, event: KeyEvent) {
        synchronized(this) {
            val scanSeries = currentScanSeries;

            if (scanSeries == null || scanSeries.isDisposed) {
                if (LogConfig.KEYBOARD_SCANNER) {
                    Timber.d("Previous scanSeries is disposed or is null, creating new BarcodeScanSeries...")
                }

                currentScanSeries = BarcodeScanSeries(event);
            }
            else if (!scanSeries.isPermittedInSeries(event)) {

                if (LogConfig.KEYBOARD_SCANNER) {
                    Timber.d("New KeyEvent is not permitted in old ScanSeries. keyEvent=$event; scanSeries=$scanSeries;")
                }

                // A new device has begun sending KeyEvents, so the old series is
                // expired and we start a new one.
                scanSeries.expire()
                currentScanSeries = BarcodeScanSeries(event)
            }


            currentScanSeries?.addKeyEvent(event)
        }
    }

    /**
     * Collects a series of [KeyEvent]s that occur in a short space of time into a single
     * Barcode scan event.
     */
    private inner class BarcodeScanSeries(firstEvent: KeyEvent) {

        val keyEventSeries = LinkedList<KeyEvent>()

        val expectedDeviceId: Int

        val scheduledTask: Disposable

        var isExpired: Boolean = false
        var isDisposed: Boolean = false

        init {
            keyEventSeries.add(firstEvent)
            expectedDeviceId = firstEvent.deviceId

            // Start a scheduled task that periodically checks if the sequence has
            // stopped emitting values:
            scheduledTask = Schedulers.computation().createWorker()
                    .schedulePeriodically(
                            this::createBarcodeScanIfSequenceIsComplete,
                            SCHEDULE_SEQUENCE_CHECK_MS, SCHEDULE_SEQUENCE_CHECK_MS,
                            TimeUnit.MILLISECONDS
                    )
        }

        /**
         * Checks if it is possible for the given [keyEvent] to be part of this
         * barcode series.
         * Only [keyEvent]s which all came from the same keyboard device in quick succession
         * can be part of a barcode character series.
         */
        fun isPermittedInSeries(keyEvent: KeyEvent): Boolean {
            return expectedDeviceId == keyEvent.deviceId
        }

        fun addKeyEvent(keyEvent: KeyEvent) {
            keyEventSeries.add(keyEvent)
        }

        private fun createBarcodeScanIfSequenceIsComplete() {
            val currentUptime = SystemClock.uptimeMillis()

            val msSinceLastEvent = (currentUptime - keyEventSeries.last.eventTime)

            if (msSinceLastEvent > MAX_TIME_BETWEEN_EVENTS_MS) {
                dispose()
                createBarcodeScan()
            }
        }

        private fun createBarcodeScan() {
            val barcode = convertEventsToBarcode()

            if (LogConfig.SYMBOL) {
                Timber.d("Creating barcode scan. callback=$onBarcodeReadyCallback")
            }

            onBarcodeReadyCallback?.invoke(barcode)
        }

        private fun convertEventsToBarcode(): String {
            val barcodeSb = StringBuilder()
            keyEventSeries.forEach {

                if (it.action == KeyEvent.ACTION_UP) {

                    val unicodeCharacter = it.keyCharacterMap.get(it.keyCode, it.metaState).toChar()

                    if (isValidBarcodeCharacter(unicodeCharacter)) {
                        val keyValue = unicodeCharacter.toString()

                        if (LogConfig.SYMBOL) {
                            Timber.d("Appending KeyEvent with UP action; keyEvent=$it; keyCharacterMap=${it.keyCharacterMap}; keyCode=${it.keyCode}; unicode=$unicodeCharacter; keyValue=$keyValue; metaState=${it.metaState}")
                        }

                        barcodeSb.append(keyValue.toUpperCase(Locale.UK))
                    }

                }
            }
            return barcodeSb.toString()
        }

        fun expire() {
            isExpired = true
            dispose()
        }

        private fun dispose() {
            isDisposed = true
            scheduledTask.dispose()
        }

        private fun isValidBarcodeCharacter(unicodeCharacter: Char): Boolean =
                unicodeCharacter.isDefined() &&
                        (unicodeCharacter.isDigit() ||
                                unicodeCharacter.isUpperCase() ||
                                unicodeCharacter.isLowerCase() ||
                                unicodeCharacter.isWhitespace())


    }
}