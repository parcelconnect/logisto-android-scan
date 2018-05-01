package ie.fastway.scansort.device.averydennison

import avd.api.core.IConnection
import ie.fastway.scansort.device.printer.LabelPrinter
import ie.fastway.scansort.device.scanner.ScanningDevice

/**
 * AveryDennison Pathfinder printer/scanner.
 */
class PathfinderDevice(
        val pathfinderScanner: PathfinderScanner,
        val pathfinderPrinter: PathfinderPrinter
) : ScanningDevice by pathfinderScanner,
        LabelPrinter by pathfinderPrinter {

    fun getConnection(): IConnection? = pathfinderScanner.avdDevice.connection

}