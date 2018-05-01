package ie.fastway.scansort.device.scanner

import avd.api.core.BarcodeType
import org.threeten.bp.Instant

/**
 * Represents an event that occurs when the user scans a barcode.
 */
data class ScanEvent(
        val scannedValue: String,
        val barcodeType: BarcodeType,
        val scanningDevice: ScanningDevice,
        val timestamp: Instant
)