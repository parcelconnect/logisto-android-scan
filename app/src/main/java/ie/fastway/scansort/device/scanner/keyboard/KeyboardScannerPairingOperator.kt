package ie.fastway.scansort.device.scanner.keyboard

import android.bluetooth.BluetoothDevice
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.pairing.PairingOperator
import ie.fastway.scansort.device.pairing.ScannerPairingListener
import ie.fastway.scansort.device.printer.PrinterPairingListener
import ie.fastway.scansort.device.scanner.KeyboardScanner
import ie.fastway.scansort.device.scanner.ScanningDevice
import ie.fastway.scansort.device.symbol.SymbolScanner
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber

/**
 * Pairs with keyboard-type scanners.
 */
class KeyboardScannerPairingOperator : PairingOperator {

    private var pendingBondDevice: ConnectableDevice? = null
    private var pendingBondListener: ScannerPairingListener? = null

    override fun canPairWith(bluetoothDevice: ConnectableDevice): Boolean {
        return (bluetoothDevice.getBluetoothClass() ==
                ConnectableDevice.BluetoothProfile.PERIPHERAL_KEYBOARD)
    }

    override fun pairWith(bluetoothDevice: ConnectableDevice, scannerPairingListener: ScannerPairingListener, printerPairingListener: PrinterPairingListener?) {
        if (LogConfig.KEYBOARD_SCANNER) {
            Timber.d("Attempting to pair with Symbol device: $bluetoothDevice")
        }

        if (bluetoothDevice.getBondState().isConnected) {
            if (LogConfig.KEYBOARD_SCANNER) {
                Timber.d("Already paired with Symbol scanner!")
            }

            // The device is already paired, so we can invoke the callback immediately.
            val symbolScanner = SymbolScanner(bluetoothDevice)
            scannerPairingListener.onScannerPaired(symbolScanner)
        }
        else {
            if (LogConfig.KEYBOARD_SCANNER) {
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
            changedDevice: BluetoothDevice,
            currentBondState: Int,
            previousBondState: Int
    ) {

        if (currentBondState == BluetoothDevice.BOND_BONDED) {
            synchronized(this) {
                if (pendingBondDevice?.getBluetoothAddress() == changedDevice?.address) {
                    val keyboardScanner = KeyboardScanner(pendingBondDevice!!)
                    pendingBondListener?.onScannerPaired(keyboardScanner)
                }
            }
        }
    }

    override fun disconnectFrom(scanningDevice: ScanningDevice) {
        if(LogConfig.KEYBOARD_SCANNER) {
            Timber.w("TODO: `disconnectFrom(ScanningDevice)` in KeyboardScanner not implemented.")
        }
    }
}
