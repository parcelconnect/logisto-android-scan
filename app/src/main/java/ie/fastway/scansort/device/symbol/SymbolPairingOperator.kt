package ie.fastway.scansort.device.symbol

import android.bluetooth.BluetoothDevice
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.pairing.PairingOperator
import ie.fastway.scansort.device.pairing.ScannerPairingListener
import ie.fastway.scansort.device.printer.PrinterPairingListener
import ie.fastway.scansort.device.scanner.ScanningDevice
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber

/**
 *
 */
class SymbolPairingOperator() : PairingOperator {

    // TODO: Use [KeyboardScannerPairingOperator] instead.

    private var pendingBondDevice: ConnectableDevice? = null
    private var pendingBondListener: ScannerPairingListener? = null

    companion object {
        const val PREFIX_CS4070 = "CS4070"
    }

    override fun canPairWith(bluetoothDevice: ConnectableDevice): Boolean {
        val canPair = isCs4070(bluetoothDevice)
        return canPair
    }

    /**
     * Checks if the scanner is a Symbol CS4070 device.
     */
    private fun isCs4070(device: ConnectableDevice?): Boolean {
        return device?.getBluetoothName() != null &&
                device.getBluetoothName().startsWith(PREFIX_CS4070)
    }

    override fun pairWith(
            bluetoothDevice: ConnectableDevice,
            scannerPairingListener: ScannerPairingListener,
            printerPairingListener: PrinterPairingListener?
    ) {

        if (LogConfig.SYMBOL) {
            Timber.d("Attempting to pair with Symbol device: $bluetoothDevice")
        }

        if (bluetoothDevice.getBondState().isConnected) {
            if (LogConfig.SYMBOL) {
                Timber.d("Already paired with Symbol scanner!")
            }

            // The device is already paired, so we can invoke the callback immediately.
            val symbolScanner = SymbolScanner(bluetoothDevice)
            scannerPairingListener.onScannerPaired(symbolScanner)
        }
        else {
            if (LogConfig.SYMBOL) {
                Timber.d("Scanner not bonded, will attempt to create new bond.")
            }

            synchronized(this) {
                pendingBondDevice = bluetoothDevice
                pendingBondListener = scannerPairingListener
                bluetoothDevice.attemptToBond()
            }
        }
    }

    fun onBondStateChanged(
            bluetoothDevice: BluetoothDevice,
            currentBondState: Int,
            previousBondState: Int
    ) {
        if (currentBondState == BluetoothDevice.BOND_BONDED) {
            synchronized(this) {
                if (pendingBondDevice?.getBluetoothAddress() == bluetoothDevice?.address) {
                    val symbolScanner = SymbolScanner(pendingBondDevice!!)
                    pendingBondListener?.onScannerPaired(symbolScanner)
                }
            }
        }
    }

    override fun disconnectFrom(scanningDevice: ScanningDevice) {
        // scanningDevice.getBluetoothDetails().attemptToBond()
    }
}