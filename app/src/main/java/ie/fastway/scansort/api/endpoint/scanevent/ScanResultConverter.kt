package ie.fastway.scansort.api.endpoint.scanevent

import ie.fastway.scansort.device.scanner.ScanResult
import ie.logistio.equinox.Equinox
import java.util.*

/**
 * Converts [ScanResult] to [ScanEventRequestJson].
 */
internal class ScanResultConverter {

    fun convertAll(eventsToConvert: List<ScanResult>): List<ScanEventRequestJson.ScanEventLogJson> {
        return eventsToConvert.mapTo(LinkedList()) { convertToJson(it) }
    }

    fun convertToJson(eventToConvert: ScanResult): ScanEventRequestJson.ScanEventLogJson {
        val scanEventJson = ScanEventRequestJson.ScanEventLogJson()

        with(eventToConvert) {
            scanEventJson.shipmentId = matchedShipment?.id

            scanEventJson.result = ScanEventRequestJson.ResultJson();
            scanEventJson.result.errorMessage = errorMessage

            scanEventJson.eventAt = Equinox.convertInstantToTimestamp(scanEvent.timestamp)

            scanEventJson.deviceScannerSerialNumber = scanEvent.scanningDevice.getDeviceId()

            scanEventJson.barcodeValue = scanEvent.scannedValue

            scanEventJson.barcodeType = scanEvent.barcodeType.name
        }

        return scanEventJson
    }

}