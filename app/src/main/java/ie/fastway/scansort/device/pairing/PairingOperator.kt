package ie.fastway.scansort.device.pairing

import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.printer.PrinterPairingListener
import ie.fastway.scansort.device.scanner.ScanningDevice

/**
 *
 */
interface PairingOperator {

    fun canPairWith(bluetoothDevice: ConnectableDevice): Boolean

    fun pairWith(bluetoothDevice: ConnectableDevice,
                 scannerPairingListener: ScannerPairingListener,
                 printerPairingListener: PrinterPairingListener? = null
    )

    fun disconnectFrom(scanningDevice: ScanningDevice)

}