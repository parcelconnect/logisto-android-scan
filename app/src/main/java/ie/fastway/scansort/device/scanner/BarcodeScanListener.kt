package ie.fastway.scansort.device.scanner

/**
 * Listens for barcode values generated by [KeyboardKeyEventToBarcodeConverter].
 */
typealias BarcodeScanListener = (String) -> Unit