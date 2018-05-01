package ie.fastway.scansort.device.scanner

import ie.fastway.scansort.shipment.Shipment

/**
 * The result of a [ScanEvent].
 *
 * The [scanEvent] data is passed to a process that handles the incoming
 * scan data and finds a matching shipment for that data, or else
 * returns an error message if it found a problem with the scan event.
 *
 * [matchedShipment] is null if no shipment was found for the [scanEvent].
 */
data class ScanResult(
        val scanEvent: ScanEvent,
        val matchedShipment: Shipment?,
        val errorMessage: String?
)